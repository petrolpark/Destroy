package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.compat.jei.animation.HeatConditionRenderer;
import com.petrolpark.destroy.recipe.DistillationRecipe;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class DistillationCategory extends DestroyRecipeCategory<DistillationRecipe> {

    public DistillationCategory(Info<DistillationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DistillationRecipe recipe, IFocusGroup focuses) {

        int fractions = recipe.getFluidResults().size();

        // Required number of Bubble Caps
        builder.addSlot(RecipeIngredientRole.CATALYST, 18, 30)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(new ItemStack(DestroyBlocks.BUBBLE_CAP.get(), fractions + 1));

        // Fluid input
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 81)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getRequiredFluid().getMatchingFluidStacks()))
            .addTooltipCallback(addFluidTooltip(recipe.getRequiredFluid().getRequiredAmount()));

        // Fluid results
        for (int i = 0; i < fractions; i++) {
            FluidStack result = recipe.getFluidResults().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, i % 2 == 0 ? 94 : 74, 74 - (12 * i))
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(result))
                .addTooltipCallback(addFluidTooltip(result.getAmount()));
        };

        HeatConditionRenderer.addHeatConditionSlots(builder, 80, 103, recipe.getRequiredHeat());
    };

    @Override
    @SuppressWarnings("resource")
    public void draw(DistillationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        for (int i = 0; i < recipe.getFluidResults().size(); i++) {
            DestroyGuiTextures.JEI_DISTILLATION_TOWER_MIDDLE.render(graphics, 55, 76 - (12 * i));
            if (i % 2 == 0) DestroyGuiTextures.JEI_DISTILLATION_TOWER_BRANCH.render(graphics, 75, 81 - (12 * i));
        };
        DestroyGuiTextures.JEI_DISTILLATION_TOWER_TOP.render(graphics, 55, 2 + (7 - recipe.getFluidResults().size()) * 12);
        DestroyGuiTextures.JEI_DISTILLATION_TOWER_BOTTOM.render(graphics, 55, 88);
        DestroyGuiTextures.JEI_DISTILLATION_TOWER_BRANCH.render(graphics, 35, 90);
        DestroyGuiTextures.JEI_TEXT_BOX_SHORT.render(graphics, 4, 102);
        HeatConditionRenderer.drawHeatConditionName(Minecraft.getInstance().font, graphics, 9, 108, recipe.getRequiredHeat());
    };
    
};
