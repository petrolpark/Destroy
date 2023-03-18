package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;

public class DistillationCategory extends CreateRecipeCategory<DistillationRecipe> {

    public DistillationCategory(Info<DistillationRecipe> info) {
        super(info);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DistillationRecipe recipe, IFocusGroup focuses) {
        return;
        // TODO
    };

    @Override
    public void draw(DistillationRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        // TODO
    };
    
};
