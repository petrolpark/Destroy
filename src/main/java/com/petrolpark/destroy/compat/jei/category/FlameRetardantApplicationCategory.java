package com.petrolpark.destroy.compat.jei.category;

import java.util.Collections;
import java.util.List;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.content.product.fireretardant.FireproofingHelper;
import com.petrolpark.destroy.content.product.fireretardant.FlameRetardantApplicationRecipe;
import com.simibubi.create.compat.jei.category.animations.AnimatedSpout;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FlameRetardantApplicationCategory extends PetrolparkRecipeCategory<FlameRetardantApplicationRecipe> {

    private final AnimatedSpout spout = new AnimatedSpout();
    private final List<ItemStack> exampleItems;
    private final Minecraft mc = Minecraft.getInstance();

    public FlameRetardantApplicationCategory(Info<FlameRetardantApplicationRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        exampleItems = List.of(new ItemStack(Items.STICK), new ItemStack(Items.SHULKER_BOX), new ItemStack(Items.IRON_PICKAXE));
    };

    @Override
	public void setRecipe(IRecipeLayoutBuilder builder, FlameRetardantApplicationRecipe recipe, IFocusGroup focuses) {
        boolean example = focuses.getFocuses(VanillaTypes.ITEM_STACK, RecipeIngredientRole.INPUT).findAny().isEmpty();
        List<ItemStack> items = example ? exampleItems : focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient).toList();

        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
		    .setBackground(getRenderedSlot(), -1, -1)
			.addItemStacks(items)
            .addRichTooltipCallback((v, t) -> { if (example) t.add(DestroyLang.translate("recipe.fireproofing.info").style(ChatFormatting.GOLD).component()); });
		builder.addSlot(RecipeIngredientRole.INPUT, 27, 32)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(recipe.getRequiredFluid().getMatchingFluidStacks()))
            .addRichTooltipCallback(addFluidTooltip(recipe.getRequiredFluid().getRequiredAmount()));
		builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 132, 51)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStacks(items.stream().map(i -> {
                ItemStack result = i.copy();
                FireproofingHelper.apply(mc.level, result);
                return result;
            }).toList());
	};

	@Override
	public void draw(FlameRetardantApplicationRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
		AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);
		spout.withFluids(recipe.getRequiredFluid().getMatchingFluidStacks()).draw(graphics, getBackground().getWidth() / 2 - 13, 22);
	};
    
};
