package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.content.product.periodictable.ElementTankFillingRecipe;
import com.petrolpark.destroy.mixin.compat.jei.CreateRecipeCategoryAccessor;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;


public class ElementTankFillingCategory extends PetrolparkRecipeCategory<ElementTankFillingRecipe> {

    public ElementTankFillingCategory(Info<ElementTankFillingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ElementTankFillingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, recipe.getRequiredFluid().getMatchingFluidStacks())
            .addTooltipCallback(CreateRecipeCategoryAccessor::invokeAddPotionTooltip);

        builder.addSlot(RecipeIngredientRole.INPUT, 25, 22)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(DestroyBlocks.ELEMENT_TANK.asStack());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 105, 22)
            .setBackground(getRenderedSlot(), -1, -1)
			.addItemStack(recipe.getRollableResultsAsItemStacks().get(0));
    };

    @Override
    public void draw(ElementTankFillingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 20, 5);
        AllGuiTextures.JEI_ARROW.render(guiGraphics, 54, 25);
    };
    
};
