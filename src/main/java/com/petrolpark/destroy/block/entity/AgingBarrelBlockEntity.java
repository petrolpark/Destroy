package com.petrolpark.destroy.block.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class AgingBarrelBlockEntity extends SmartTileEntity {

    protected SmartInventory inventory;
    protected SmartFluidTankBehaviour inputTank, outputTank;
    LazyOptional<IFluidHandler> fluidCapability;
    LazyOptional<IItemHandler> itemCapability;
    int progress;

    public AgingBarrelBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        inventory = new SmartInventory(1, this, 1, false).forbidExtraction();
        itemCapability = LazyOptional.of(() -> inventory);
        int progress = 0;
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, 1000, true)
            .forbidExtraction();
        outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, 1000, true)
            .forbidInsertion();
        behaviours.add(inputTank);
        behaviours.add(outputTank);
        fluidCapability = LazyOptional.of(() -> {
			LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
			LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
			return new CombinedTankWrapper(outputCap.orElse(null), inputCap.orElse(null));
		});
    };

    private void process() {
        
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCapability.cast();
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void tick() {
        
        super.tick();
    };

    


    
}
