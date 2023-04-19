package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.compat.jei.category.ReactionCategory;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class DestroyRecipeManagerPlugins {
    public static final IRecipeManagerPlugin MOLECULE_RECIPES_PLUGIN = new IRecipeManagerPlugin() {

        @Override
        public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
            List<RecipeType<?>> recipeTypes = new ArrayList<RecipeType<?>>();
            if (focus.getTypedValue().getType() == MoleculeJEIIngredient.TYPE) {
                recipeTypes.addAll(DestroyJEI.RECIPE_TYPES); // Add all possible processing recipe categories
            };
            return recipeTypes;
        };

        @Override
        @SuppressWarnings("unchecked")
        public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
            List<T> recipes = new ArrayList<>();
            Destroy.LOGGER.info("Lets check "+recipeCategory.getTitle().getString());

            // Molecules
            Molecule molecule = focus.checkedCast(MoleculeJEIIngredient.TYPE).map(moleculeIngredient -> moleculeIngredient.getTypedValue().getIngredient()).orElse(null);
            if (molecule != null) { // Ignore this if we're not dealing with a Molecule
                switch (focus.getRole()) {
                    case INPUT: {
                        // Add Reaction Recipes
                        if (recipeCategory instanceof ReactionCategory) molecule.getReactantReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                        
                        // Add non-Reaction Recipes
                        recipes.addAll(DestroyJEI.MOLECULES_INPUT.get(molecule).stream()
                            .filter(recipe -> recipe.getClass() == recipeCategory.getRecipeType().getRecipeClass()) // Check for Recipes that match this category
                            .map(recipe -> (T) recipe) // Cast these Recipes
                            .toList()
                        );
                    }
                    case OUTPUT: {
                        // Add Reaction Recipes
                        if (recipeCategory instanceof ReactionCategory) molecule.getProductReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                    
                        // Add non-Reaction Recipes
                        recipes.addAll(DestroyJEI.MOLECULES_INPUT.get(molecule).stream()
                            .filter(recipe -> recipe.getClass() == recipeCategory.getRecipeType().getRecipeClass()) // Check for Recipes that match this category
                            .map(recipe -> (T) recipe) // Cast these Recipes
                            .toList()
                        );
                    }
                    case CATALYST: {
                        
                    }
                    case RENDER_ONLY: {

                    };
                };
            };
            return recipes;
        };

        @Override
        public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
            return List.of();
        };
        
    };
};
