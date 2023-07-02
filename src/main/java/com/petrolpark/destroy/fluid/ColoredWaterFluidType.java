package com.petrolpark.destroy.fluid;

import com.simibubi.create.AllFluids.TintedFluidType;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class ColoredWaterFluidType extends TintedFluidType {

    private final int color;

    public ColoredWaterFluidType(Properties properties, int color) {
        super(properties, new ResourceLocation("minecraft", "block/water_still"), new ResourceLocation("minecraft", "block/water_flowing"));
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
