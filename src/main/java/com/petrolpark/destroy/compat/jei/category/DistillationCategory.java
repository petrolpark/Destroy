package com.petrolpark.destroy.compat.jei.category;

import com.petrolpark.client.rendering.PetrolparkGuiTexture;
import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.compat.jei.animation.HeatConditionRenderer;
import com.petrolpark.destroy.content.processing.distillation.DistillationRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class DistillationCategory extends PetrolparkRecipeCategory<DistillationRecipe> {

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

        // Location indicator
        addOptionalRequiredBiomeSlot(builder, recipe, 18, 49);

        // Fluid input
        addFluidSlot(builder, 18, 81, recipe.getRequiredFluid());

        // Fluid results
        for (int i = 0; i < fractions; i++) {
            FluidStack result = recipe.getFluidResults().get(i);
            addFluidSlot(builder,  i % 2 == 0 ? 94 : 74, 74 - (12 * i), result);
        };

        HeatConditionRenderer.addHeatConditionSlots(builder, 80, 103, recipe.getRequiredHeat());
    };

    @Override
    @SuppressWarnings("resource")
    public void draw(DistillationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        for (int i = 0; i < recipe.getFluidResults().size(); i++) {
            PetrolparkGuiTexture.JEI_DISTILLATION_TOWER_MIDDLE.render(graphics, 55, 76 - (12 * i));
            if (i % 2 == 0) PetrolparkGuiTexture.JEI_DISTILLATION_TOWER_BRANCH.render(graphics, 75, 81 - (12 * i));
        };
        PetrolparkGuiTexture.JEI_DISTILLATION_TOWER_TOP.render(graphics, 55, 2 + (7 - recipe.getFluidResults().size()) * 12);
        PetrolparkGuiTexture.JEI_DISTILLATION_TOWER_BOTTOM.render(graphics, 55, 88);
        PetrolparkGuiTexture.JEI_DISTILLATION_TOWER_BRANCH.render(graphics, 35, 90);
        PetrolparkGuiTexture.JEI_TEXT_BOX_SHORT.render(graphics, 4, 102);
        HeatConditionRenderer.drawHeatConditionName(Minecraft.getInstance().font, graphics, 9, 108, recipe.getRequiredHeat());
    };
    
};
