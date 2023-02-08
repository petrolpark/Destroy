package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.recipe.MutationRecipe;
import com.petrolpark.destroy.util.CropMutation;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;

public class MutationCategory extends CreateRecipeCategory<MutationRecipe> {

    public static final List<MutationRecipe> RECIPES = new ArrayList<>();

    static {
        for (HashSet<CropMutation> mutations : CropMutation.MUTATIONS.values()) {
            for (CropMutation mutation : mutations) {
                RECIPES.add(MutationRecipe.create(mutation));
                Destroy.LOGGER.info("Generated recipeeee");
            };
        };
    };

    public MutationCategory(Info<MutationRecipe> info) {
        super(info);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MutationRecipe recipe, IFocusGroup focuses) {
        
    };
    
};
