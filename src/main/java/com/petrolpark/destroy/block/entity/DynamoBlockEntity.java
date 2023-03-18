package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.behaviour.ChargingBehaviour;
import com.petrolpark.destroy.behaviour.ChargingBehaviour.ChargingBehaviourSpecifics;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.content.contraptions.processing.BasinOperatingTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DynamoBlockEntity extends BasinOperatingTileEntity implements ChargingBehaviourSpecifics {

    private static final Object electrolysisRecipeKey = new Object();

    public ChargingBehaviour chargingBehaviour;

    public DynamoBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        chargingBehaviour = new ChargingBehaviour(this);
        behaviours.add(chargingBehaviour);
    };

    @Override
    @SuppressWarnings("null")
    public void onSpeedChanged(float prevSpeed) {
        if (hasLevel()) {
            getLevel().updateNeighborsAt(getBlockPos(), DestroyBlocks.DYNAMO.get()); // It thinks getLevel() can be null (it can't at this point)
        };
        super.onSpeedChanged(prevSpeed);
    };

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        applyBasinRecipe();
        return true;
    };

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryProcessOnBelt'");
    };

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tryProcessInWorld'");
    };

    @Override
    public boolean canProcessInBulk() {
        return DestroyAllConfigs.SERVER.contraptions.dynamoBulkCharging.get();
    };

    @Override
    public void onChargingCompleted() {
        if (chargingBehaviour.mode == ChargingBehaviour.Mode.BASIN && matchBasinRecipe(currentRecipe) && getBasin().filter(BasinTileEntity::canContinueProcessing).isPresent()) {
			startProcessingBasin();
		} else {
			basinChecker.scheduleUpdate();
        }
    };

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    };

    @Override
    protected boolean isRunning() {
        return chargingBehaviour.running;
    };

    @Override
    protected void onBasinRemoved() {
        chargingBehaviour.running = false;
        chargingBehaviour.runningTicks = 0;
        sendData();
    };

    public int getRedstoneSignal() {
        return (int) Mth.lerp(Mth.clamp(Math.abs(getSpeed()) / 256f, 0, 1), 0, 15);
    };

    @Override
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> recipe) {
        return recipe.getType() == DestroyRecipeTypes.ELECTROLYSIS.getType();
    };

    @Override
    protected Object getRecipeCacheKey() {
        return electrolysisRecipeKey;
    };
    
}
