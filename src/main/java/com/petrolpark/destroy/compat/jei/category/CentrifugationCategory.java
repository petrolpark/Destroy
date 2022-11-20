package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.compat.jei.animation.AnimatedCentrifuge;
import com.petrolpark.destroy.gui.DestroyGuiTextures;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraftforge.fluids.FluidStack;

public class CentrifugationCategory extends CreateRecipeCategory<CentrifugationRecipe> {

    private final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

    private final int CENTRIFUGE_X = 33;
    private final int CENTRIFUGE_Y = 60;

    public CentrifugationCategory(Info<CentrifugationRecipe> info) {
        super(info);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugationRecipe recipe, IFocusGroup focuses) {

        FluidIngredient inputFluid = recipe.getFluidIngredients().iterator().next();
        FluidStack denseOutputFluid = recipe.getDenseOutputFluid();
        FluidStack lightOutputFluid = recipe.getLightOutputFluid();

        builder
            .addSlot(RecipeIngredientRole.INPUT, 3, 3)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(inputFluid.getMatchingFluidStacks()))
            .addTooltipCallback(addFluidTooltip(inputFluid.getRequiredAmount()));

        builder
            .addSlot(RecipeIngredientRole.OUTPUT, 99, 38)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(denseOutputFluid))
            .addTooltipCallback(addFluidTooltip(denseOutputFluid.getAmount()));

        builder
            .addSlot(RecipeIngredientRole.OUTPUT, 33, 96)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(lightOutputFluid))
            .addTooltipCallback(addFluidTooltip(lightOutputFluid.getAmount()));
        
    };

    @Override
    public void draw(CentrifugationRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, matrixStack, mouseX, mouseY);

        AllGuiTextures.JEI_SHADOW.render(matrixStack, CENTRIFUGE_X - 17, CENTRIFUGE_Y - 4);
        centrifuge.draw(matrixStack, CENTRIFUGE_X, CENTRIFUGE_Y);

        AllGuiTextures.JEI_DOWN_ARROW.render(matrixStack, 29, 9);
        DestroyGuiTextures.JEI_SHORT_DOWN_ARROW.render(matrixStack, 33, 70);
        DestroyGuiTextures.JEI_SHORT_RIGHT_ARROW.render(matrixStack, 72, 38);
    };
    
}
