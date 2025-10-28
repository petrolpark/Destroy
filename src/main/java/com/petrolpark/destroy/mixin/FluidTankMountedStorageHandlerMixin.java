package com.petrolpark.destroy.mixin;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour.GeniusFluidTank;
import com.simibubi.create.content.fluids.tank.storage.FluidTankMountedStorage;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

@Mixin(FluidTankMountedStorage.Handler.class)
public abstract class FluidTankMountedStorageHandlerMixin extends FluidTank {

    public FluidTankMountedStorageHandlerMixin(int capacity) {
        super(capacity);
    };

    @Unique
    public Lazy<GeniusFluidTank> geniusFluidTank = Lazy.of(() -> new GeniusFluidTank(this.capacity, f -> {}));

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        GeniusFluidTank tank = geniusFluidTank.get();
        tank.setFluid(fluid);
        int filled = tank.fill(resource, action);

        if(filled > 0) {
            fluid = tank.getFluid();
            onContentsChanged();
        };

        return filled;
    };

    // This fixes a bug with Create causing an IllegalStateException to be thrown when emptying a contraption holding a fluid with NBT data
    @Override
    public @NotNull FluidStack getFluid() {
        return this.fluid.isEmpty() ? FluidStack.EMPTY : this.fluid;
    };

}
