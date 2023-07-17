package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.compat.jei.animation.AnimatedCentrifuge;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;

public class CentrifugationCategory extends DestroyRecipeCategory<CentrifugationRecipe> {

    private static final AnimatedCentrifuge centrifuge = new AnimatedCentrifuge();

    private static final int CENTRIFUGE_X = 35;
    private static final int CENTRIFUGE_Y = 60;

    public CentrifugationCategory(Info<CentrifugationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
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
    public void draw(CentrifugationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        AllGuiTextures.JEI_SHADOW.render(graphics, CENTRIFUGE_X - 19, CENTRIFUGE_Y - 5);
        centrifuge.draw(graphics, CENTRIFUGE_X, CENTRIFUGE_Y);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 29, 9);
        DestroyGuiTextures.JEI_SHORT_DOWN_ARROW.render(graphics, 33, 70);
        DestroyGuiTextures.JEI_SHORT_RIGHT_ARROW.render(graphics, 72, 38);
    };
    
}
