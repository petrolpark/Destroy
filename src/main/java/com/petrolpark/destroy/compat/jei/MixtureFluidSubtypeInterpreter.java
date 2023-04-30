package com.petrolpark.destroy.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraftforge.fluids.FluidStack;

/**
 * Return a different String every time because no two Mixtures should be the same.
 * The interpretation of Mixtures is done by interpreting all of the contents of the Mixture separately in {@link DestroyRecipeManagerPlugin}.
*/ 
public class MixtureFluidSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {

    private static int number = 0;

    @Override
    public String apply(FluidStack ingredient, UidContext context) {
        return String.valueOf(number++);
    };
    
};
