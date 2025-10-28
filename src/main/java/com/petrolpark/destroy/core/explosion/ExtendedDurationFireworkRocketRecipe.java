package com.petrolpark.destroy.core.explosion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.DestroyTags;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public class ExtendedDurationFireworkRocketRecipe extends CustomRecipe {

    public static final RecipeSerializer<ExtendedDurationFireworkRocketRecipe> DURATION_4_FIREWORK_ROCKET = new SimpleCraftingRecipeSerializer<>((rl, c) -> new ExtendedDurationFireworkRocketRecipe(rl, c, false));
    public static final RecipeSerializer<ExtendedDurationFireworkRocketRecipe> DURATION_5_FIREWORK_ROCKET = new SimpleCraftingRecipeSerializer<>((rl, c) -> new ExtendedDurationFireworkRocketRecipe(rl, c, true));
    
    private static final Supplier<Ingredient> CARD_INGREDIENT = () -> Ingredient.of(DestroyItems.CARD_STOCK.get());
    private static final Supplier<Ingredient> STAR_INGREDIENT = () -> Ingredient.of(Items.FIREWORK_STAR);
    private final Ingredient EXPLOSIVE_INGREDIENT;

    private final boolean secondaryExplosive;

    public ExtendedDurationFireworkRocketRecipe(ResourceLocation id, CraftingBookCategory category, boolean secondaryExplosive) {
        super(id, category);
        this.secondaryExplosive = secondaryExplosive;
        EXPLOSIVE_INGREDIENT = explosiveIngredient(secondaryExplosive);
    };

    public static final Ingredient explosiveIngredient(boolean secondaryExplosive) {
        return Ingredient.of(secondaryExplosive ? DestroyTags.Items.SECONDARY_EXPLOSIVES.tag : DestroyTags.Items.PRIMARY_EXPLOSIVES.tag);
    };

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        boolean hasCardStock = false;
        boolean hasExplosive = false;
        for(int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (CARD_INGREDIENT.get().test(stack)) {
                if (hasCardStock) return false;
                hasCardStock = true;
            } else if (EXPLOSIVE_INGREDIENT.test(stack)) {
                if (hasExplosive) return false;
                hasExplosive = true;
            } else if (!STAR_INGREDIENT.get().test(stack) && !stack.isEmpty()) {
                return false;
            };
            if (hasExplosive && hasCardStock) return true;
        };
        return false;
    };

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 10);
        CompoundTag tag = fireworkStack.getOrCreateTagElement("Fireworks");
        ListTag explosionsTag = new ListTag();

        for(int slot = 0; slot < inv.getContainerSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (STAR_INGREDIENT.get().test(stack)) {
                CompoundTag explosionTag = stack.getTagElement("Explosion");
                if (explosionTag != null) {
                    explosionsTag.add(explosionTag);
                };  
            };
        };

        tag.putByte("Flight", (byte)(secondaryExplosive ? 5 : 4));
        if (!explosionsTag.isEmpty()) {
            tag.put("Explosions", explosionsTag);
        };

        return fireworkStack;
    };

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return secondaryExplosive ? DURATION_5_FIREWORK_ROCKET : DURATION_4_FIREWORK_ROCKET;
    };

    public static List<CraftingRecipe> exampleRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>(2);
        for (boolean secondary : Iterate.trueAndFalse) {
            ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET, 10);
            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, CARD_INGREDIENT.get(), explosiveIngredient(secondary));
            int duration = (secondary ? 5 : 4);
            fireworkStack.getOrCreateTagElement("Fireworks").putByte("Flight", (byte)duration);
            recipes.add(new ShapelessRecipe(Destroy.asResource("duration_"+duration+"_firework_crafting"), "destroy.firework.duration_" + duration, CraftingBookCategory.MISC, fireworkStack, inputs));
        };
        return recipes;
    };
    
};
