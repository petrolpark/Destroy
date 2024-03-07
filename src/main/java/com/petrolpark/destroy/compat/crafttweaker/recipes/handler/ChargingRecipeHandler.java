package com.petrolpark.destroy.compat.crafttweaker.recipes.handler;

import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

@IRecipeHandler.For(ChargingRecipe.class)
public class ChargingRecipeHandler implements IDestroyRecipeHandler<ChargingRecipe> {

    @Override
    public String getRecipeTypeName() {
        return "charging";
    }

    @Override
    public ProcessingRecipeBuilder.ProcessingRecipeFactory<ChargingRecipe> factory() {
        return ChargingRecipe::new;
    }
}
