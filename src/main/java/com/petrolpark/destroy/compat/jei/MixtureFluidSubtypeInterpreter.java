package com.petrolpark.destroy.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraftforge.fluids.FluidStack;

/**
 * The proper interpretation of Mixtures is done by interpreting all of the contents of the Mixture separately in {@link DestroyRecipeManagerPlugin}.
*/ 
public class MixtureFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {

    @Override
    public String apply(FluidStack ingredient, UidContext context) {
        if (!ingredient.hasTag()) return IIngredientSubtypeInterpreter.NONE;
        return ingredient.getOrCreateTag().getAsString();
    };
    
};
