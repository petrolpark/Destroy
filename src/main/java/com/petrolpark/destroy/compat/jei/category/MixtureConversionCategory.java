package com.petrolpark.destroy.compat.jei.category;

import java.util.List;
import java.util.Collections;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.core.chemistry.recipe.MixtureConversionRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

public class MixtureConversionCategory extends PetrolparkRecipeCategory<MixtureConversionRecipe> {

    public MixtureConversionCategory(Info<MixtureConversionRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixtureConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withFullVisibility(recipe.getFluidIngredients().get(0).getMatchingFluidStacks()))
            .addRichTooltipCallback(addFluidTooltip(1));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withFullVisibility(recipe.getFluidResults()))
            .addRichTooltipCallback(addFluidTooltip(1));
    };
    
    @Override
    public List<Component> getTooltipStrings(MixtureConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX > 20 && mouseX < 105) {
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("recipe.mixture_conversion.description").string(), Palette.GRAY_AND_WHITE);
        };
        return Collections.emptyList();
    };

    @Override
    public void draw(MixtureConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 27, 6);
    };

    public static List<FluidStack> withFullVisibility(List<FluidStack> stacks) {
        return stacks.stream().map(fs -> {
            FluidStack stack = fs.copy();
            stack.setAmount(1000);
            return stack;
        }).toList();
    };
    
};
