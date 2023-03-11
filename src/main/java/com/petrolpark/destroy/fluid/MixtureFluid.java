package com.petrolpark.destroy.fluid;

import com.petrolpark.destroy.chemistry.Mixture;
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

    public static FluidStack of(int amount, Mixture mixture) {
        FluidStack fluidStack = new FluidStack(DestroyFluids.MIXTURE.get().getSource(), amount);
        addMixtureToFluidStack(fluidStack, mixture);
        return fluidStack;
    };

    public static FluidStack addMixtureToFluidStack(FluidStack fluidStack, Mixture mixture) {
        if (mixture.isEmpty()) {
            fluidStack.removeChildTag("Mixture");
            return fluidStack;
        };
        fluidStack.getOrCreateTag().put("Mixture", mixture.writeNBT());
        return fluidStack;
    };

    public static class MixtureFluidType extends TintedFluidType {

        public MixtureFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            super(properties, stillTexture, flowingTexture);
        };

        @Override
        protected int getTintColor(FluidStack stack) {
            // TODO Auto-generated method stub
            return 0;
        };

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            // TODO Auto-generated method stub
            return 0;
        };

    };

    
}
