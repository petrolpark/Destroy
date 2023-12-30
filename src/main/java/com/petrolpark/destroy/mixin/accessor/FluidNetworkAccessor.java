package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.fluids.FluidNetwork;

import net.minecraftforge.fluids.FluidStack;

@Mixin(FluidNetwork.class)
public interface FluidNetworkAccessor {
    
    @Accessor(
        value = "fluid",
        remap = false
    )
    public void setFluid(FluidStack stack);

    @Accessor(
        value = "fluid",
        remap = false
    )
    public FluidStack getFluid();
};
