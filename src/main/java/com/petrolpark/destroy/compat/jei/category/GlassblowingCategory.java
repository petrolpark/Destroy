package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.compat.jei.animation.AnimatedBlowpipe;
import com.petrolpark.destroy.content.processing.glassblowing.GlassblowingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class GlassblowingCategory extends PetrolparkRecipeCategory<GlassblowingRecipe> {

    private final AnimatedBlowpipe blowpipe;

    public GlassblowingCategory(Info<GlassblowingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        blowpipe = new AnimatedBlowpipe();
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GlassblowingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 32)
            .setSlotName("input")
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getFluidIngredients().get(0).getMatchingFluidStacks()))
            .addRichTooltipCallback(addFluidTooltip(recipe.getFluidIngredients().get(0).getRequiredAmount()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 32)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(recipe.getRollableResults().get(0).getStack());
    };

    @Override
    public void draw(GlassblowingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PoseStack ms = guiGraphics.pose();
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 38, 14);
        ms.pushPose();
        ms.translate(78, 15, 0);
        blowpipe.draw(recipe, recipeSlotsView.findSlotByName("input").get().getDisplayedIngredient(ForgeTypes.FLUID_STACK).get(), guiGraphics);
        ms.popPose();
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 27, 36);
    };
    
};
