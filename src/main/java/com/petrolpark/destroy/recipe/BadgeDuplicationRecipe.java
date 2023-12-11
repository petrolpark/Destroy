package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.item.BadgeItem;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

@MoveToPetrolparkLibrary
public class BadgeDuplicationRecipe extends CustomRecipe {

    public static final RecipeSerializer<BadgeDuplicationRecipe> BADGE_DUPLICATION = new SimpleCraftingRecipeSerializer<>(BadgeDuplicationRecipe::new);

    public BadgeDuplicationRecipe(ResourceLocation rl, CraftingBookCategory category) {
        super(rl, category);
    };

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        ItemStack badge = null;
        ItemStack duplicationStack = null;
        for(int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (stack.getItem() instanceof BadgeItem && badge == null) {
                badge = stack;
            } else if (duplicationStack == null) {
                duplicationStack = stack;
            } else {
                return false;
            };
        };
        if (badge == null) return false;
        Ingredient duplicationIngredient = ((BadgeItem)badge.getItem()).badge.get().getDuplicationIngredient();
        return duplicationIngredient != null && duplicationIngredient.test(duplicationStack);
    };

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (stack.getItem() instanceof BadgeItem) return stack;
        };
        return ItemStack.EMPTY; // Shouldn't be called
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BADGE_DUPLICATION;
    };
    
};
