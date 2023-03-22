package com.petrolpark.destroy.fluid;

import com.simibubi.create.AllFluids.TintedFluidType;
import com.simibubi.create.content.contraptions.fluids.VirtualFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class UrineFluid extends VirtualFluid {


    public UrineFluid(Properties properties) {
        super(properties);
    };

    public static class UrineFluidType extends TintedFluidType {

        private static final int PEE_TINT = 15391310;

        public UrineFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        };
    
        @Override
        protected int getTintColor(FluidStack stack) {
            return PEE_TINT;
        };
    
        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return PEE_TINT;
        };
        
    };
};
