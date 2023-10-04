package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.recipe.ObliterationRecipe;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ObliterationCategory extends DestroyRecipeCategory<ObliterationRecipe> {

    public ObliterationCategory(Info<ObliterationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ObliterationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));
        
        List<ItemStack> explosives = new ArrayList<>();
        ForgeRegistries.ITEMS.tags().getTag(DestroyItemTags.OBLITERATION_EXPLOSIVE.tag).forEach(item -> {
            explosives.add(new ItemStack(item));
        });
        builder.addSlot(RecipeIngredientRole.CATALYST, 77, 26)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStacks(explosives);

        ProcessingOutput output = recipe.getRollableResults().get(0);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 51)
            .setBackground(getRenderedSlot(output), -1, -1)
			.addItemStack(output.getStack())
			.addTooltipCallback(addStochasticTooltip(output));
    };

    @Override
    public void draw(ObliterationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

        DestroyGuiTextures.JEI_EXPLOSION.render(graphics, 76, 4);
		AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.popPose();
    };

    
    
};
