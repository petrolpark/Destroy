package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.petrolpark.destroy.recipe.ElectrolysisRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

@IRecipeHandler.For(ElectrolysisRecipe.class)
public class ElectrolysisRecipeHandler implements IDestroyRecipeHandler<ElectrolysisRecipe> {
    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<ElectrolysisRecipe> factory() {
        return ElectrolysisRecipe::new;
    }
}
