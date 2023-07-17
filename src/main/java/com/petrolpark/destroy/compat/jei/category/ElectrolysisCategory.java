package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.destroy.compat.jei.animation.AnimatedDynamo;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;

public class ElectrolysisCategory extends BasinCategory {

    AnimatedDynamo dynamo;

    public ElectrolysisCategory(Info<BasinRecipe> info) {
        super(info, false);
        dynamo = new AnimatedDynamo(true);
    };

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        dynamo.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
    };
    
};
