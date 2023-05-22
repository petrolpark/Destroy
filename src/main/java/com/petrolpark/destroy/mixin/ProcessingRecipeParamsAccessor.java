package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.NonNullList;
import net.minecraftforge.fluids.FluidStack;

@Mixin(ProcessingRecipeBuilder.ProcessingRecipeParams.class)
public interface ProcessingRecipeParamsAccessor {

    @Accessor("fluidIngredients")
    public NonNullList<FluidIngredient> getFluidIngredients();

    @Accessor("fluidResults")
	public NonNullList<FluidStack> getFluidResults();
};
