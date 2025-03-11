package com.petrolpark.destroy.compat.jei.category;

import java.util.List;

import com.petrolpark.compat.jei.category.PetrolparkRecipeCategory;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.Lang;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

public class CartographyTableCategory extends PetrolparkRecipeCategory<CartographyTableCategory.CartographyTableRecipe> {

    private final Minecraft mc;

    private static final ResourceLocation seismographRecipeId = Destroy.asResource("seismograph");
    private static final ResourceLocation scalingRecipeId = new ResourceLocation("scaling");
    private static final ResourceLocation lockingRecipeId = new ResourceLocation("locking");

    public static final List<CartographyTableRecipe> getAllRecipes() {
        ItemStack map = new ItemStack(Items.FILLED_MAP);
        map.getOrCreateTag().putInt("map", 52);
        ItemStack scaledMap = map.copy();
        scaledMap.getOrCreateTag().putInt("map", 53);
        return List.of(
            new CartographyTableRecipe(seismographRecipeId, DestroyItems.SEISMOGRAPH.asStack(), map, DestroyItems.SEISMOMETER),
            new CartographyTableRecipe(new ResourceLocation("duplicating"), map.copyWithCount(2), map, Items.MAP),
            new CartographyTableRecipe(scalingRecipeId, scaledMap, map, Items.PAPER),
            new CartographyTableRecipe(lockingRecipeId, map, map, Items.GLASS_PANE)
        );
    };

    public CartographyTableCategory(Info<CartographyTableRecipe> info, IJeiHelpers helpers) {
        super(info, helpers);
        mc = Minecraft.getInstance();
    };

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CartographyTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.INPUT, 21, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addIngredients(recipe.getIngredients().get(1))
            .addRichTooltipCallback((view, tt) -> { if (recipe.getId().equals(seismographRecipeId)) tt.add(CreateLang.translateDirect("recipe.deploying.not_consumed").withStyle(ChatFormatting.GOLD)); });

        builder.addSlot(RecipeIngredientRole.OUTPUT, 107, 2)
            .setBackground(getRenderedSlot(), -1, -1)
            .addItemStack(recipe.getResultItem(mc.level.registryAccess()))
            .addRichTooltipCallback((view, tt) -> {
                if (recipe.getId().equals(scalingRecipeId)) tt.add(DestroyLang.translate("recipe.cartography_table.scaled").style(ChatFormatting.GOLD).component());
                else if (recipe.getId().equals(lockingRecipeId)) tt.add(Component.translatable("filled_map.locked", 52).withStyle(ChatFormatting.GRAY));
            });
    };

    @Override
    public void draw(CartographyTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 36, 6);
    };

    public static class CartographyTableRecipe extends ShapelessRecipe {

        public CartographyTableRecipe(ResourceLocation id, ItemStack result, ItemStack mapIngredient, ItemLike nonMapIngredient) {
            super(id, "", CraftingBookCategory.MISC, result, NonNullList.of(Ingredient.EMPTY, Ingredient.of(mapIngredient), Ingredient.of(nonMapIngredient)));
        };

    };
};
