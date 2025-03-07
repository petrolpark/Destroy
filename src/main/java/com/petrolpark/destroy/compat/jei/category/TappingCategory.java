package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.compat.jei.animation.AnimatedTreeTap;
import com.petrolpark.destroy.content.processing.treetap.BlockTapping;
import com.petrolpark.destroy.content.processing.treetap.TappingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;

public class TappingCategory extends PetrolparkRecipeCategory<TappingRecipe> {

    public static final List<TappingRecipe> RECIPES = new ArrayList<>(BlockTapping.ALL_TAPPINGS.size());

    static {
        for (BlockTapping tapping : BlockTapping.ALL_TAPPINGS) RECIPES.add(TappingRecipe.create(tapping));
    };

    private final AnimatedTreeTap tap = new AnimatedTreeTap();

    public TappingCategory(Info<TappingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TappingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));

        FluidStack fs = recipe.getFluidResults().get(0);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 50)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(fs))
            .addRichTooltipCallback(addFluidTooltip(fs.getAmount()));
    };

    @Override
	public void draw(TappingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);

		tap.draw(graphics, 80, 50);
	};
    
};
