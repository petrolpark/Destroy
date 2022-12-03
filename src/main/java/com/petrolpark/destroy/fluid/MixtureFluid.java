package com.petrolpark.destroy.fluid;

import com.simibubi.create.AllFluids.TintedFluidType;
import com.simibubi.create.content.contraptions.fluids.VirtualFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class MixtureFluid extends VirtualFluid {

    public MixtureFluid(Properties properties) {
        super(properties);
    };

    public static class MixtureFluidType extends TintedFluidType {

        public MixtureFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        };

        @Override
        protected int getTintColor(FluidStack stack) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    
}
