package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraftforge.fluids.FluidStack;

public class CentrifugationRecipe extends SingleFluidRecipe {

    public CentrifugationRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.CENTRIFUGATION, params);
    };

    public FluidStack getDenseOutputFluid() {
        checkForValidOutputs();
        return fluidResults.get(0);
    };

    public FluidStack getLightOutputFluid() {
        checkForValidOutputs();
        return fluidResults.get(1);
    };

    private void checkForValidOutputs() {
        if (fluidResults.size() != 2) throw new IllegalStateException("Centrifugation Recipe " + id + " contains the wrong number of output fluids.");
    };

    @Override
    public String getRecipeTypeName() {
        return "Centrifugation";
    };
    
}
