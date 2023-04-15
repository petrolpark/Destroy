package com.petrolpark.destroy.compat.jei;

import java.util.ArrayList;
import java.util.List;

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
            if (focus.getTypedValue().getIngredient() instanceof Molecule) recipeTypes.add(ReactionCategory.TYPE);
            return recipeTypes;
        };

        @Override
        @SuppressWarnings("unchecked")
        public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
            List<T> recipes = new ArrayList<>();
            if (!(recipeCategory instanceof ReactionCategory)) return recipes;
            Molecule molecule = focus.checkedCast(MoleculeJEIIngredient.TYPE).map(moleculeIngredient -> moleculeIngredient.getTypedValue().getIngredient()).orElse(null);
            if (molecule == null) return recipes;
            switch (focus.getRole()) {
                case INPUT: {
                    molecule.getReactantReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                }
                case OUTPUT: {
                    molecule.getProductReactions().forEach(reaction -> recipes.add((T)(ReactionCategory.RECIPES.get(reaction)))); // This is an unchecked conversion but I think it's fine
                }
                case CATALYST: {
                    //TODO
                }
                case RENDER_ONLY: {

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
