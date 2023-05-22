package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class DistillationRecipe extends SingleFluidRecipe {

    public DistillationRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.DISTILLATION, params);
    };

    /**
     * The number of fractions this Recipe produces
     */
    public int getFractions() {
        return getFluidResults().size();
    };

    @Override
    protected int getMaxFluidOutputCount() {
        return 8;
    };

    @Override
    protected boolean canRequireHeat() {
        return true;
    };

    @Override
    protected boolean canSpecifyDuration() {
        return false;
    };

    @Override
    public String getRecipeTypeName() {
        return "distillation";
    };
    
}
