package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.compat.jei.animation.AnimatedDynamo;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;

public class ElectrolysisCategory extends BasinCategory {

    AnimatedDynamo dynamo;

    public ElectrolysisCategory(Info<BasinRecipe> info) {
        super(info, false);
        dynamo = new AnimatedDynamo(true);
    };

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, matrixStack, mouseX, mouseY);
        dynamo.draw(matrixStack, getBackground().getWidth() / 2 + 3, 34);
    };
    
};
