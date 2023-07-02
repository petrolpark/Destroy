package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.DistillationRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.world.item.crafting.Ingredient;

public class DistillationCategory extends DestroyRecipeCategory<DistillationRecipe> {

    public DistillationCategory(Info<DistillationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DistillationRecipe recipe, IFocusGroup focuses) {
        builder
            .addSlot(RecipeIngredientRole.INPUT, 52, 40)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(Ingredient.of(DestroyItems.HYPERACCUMULATING_FERTILIZER.get()));
    };

    @Override
    public void draw(DistillationRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        // TODO
    };
    
};
