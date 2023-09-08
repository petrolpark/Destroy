package com.petrolpark.destroy.fluid;

import com.simibubi.create.AllFluids.TintedFluidType;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class ColoredFluidType extends TintedFluidType {

    private final int color;

    public ColoredFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int color) {
        super(properties, stillTexture, flowingTexture);
        this.color = color;
    };

    @Override
    protected int getTintColor(FluidStack stack) {
        return color;
    };

    @Override
    protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return color;
    };
    
};
