package com.petrolpark.destroy.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.AgingBarrelBlock;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.compat.jei.animation.GUIBlockRenderer;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Pair;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;

public class AgingCategory extends DestroyRecipeCategory<AgingRecipe> {

    private static final GUIBlockRenderer blockRenderer = new GUIBlockRenderer();

    public AgingCategory(Info<AgingRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
    };

    /**
     * Most of this is all copied from {@link com.simibubi.create.compat.jei.category.BasinCategory BasinCategory}.
     */
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AgingRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

		int size = condensedIngredients.size() ; // The +1 is for the mandatory single Fluid; size should have a maximum of 3
        int xOffset = 8 + (size < 3 ? (3 - size) * 19 / 2 : 0); // Move the list of inputs to the left depending on how many there are (so they look central)
		int i = 1; // Start at one as we automatically count the Fluid Input

        // Add the Fluid Ingredient
        FluidIngredient fluidIngredient = recipe.getRequiredFluid();
        builder.addSlot(RecipeIngredientRole.INPUT, xOffset, 33)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
            .addTooltipCallback(addFluidTooltip(fluidIngredient.getRequiredAmount()));

        // Add the Item Ingredient(s)
        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
            // Generate the possible Item Stacks
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            };

            // Add the Item Ingredient
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset + (i % 3) * 19, 33)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(stacks);
            i++;
        };

        // Add the Fluid result
        FluidStack resultantFluid = recipe.getFluidResults().get(0);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 142, 35)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(resultantFluid))
            .addTooltipCallback(addFluidTooltip(resultantFluid.getAmount()));
    };

    @Override
    @SuppressWarnings("resource")
    public void draw(AgingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        AllGuiTextures.JEI_DOWN_ARROW.render(stack, 136, 14);
        AllGuiTextures.JEI_SHADOW.render(stack, 81, 50);

        // Render Aging Barrel
        stack.pushPose();
        stack.translate(getBackground().getWidth() / 2 + 4, 51, 0);
        blockRenderer.renderBlock(DestroyBlocks.AGING_BARREL.getDefaultState()
            .setValue(AgingBarrelBlock.IS_OPEN, true)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
        stack, 23);
        stack.popPose();

        // Render duration text
        DestroyGuiTextures.JEI_TEXT_BOX_LONG.render(stack, 4, 63);
        int seconds = (recipe.getProcessingDuration() % 1200) / 20;
        Minecraft.getInstance().font.draw(stack, DestroyLang.translate("tooltip.aging_barrel.aging_time", ""+ recipe.getProcessingDuration() / 1200 + ":" + (seconds < 10 ? "0" : "") + seconds).string(), 9,
				69, 0xFFFFFF);

    };
    
};
