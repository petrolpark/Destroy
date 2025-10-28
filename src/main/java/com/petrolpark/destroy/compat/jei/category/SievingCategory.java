package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.compat.jei.animation.AnimatedSieve;
import com.petrolpark.destroy.content.processing.sieve.SievingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class SievingCategory extends PetrolparkRecipeCategory<SievingRecipe> {

    private static final AnimatedSieve sieve = new AnimatedSieve();

    public SievingCategory(Info<SievingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SievingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 34, 9)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));

        int xOffset = (int)(recipe.getRollableResults().size() * 9.5f);
        int i = 0;
        for (ProcessingOutput output : recipe.getRollableResults()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 75 + i * 19 - xOffset, 70)
                .setBackground(getRenderedSlot(output), -1, -1)
                .addItemStack(output.getStack())
                .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        };

        
    };

    @Override
    public void draw(SievingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 50, 50);
        sieve.draw(guiGraphics, 70, 62);
        AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 62, 14);
    };
    
};
