package com.petrolpark.destroy.block.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.block.CentrifugeBlock;
import com.petrolpark.destroy.block.display.MixtureContentsDisplaySource;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.PollutingBehaviour;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.GeniusFluidTankBehaviour;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class CentrifugeBlockEntity extends KineticBlockEntity implements IDirectionalOutputFluidBlockEntity {

    private static final Object centrifugationRecipeKey = new Object();
    private static final int TANK_CAPACITY = 1000;

    private SmartFluidTankBehaviour inputTank, denseOutputTank, lightOutputTank;
    protected LazyOptional<IFluidHandler> allFluidCapability;

    protected DestroyAdvancementBehaviour advancementBehaviour;
    protected PollutingBehaviour pollutingBehaviour;

    private Direction denseOutputTankFace;

    private int lubricationLevel;
    private static final int MAX_LUBRICATION_LEVEL = 10;

    public int timer;
    private CentrifugationRecipe lastRecipe;

    private boolean pondering; // Whether this Centrifuge is in a Ponder Scene

    public CentrifugeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        denseOutputTankFace = state.getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        lubricationLevel = 1;
        pondering = false;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::onFluidStackChanged);
        denseOutputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::onFluidStackChanged)
            .forbidInsertion();
        lightOutputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::onFluidStackChanged)
            .forbidInsertion();
        behaviours.addAll(List.of(inputTank, denseOutputTank, lightOutputTank));

        allFluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(inputTank.getCapability().orElse(null), denseOutputTank.getCapability().orElse(null), lightOutputTank.getCapability().orElse(null));
        });

        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);

        pollutingBehaviour = new PollutingBehaviour(this);
        behaviours.add(pollutingBehaviour);
    };

    /**
     * Attempts to rotate the Centrifuge so that it faces a new face which also has a Pipe. If no Pipe is available, just rotates it anyway.
     * @param shouldSwitch Whether the rotation should prioritise switching faces or staying on the current face
     * @return Whether the Centrifuge was rotated
     */
    @SuppressWarnings("null")
    public boolean attemptRotation(boolean shouldSwitch) {
        if (!hasLevel()) return false;
        if (getLevel().setBlock(getBlockPos(), getBlockState().setValue(CentrifugeBlock.DENSE_OUTPUT_FACE, refreshDirection(this, shouldSwitch ? denseOutputTankFace.getClockWise() : denseOutputTankFace, getDenseOutputTank(), true)), 6)) { // If the output Direction can be successfully changed
            denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
            notifyUpdate(); // Block State has changed
            return true;
        };
        return false;
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (!hasLevel()) return; // Don't do anything if we're not in a Level
        if (getSpeed() == 0) return; // Don't do anything without rotational power
        if (isTankFull(getDenseOutputTank()) || isTankFull(getLightOutputTank())) return; // Don't do anything if output is full
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (getLevel().isClientSide()) { // It thinks getLevel() can be null (it can't)
                spawnParticles();
                return;
            };
            if (timer <= 0) {
                process();
            };
            sendData();
            return;
        };
        if (inputTank.isEmpty()) return; // Don't do anything more if input Tank is empty

        if (lastRecipe == null || !lastRecipe.getRequiredFluid().test(getInputTank().getFluid())) { // If the Recipe has changed
            List<Recipe<?>> possibleRecipes = RecipeFinder.get(centrifugationRecipeKey, getLevel(), r -> r.getType() == DestroyRecipeTypes.CENTRIFUGATION.getType()).stream().filter(r -> {
                CentrifugationRecipe recipe = (CentrifugationRecipe) r;
                if (!recipe.getRequiredFluid().test(getInputTank().getFluid())) return false; // If there is insufficient input Fluid
                if (!canFitFluidInTank(recipe.getDenseOutputFluid(), getDenseOutputTank()) || !canFitFluidInTank(recipe.getLightOutputFluid(), getLightOutputTank())) return false; // If the outputs can't fit
                return true;
            }).collect(Collectors.toList());
            if (possibleRecipes.size() >= 1) {
                lastRecipe = (CentrifugationRecipe)possibleRecipes.get(0);
            } else { // If no recipe could be found
                lastRecipe = null;
            };
        };

        if (lastRecipe == null) {
            timer = 100; // If we have no Recipe, don't try checking again for another 100 ticks
        } else {
            timer = lastRecipe.getProcessingDuration();
        };

        sendData();
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        lubricationLevel = compound.getInt("Lubrication");
        timer = compound.getInt("Timer");
        getInputTank().readFromNBT(compound.getCompound("InputTank"));
        getDenseOutputTank().readFromNBT(compound.getCompound("DenseOutputTank"));
        getLightOutputTank().readFromNBT(compound.getCompound("LightOutputTank"));
        denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        super.read(compound, clientPacket);
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Lubrication", lubricationLevel);
        compound.putInt("Timer", timer);
        compound.put("InputTank", getInputTank().writeToNBT(new CompoundTag()));
        compound.put("DenseOutputTank", getDenseOutputTank().writeToNBT(new CompoundTag()));
        compound.put("LightOutputTank", getLightOutputTank().writeToNBT(new CompoundTag()));
        super.write(compound, clientPacket);
    };

    public int getProcessingSpeed() {
        float lubricationMultiplier = lubricationLevel / MAX_LUBRICATION_LEVEL;
        return Mth.clamp((int) Math.abs(getSpeed() * lubricationMultiplier / 16f), 1, 512);
    };

    public SmartFluidTank getInputTank() {
        return inputTank.getPrimaryHandler();
    };

    public SmartFluidTank getDenseOutputTank() {
        return denseOutputTank.getPrimaryHandler();
    };

    public SmartFluidTank getLightOutputTank() {
        return lightOutputTank.getPrimaryHandler();
    };

    public void process() {
        if (lastRecipe == null) { // If there is no Recipe
            if (DestroyFluids.isMixture(getInputTank().getFluid())) { // If there are Fluids to Centrifuge
                ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, getInputTank().getFluid().getOrCreateChildTag("Mixture"));
                if (mixture == null) return;
                if (!(DestroyFluids.isMixture(getDenseOutputTank().getFluid()) || getDenseOutputTank().isEmpty()) || !(DestroyFluids.isMixture(getLightOutputTank().getFluid()) || getLightOutputTank().isEmpty())) return; // Don't go any further if either output tank can't take Mixture
                int amount = IntStream.of(new int[]{getInputTank().getFluidAmount(), getDenseOutputTank().getSpace() * 2, getLightOutputTank().getSpace() * 2}).min().getAsInt(); // Determine how much can be processed
                if (amount == 0) return; // If either of the two output tanks can't fit anything at all, give up
                float totalVolume = amount / 1000f;

                List<Molecule> chargedMolecules = new ArrayList<>();
                List<Molecule> neutralMolecules = new ArrayList<>();
                for (Molecule molecule : mixture.getContents(false)) {
                    (molecule.getCharge() == 0 ? neutralMolecules : chargedMolecules).add(molecule);
                };

                Collections.sort(neutralMolecules, (m1, m2) -> {
                    return Float.compare(m1.getDensity(), m2.getDensity()); //TODO account for the fact that some things may be in the gas state
                });

                float volumeOfDenseMixture = 0f; // in B

                ReadOnlyMixture lightMixture = new ReadOnlyMixture();
                ReadOnlyMixture denseMixture = new ReadOnlyMixture();

                float totalMoles = 0f;
                for (Molecule molecule : neutralMolecules) {
                    totalMoles += mixture.getConcentrationOf(molecule) * totalVolume;
                };

                for (Molecule molecule : neutralMolecules) {
                    float moles = mixture.getConcentrationOf(molecule) * totalVolume;
                    float volume = totalVolume * moles / totalMoles; // Volume of this Molecule present in the original Mixture in Buckets
                    float volumeInDenseMixture = Math.min((totalVolume / 2f) - volumeOfDenseMixture, volume); // What volume of this Molecule gets put in the dense portion, in Buckets
                    volumeOfDenseMixture += volumeInDenseMixture; // Increase the amount of dense Fluid made
                    denseMixture.addMolecule(molecule, 2f * (moles * volumeInDenseMixture / volume) / totalVolume);
                    lightMixture.addMolecule(molecule, 2f * (moles * (volume - volumeInDenseMixture) / volume) / totalVolume);
                };

                ReadOnlyMixture ionMixture = denseMixture.getConcentrationOf(DestroyMolecules.WATER) > 0f || lightMixture.getConcentrationOf(DestroyMolecules.WATER) <= 0f ? denseMixture : lightMixture; // Stick charged molecules with water if there is any, or the dense mixture if not //TODO replace with consideration of dielectrics
                chargedMolecules.forEach(molecule -> ionMixture.addMolecule(molecule, 2 * mixture.getConcentrationOf(molecule)));

                // If we've got to this point, the Fluid can be succesfully processed
                getInputTank().drain(amount, FluidAction.EXECUTE);
                getDenseOutputTank().fill(MixtureFluid.of(amount / 2, denseMixture), FluidAction.EXECUTE);
                getLightOutputTank().fill(MixtureFluid.of(amount / 2, lightMixture), FluidAction.EXECUTE);
                advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.USE_CENTRIFUGE);
                notifyUpdate();
            } else { // If there is no Mixture to Centrifuge
                return;
            };
        } else { // If there is a Recipe
            if (!canFitFluidInTank(lastRecipe.getDenseOutputFluid(), getDenseOutputTank()) || !canFitFluidInTank(lastRecipe.getLightOutputFluid(), getLightOutputTank()) || hasFluidInTank(lastRecipe.getRequiredFluid(), getLightOutputTank())) return; // Ensure the Recipe can still be Processed
            getInputTank().drain(lastRecipe.getRequiredFluid().getRequiredAmount(), FluidAction.EXECUTE);
            getDenseOutputTank().fill(lastRecipe.getDenseOutputFluid(), FluidAction.EXECUTE);
            getLightOutputTank().fill(lastRecipe.getLightOutputFluid(), FluidAction.EXECUTE);
            advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.USE_CENTRIFUGE);
            notifyUpdate();
        };
    };

    @SuppressWarnings("null")
    public void spawnParticles() {
        FluidStack fluidStack = inputTank.getPrimaryHandler().getFluid();
        if (fluidStack.isEmpty() || !hasLevel()) return;

        RandomSource random = getLevel().getRandom(); // It thinks getLevel() might be null

        ParticleOptions data = FluidFX.getFluidParticle(fluidStack);
        float angle = random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.7f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), random, 1/ 128f);
        getLevel().addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z); // It thinks getLevel() might be null
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == Direction.UP) {
                return inputTank.getCapability().cast();
            } else if (side == Direction.DOWN) {
                return lightOutputTank.getCapability().cast();
            } else if (side == denseOutputTankFace || pondering) {
                return denseOutputTank.getCapability().cast();
            } else if (side == null) { // For the PollutingBehaviour, it needs all tanks
                return allFluidCapability.cast();
            };
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        allFluidCapability.invalidate();
    };

    public int getEachTankCapacity() {
        return TANK_CAPACITY;
    };

    private void onFluidStackChanged() {
        notifyUpdate();
    };

    /**
     * Let this Centrifuge know we're in a Ponder.
     * This makes it so the dense Fluid can be pulled from any side.
     */
    public void setPondering() {
        pondering = true;
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        // if (MAX_LUBRICATION_LEVEL != 0) {
        //     DestroyLang.translate("tooltip.centrifuge.lubrication")
        //         .style(ChatFormatting.WHITE)
        //         .space()
        //         .add(DestroyLang.barMeterComponent(lubricationLevel, MAX_LUBRICATION_LEVEL, Math.min(MAX_LUBRICATION_LEVEL, 20)))
        //         .forGoggles(tooltip);
        // };

        DestroyLang.fluidContainerInfoHeader(tooltip);
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.input_tank"), getInputTank());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.dense_output_tank"), getDenseOutputTank());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.light_output_tank"), getLightOutputTank());
        
        return true;
    };

    public static class CentrifugeDisplaySource extends MixtureContentsDisplaySource {

        private final Function<CentrifugeBlockEntity, SmartFluidTank> tankGetter;
        private final String tankId;

        private CentrifugeDisplaySource(String tankId, Function<CentrifugeBlockEntity, SmartFluidTank> tankGetter) {
            super(false);
            this.tankId = tankId;
            this.tankGetter = tankGetter;
        };

        @Override
        public FluidStack getFluidStack(DisplayLinkContext context) {
            if (context.getSourceBlockEntity() instanceof CentrifugeBlockEntity centrifuge) {
                return tankGetter.apply(centrifuge).getFluid();
            };
            return FluidStack.EMPTY;
        };

        @Override
        public Component getName() {
            return DestroyLang.translate("display_source.centrifuge."+tankId).component();
        };
    };
 
    public static CentrifugeDisplaySource INPUT_DISPLAY_SOURCE = new CentrifugeDisplaySource("input", CentrifugeBlockEntity::getInputTank);
    public static CentrifugeDisplaySource DENSE_OUTPUT_DISPLAY_SOURCE = new CentrifugeDisplaySource("dense_output", CentrifugeBlockEntity::getDenseOutputTank);
    public static CentrifugeDisplaySource LIGHT_OUTPIT_DISPLAY_SOURCE = new CentrifugeDisplaySource("light_output", CentrifugeBlockEntity::getLightOutputTank);

    
};
