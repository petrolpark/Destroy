package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.GeniusFluidTankBehaviour.GeniusFluidTank;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraftforge.fluids.FluidStack;

@Mixin(FluidTankBlockEntity.class)
public abstract class FluidTankBlockEntityMixin {
    
    @Overwrite
    public SmartFluidTank createInventory() {
        return new GeniusFluidTank(FluidTankBlockEntity.getCapacityMultiplier(), this::invokeOnFluidStackChanged);
    };

    @Invoker("onFluidStackChanged")
    public abstract void invokeOnFluidStackChanged(FluidStack stack);
};
