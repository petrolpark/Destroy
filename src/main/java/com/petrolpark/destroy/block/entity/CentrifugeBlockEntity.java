package com.petrolpark.destroy.block.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.CentrifugeBlock;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.fluids.FluidFX;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.recipe.RecipeFinder;

import net.minecraft.ChatFormatting;
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

public class CentrifugeBlockEntity extends KineticTileEntity implements IFluidBlockEntity {

    private static final Object centrifugationRecipeKey = new Object();
    private static final int TANK_CAPACITY = DestroyAllConfigs.SERVER.contraptions.centrifugeCapacity.get();

    private SmartFluidTankBehaviour inputTank, denseOutputTank, lightOutputTank;
    protected LazyOptional<IFluidHandler> inputFluidCapability, denseOutputFluidCapability, lightOutputFluidCapability;

    protected DestroyAdvancementBehaviour advancementBehaviour;

    private Direction denseOutputTankFace;

    private int lubricationLevel;
    private static final int MAX_LUBRICATION_LEVEL = DestroyAllConfigs.SERVER.contraptions.centrifugeMaxLubricationLevel.get();

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
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::sendData);
        denseOutputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::sendData)
            .forbidInsertion();
        lightOutputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::sendData)
            .forbidInsertion();
        behaviours.addAll(List.of(inputTank, denseOutputTank, lightOutputTank));
        inputFluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(inputTank.getCapability().orElse(null));
        });
        denseOutputFluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(denseOutputTank.getCapability().orElse(null));
        });
        lightOutputFluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(lightOutputTank.getCapability().orElse(null));
        });

        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);
    };

    /**
     * Attempts to rotate the Centrifuge so that it faces a new face which also has a Pipe. If no Pipe is available, just rotates it anyway.
     * @param shouldSwitch Whether the rotation should prioritise switching faces or staying on the current face
     * @return Whether the Centrifuge was rotated
     */
    @SuppressWarnings("null")
    public boolean attemptRotation(boolean shouldSwitch) {
        if (!hasLevel()) {
            return false;
        };
        if (getLevel().setBlock(getBlockPos(), getBlockState().setValue(CentrifugeBlock.DENSE_OUTPUT_FACE, refreshDirection(this, shouldSwitch ? denseOutputTankFace.getClockWise() : denseOutputTankFace, getDenseOutputTank(), true)), 6)) { // If the output Direction can be successfully changed
            denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
            notifyUpdate(); // Block State has changed
            return true;
        };
        return false;
    };

    //TODO Sounds

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (!hasLevel()) return; // Don't do anything if we're not in a Level
        if (getSpeed() == 0) return; // Don't do anything without rotational power
        if (isTankFull(getDenseOutputTank()) || isTankFull(getLightOutputTank())) return; // Don't do anything if output is full
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (getLevel().isClientSide()) {
                spawnParticles();
                return;
            };
            if (timer <= 0) {
                process();
            };
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
                timer = lastRecipe.getProcessingDuration();
                sendData();
            } else {
                timer = 100; // Don't try checking for another Recipe for another 100 ticks
                lastRecipe = null;
                sendData();
            };
            return;
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
        float lubricationMultiplier = MAX_LUBRICATION_LEVEL != 0 ? lubricationLevel / MAX_LUBRICATION_LEVEL : 1;
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
        if (lastRecipe == null) return;
        if (!canFitFluidInTank(lastRecipe.getDenseOutputFluid(), getDenseOutputTank()) || !canFitFluidInTank(lastRecipe.getLightOutputFluid(), getLightOutputTank()) || hasFluidInTank(lastRecipe.getRequiredFluid(), getLightOutputTank())) return; // Ensure the Recipe can still be Processed
        getInputTank().drain(lastRecipe.getRequiredFluid().getRequiredAmount(), FluidAction.EXECUTE);
        getDenseOutputTank().fill(lastRecipe.getDenseOutputFluid(), FluidAction.EXECUTE);
        getLightOutputTank().fill(lastRecipe.getLightOutputFluid(), FluidAction.EXECUTE);
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancements.USE_CENTRIFUGE);
        notifyUpdate();
    };

    @SuppressWarnings("null") // It's not null; I checked
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
                return inputFluidCapability.cast();
            } else if (side == Direction.DOWN) {
                return lightOutputFluidCapability.cast();
            } else if (side == denseOutputTankFace || pondering) {
                return denseOutputFluidCapability.cast();
            };
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        inputFluidCapability.invalidate();
        denseOutputFluidCapability.invalidate();
        lightOutputFluidCapability.invalidate();
    };

    public int getEachTankCapacity() {
        return DestroyAllConfigs.SERVER.contraptions.centrifugeCapacity.get();
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

        //"Lubrication: <level>"
        if (MAX_LUBRICATION_LEVEL != 0) {
            DestroyLang.translate("tooltip.centrifuge.lubrication")
                .style(ChatFormatting.GRAY)
                .space()
                .add(DestroyLang.barMeterComponent(lubricationLevel, MAX_LUBRICATION_LEVEL, Math.min(MAX_LUBRICATION_LEVEL, 20)))
                .forGoggles(tooltip);
        };
        //"Contents: "
        if (!(inputTank.isEmpty() && denseOutputTank.isEmpty() && lightOutputTank.isEmpty())) {
            DestroyLang.translate("tooltip.fluidcontraption.contents")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
            //"Unprocessed: <fluid>"
            if (!inputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.input_tank"), getInputTank());
            };
            //"Dense Product: <fluid>"
            if (!denseOutputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.dense_output_tank"), getDenseOutputTank());
            };
            //"Light Product: <fluid>"
            if (!lightOutputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.light_output_tank"), getLightOutputTank());
            };
        };
        return true;
    };

    
}
