package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.compat.jei.animation.AnimatedCentrifuge;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugationRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;

public class CentrifugationCategory extends PetrolparkRecipeCategory<CentrifugationRecipe> {

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

        addFluidSlot(builder, 3, 3, inputFluid);
        addOptionalRequiredBiomeSlot(builder, recipe, 3, 19);

        addFluidSlot(builder, 99, 38, denseOutputFluid);
        addFluidSlot(builder, 33, 96, lightOutputFluid);
    };

    @Override
    public void draw(CentrifugationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        AllGuiTextures.JEI_SHADOW.render(graphics, CENTRIFUGE_X - 19, CENTRIFUGE_Y - 5);
        centrifuge.draw(graphics, CENTRIFUGE_X, CENTRIFUGE_Y);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 29, 9);
        PetrolparkGuiTexture.JEI_SHORT_DOWN_ARROW.render(graphics, 33, 70);
        PetrolparkGuiTexture.JEI_SHORT_RIGHT_ARROW.render(graphics, 72, 38);
    };
    
}
