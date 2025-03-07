package com.petrolpark.destroy.compat.jei.category;

import java.util.List;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.compat.jei.animation.AnimatedDynamo;
import com.petrolpark.destroy.content.processing.dynamo.ChargingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class ChargingCategory extends PetrolparkRecipeCategory<ChargingRecipe> {

    private final AnimatedDynamo dynamo = new AnimatedDynamo(false, false);

    public ChargingCategory(Info<ChargingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChargingRecipe recipe, IFocusGroup focuses) {
        builder
            .addSlot(RecipeIngredientRole.INPUT, 27, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));

		List<ProcessingOutput> results = recipe.getRollableResults();
		int i = 0;
		for (ProcessingOutput output : results) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
					.setBackground(getRenderedSlot(output), -1, -1)
					.addItemStack(output.getStack())
					.addRichTooltipCallback(addStochasticTooltip(output));
			i++;
		};
    };

    @Override
	public void draw(ChargingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);

		dynamo.draw(graphics, getBackground().getWidth() / 2 - 17, 22);
	};
    
};
