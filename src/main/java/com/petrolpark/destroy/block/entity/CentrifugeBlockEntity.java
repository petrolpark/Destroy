package com.petrolpark.destroy.block.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.destroy.block.CentrifugeBlock;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.contraptions.fluids.FluidFX;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class CentrifugeBlockEntity extends FluidKineticTileEntity implements IWrenchable {

    private SmartFluidTank inputTank, denseOutputTank, lightOutputTank;
    protected LazyOptional<IFluidHandler> inputFluidCapability, denseOutputFluidCapability, lightOutputFluidCapability;

    private Direction denseOutputTankFace;

    private int lubricationLevel;
    private int MAX_LUBRICATION_LEVEL = DestroyAllConfigs.SERVER.contraptions.centrifugeMaxLubricationLevel.get();

    public int timer;
    private CentrifugationRecipe lastRecipe;

    public CentrifugeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        inputTank = new SmartFluidTank(getEachTankCapacity(), this::onFluidStackChanged);
        denseOutputTank = new SmartFluidTank(getEachTankCapacity(), this::onFluidStackChanged);
        lightOutputTank = new SmartFluidTank(getEachTankCapacity(), this::onFluidStackChanged);
        inputFluidCapability = LazyOptional.of(() -> inputTank);
        denseOutputFluidCapability = LazyOptional.of(() -> denseOutputTank);
        lightOutputFluidCapability = LazyOptional.of(() -> lightOutputTank);
        denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        updateDenseOutputFace();
        lubricationLevel = 1;
    };

    public void updateDenseOutputFace() {
        if (this.getLevel() == null) return;
        denseOutputTankFace = refreshDirection(denseOutputTankFace);
        getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CentrifugeBlock.DENSE_OUTPUT_FACE, denseOutputTankFace));
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        denseOutputTankFace = refreshDirection(denseOutputTankFace.getClockWise());
        return IWrenchable.super.onWrenched(state, context);
    };

    //TODO Sounds

    @Override
    public void tick() {
        super.tick();

        if (getSpeed() == 0) return; //don't do anything without rotational power
        if (isTankFull(denseOutputTank) || isTankFull(lightOutputTank)) return; //don't do anything if output is full
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (getLevel() != null && getLevel().isClientSide()) {
                spawnParticles();
                return;
            };
            if (timer <= 0) {
                process();
            };
            return;
        };
        if (inputTank.isEmpty()) return; //don't do anything more if input tank is empty
        if (lastRecipe == null || lastRecipe.getRequiredFluid().test(inputTank.getFluid())) { //if the recipe has changed
            RecipeWrapper wrapper = new RecipeWrapper(new EmptyHandler()); //create dummy wrapper
            for (Recipe<RecipeWrapper> recipe : getLevel().getRecipeManager().getRecipesFor(DestroyRecipeTypes.CENTRIFUGATION.getType(), wrapper, level)) { //search all Centrifugation recipes...
                if (((CentrifugationRecipe)recipe).getRequiredFluid().test(inputTank.getFluid())) { //...for Recipes with the right matching fluid
                    lastRecipe = (CentrifugationRecipe)recipe;
                    if (canFitFluidInTank(lastRecipe.getDenseOutputFluid(), denseOutputTank) && canFitFluidInTank(lastRecipe.getLightOutputFluid(), lightOutputTank) && hasFluidInTank(lastRecipe.getRequiredFluid(), inputTank)) { //check recipe output can fit
                        timer = lastRecipe.getProcessingDuration();
                        sendData();
                        return;
                    } else {
                        lastRecipe = null;
                    };
                };
            };
            lastRecipe = null; //no matching recipe was found :(
            timer = 100; 
            sendData();
        };
        sendData();
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        lubricationLevel = compound.getInt("Lubrication");
        timer = compound.getInt("Timer");
        inputTank.readFromNBT(compound.getCompound("InputTank"));
        denseOutputTank.readFromNBT(compound.getCompound("DenseOutputTank"));
        lightOutputTank.readFromNBT(compound.getCompound("LightOutputTank"));
        denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        super.read(compound, clientPacket);
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Lubrication", lubricationLevel);
        compound.putInt("Timer", timer);
        compound.put("InputTank", inputTank.writeToNBT(new CompoundTag()));
        compound.put("DenseOutputTank", denseOutputTank.writeToNBT(new CompoundTag()));
        compound.put("LightOutputTank", lightOutputTank.writeToNBT(new CompoundTag()));
        super.write(compound, clientPacket);
    };

    public int getProcessingSpeed() {
        float lubricationMultiplier = MAX_LUBRICATION_LEVEL != 0 ? lubricationLevel / MAX_LUBRICATION_LEVEL : 1;
        return Mth.clamp((int) Math.abs(getSpeed() * lubricationMultiplier / 16f), 1, 512);
    };

    public SmartFluidTank getInputTank() {
        return this.inputTank;
    };

    public SmartFluidTank getDenseOutputTank() {
        return this.denseOutputTank;
    };

    public SmartFluidTank getLightOutputTank() {
        return this.lightOutputTank;
    };

    public void process() {
        if (lastRecipe == null) return;
        if (!canFitFluidInTank(lastRecipe.getDenseOutputFluid(), denseOutputTank) || !canFitFluidInTank(lastRecipe.getLightOutputFluid(), lightOutputTank) || hasFluidInTank(lastRecipe.getRequiredFluid(), inputTank)) return; //ensure the Recipe can still be Processed
        FluidStack stackToDrain = new FluidStack(inputTank.getFluid().getFluid(), lastRecipe.getRequiredFluid().getRequiredAmount()); //Drain a Fluid Stack with the Fluid matching the Fluid already in the Tank (which should be valid), and the amount matching the recipe
        inputTank.drain(stackToDrain, FluidAction.EXECUTE);
        denseOutputTank.fill(lastRecipe.getDenseOutputFluid(), FluidAction.EXECUTE);
        lightOutputTank.fill(lastRecipe.getLightOutputFluid(), FluidAction.EXECUTE);
        sendData();
        setChanged();
    };

    public void spawnParticles() {
        FluidStack fluidStack = inputTank.getFluid();
        if (fluidStack.isEmpty()) return;

        ParticleOptions data = FluidFX.getFluidParticle(fluidStack);
        float angle = level.random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.5f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1/ 128f);
        level.addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z);
    };

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == Direction.UP) {
                return inputFluidCapability.cast();
            } else if (side == Direction.DOWN) {
                return lightOutputFluidCapability.cast();
            } else if (side == denseOutputTankFace) {
                return denseOutputFluidCapability.cast();
            };
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputFluidCapability.invalidate();
        denseOutputFluidCapability.invalidate();
        lightOutputFluidCapability.invalidate();
    };

    public int getEachTankCapacity() {
        return DestroyAllConfigs.SERVER.contraptions.centrifugeCapacity.get();
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
            DestroyLang.translate("tooltip.centrifuge.contents")
                .style(ChatFormatting.GRAY)
                .forGoggles(tooltip);
            //"Unprocessed: <fluid>"
            if (!inputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.input_tank"), inputTank);
            };
            //"Dense Product: <fluid>"
            if (!denseOutputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.dense_output_tank"), denseOutputTank);
            };
            //"Light Product: <fluid>"
            if (!lightOutputTank.isEmpty()) {
                DestroyLang.tankContentsTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.light_output_tank"), lightOutputTank);
            };
        };
        return true;
    };

    
}
