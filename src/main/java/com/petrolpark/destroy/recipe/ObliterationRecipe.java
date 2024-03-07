package com.petrolpark.destroy.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * These Recipes are display only. Creating an Obliteration Recipe will not add it to the game -
 * the loot table of the Block must also be changed.
 */
public class ObliterationRecipe extends ProcessingRecipe<RecipeWrapper> {

    public ObliterationRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.OBLITERATION, params);
    };

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return false;
    };

    @Override
    protected int getMaxInputCount() {
        return 1;
    };

    @Override
    protected int getMaxOutputCount() {
        return 1;
    };

    public Ingredient getInput() {
        if(ingredients.isEmpty()) return null;
        return ingredients.get(0);
    }

    public ProcessingOutput getResult() {
        if(results.isEmpty()) return null;
        return results.get(0);
    }
    
};
