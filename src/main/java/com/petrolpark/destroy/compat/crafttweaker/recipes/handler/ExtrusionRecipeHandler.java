package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.petrolpark.destroy.compat.crafttweaker.recipes.manager.IDestroyRecipeManager;
import com.petrolpark.destroy.recipe.ExtrusionRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

@IRecipeHandler.For(ExtrusionRecipe.class)
public class ExtrusionRecipeHandler implements ITwoItemsRecipeHandler<ExtrusionRecipe> {
    @Override
    public String getRecipeTypeName() {
        return "extrusion";
    }

    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<ExtrusionRecipe> factory() {
        return ExtrusionRecipe::new;
    }
}
