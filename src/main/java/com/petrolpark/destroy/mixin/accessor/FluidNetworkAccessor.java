package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.FluidNetwork;

import net.minecraftforge.fluids.FluidStack;

@Mixin(FluidNetwork.class)
public interface FluidNetworkAccessor {
    
    @Accessor("fluid")
    public void setFluid(FluidStack stack);

    @Accessor("fluid")
    public FluidStack getFluid();
};
