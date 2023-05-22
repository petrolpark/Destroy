package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class ElectrolysisRecipe extends BasinRecipe {

    public ElectrolysisRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.ELECTROLYSIS, params);
    };
    
};
