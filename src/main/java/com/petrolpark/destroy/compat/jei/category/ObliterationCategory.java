package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.core.explosion.ObliterationRecipe;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockItem;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;

public class ObliterationCategory extends PetrolparkRecipeCategory<ObliterationRecipe> {

    public ObliterationCategory(Info<ObliterationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ObliterationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));
        
        builder.addSlot(RecipeIngredientRole.CATALYST, 77, 26)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(MixedExplosiveBlockItem.getExampleItemStack());

        ProcessingOutput output = recipe.getRollableResults().get(0);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 51)
            .setBackground(getRenderedSlot(output), -1, -1)
			.addItemStack(output.getStack())
			.addRichTooltipCallback(addStochasticTooltip(output));
    };

    @Override
    public void draw(ObliterationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        PetrolparkGuiTexture.JEI_EXPLOSION.render(graphics, 76, 4);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.popPose();
    };

    
    
};
