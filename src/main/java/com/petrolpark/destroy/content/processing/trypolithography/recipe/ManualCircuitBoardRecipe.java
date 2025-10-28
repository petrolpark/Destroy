package com.petrolpark.destroy.content.processing.trypolithography.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.destroy.DestroyRecipeTypes;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitPatternItem;
import com.petrolpark.recipe.manualonly.ManualOnlyShapedRecipe;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ManualCircuitBoardRecipe extends ManualOnlyShapedRecipe {

    private static class MaskIngredient extends Ingredient {

        private final Item maskItem;

        protected MaskIngredient(Item maskItem) {
            super(Stream.empty());
            this.maskItem = maskItem;
        };

        @Override
        public ItemStack[] getItems() {
            ItemStack stack = new ItemStack(maskItem);
            stack.getOrCreateTag().putBoolean("HideContaminants", true);
            CircuitPatternItem.putPattern(stack, CircuitDeployerApplicationRecipe.EXAMPLE_PATTERN);
            return new ItemStack[]{stack};
        };
    };  

    protected final Item maskItem;
    protected final int[] maskPositions;

    private ManualCircuitBoardRecipe(ResourceLocation id, int width, int height, NonNullList<Ingredient> recipeItems, Item maskItem, int[] maskPositions, ItemStack result) {
        super(id, "", CraftingBookCategory.MISC, width, height, recipeItems, result);
        if (!(maskItem instanceof CircuitPatternItem)) throw new IllegalArgumentException("Mask ingredient must be able to support circuit patterns.");
        if (!(result.getItem() instanceof CircuitPatternItem)) throw new IllegalArgumentException("Result item must be able to support circuit patterns.");
        this.maskItem = maskItem;
        this.maskPositions = maskPositions;
    };
    
    public static Map<String, Ingredient> keyFromJson(Item maskItem, JsonObject keyEntry) {
        Map<String, Ingredient> map = new HashMap<>();

        for(Map.Entry<String, JsonElement> entry : keyEntry.entrySet()) {
            if (entry.getKey().length() != 1) throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            if ("#".equals(entry.getKey())) throw new JsonSyntaxException("Invalid key entry: '#' is reserved for the Circuit Mask(s).");
            if (" ".equals(entry.getKey())) throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue(), false));
        };

        map.put("#", new MaskIngredient(maskItem));
        map.put(" ", Ingredient.EMPTY);
        return map;
    };

    @Override
    public RecipeSerializer<?> getSerializer() {
        return DestroyRecipeTypes.CIRCUIT_BOARD_MANUAL_CRAFTING.getSerializer();
    };

    @Override
    public ItemStack getExampleResult() {
        ItemStack stack = getResultItem(null);
        CircuitPatternItem.putPattern(stack, CircuitDeployerApplicationRecipe.EXAMPLE_PATTERN);
        return stack;
    };

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        return !assemble(inv, level.registryAccess()).isEmpty();
    };

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(inv, registryAccess).copy();
        // Check each position for the recipe within the container
        for (int i = 0; i <= inv.getWidth() - getWidth(); i++) {
            tryEachPosition: for (int j = 0; j <= inv.getHeight() - getHeight(); j++) {
                // Try both mirrored and non-mirrored
                for (boolean mirrored : Iterate.trueAndFalse) {
                    int thisPattern = -1;
                    // Test each item
                    for (int column = 0; column < inv.getWidth(); column++) {
                        for (int row = 0; row < inv.getHeight(); row++) {
                            int x = column - i;
                            int y = row - j;
                            Ingredient ingredient = Ingredient.EMPTY;
                            if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
                                if (mirrored) ingredient = getIngredients().get(getWidth() - x - 1 + y * getWidth());
                                else ingredient = getIngredients().get(x + y * getWidth());
                            };
            
                            ItemStack stack = inv.getItem(column + row * inv.getWidth());
                            if (ingredient instanceof MaskIngredient) {
                                if (!stack.getItem().equals(maskItem)) continue tryEachPosition;
                                int pattern = CircuitPatternItem.getPattern(stack);
                                if (thisPattern == -1) thisPattern = pattern;
                                else if (thisPattern != pattern) continue tryEachPosition;
                            } else {
                                if (!ingredient.test(stack)) continue tryEachPosition;
                            };
                        };
                    };
                    if (thisPattern == -1) continue tryEachPosition;
                    CircuitPatternItem.putPattern(result, thisPattern);
                    return result;
                };
            };
        };
        return ItemStack.EMPTY;
    };

    public static class Serializer implements RecipeSerializer<ManualCircuitBoardRecipe> {

        @Override
        @SuppressWarnings("deprecation")
        public ManualCircuitBoardRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            // Mask Ingredient
            String maskItemId = GsonHelper.getAsString(json, "mask");
            Item maskItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(maskItemId)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + maskItemId + "'"));
            
            // Other Ingredients and positions
            Map<String, Ingredient> map = keyFromJson(maskItem, GsonHelper.getAsJsonObject(json, "key"));
            String[] rows = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int x = rows[0].length();
            int y = rows.length;
            NonNullList<Ingredient> list = ShapedRecipe.dissolvePattern(rows, map, x, y);

            // Mask Ingredient positions
            List<Integer> maskPositions = new ArrayList<>(x * y);
            for (int i = 0; i < x * y; i++) if (list.get(i) instanceof MaskIngredient) maskPositions.add(i);
            if (maskPositions.isEmpty()) throw new JsonSyntaxException("Recipe must contain at least one mask");

            // Result
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            return new ManualCircuitBoardRecipe(recipeId, x, y, list, maskItem, maskPositions.stream().mapToInt(i -> i).toArray(), result);
        };

        @Override
        @SuppressWarnings("deprecation")
        public ManualCircuitBoardRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            Item maskItem = buffer.readById(BuiltInRegistries.ITEM);
            ItemStack result = buffer.readItem();
            NonNullList<Ingredient> list = NonNullList.withSize(i * j, Ingredient.EMPTY);
            int[] maskPositions = buffer.readVarIntArray(i * j);

            addEachIngredient: for (int k = 0; k < list.size(); k++) {
                for (int position : maskPositions) {
                    if (position == k) {
                        list.set(k, new MaskIngredient(maskItem));
                        continue addEachIngredient;
                    };
                };
                list.set(k, Ingredient.fromNetwork(buffer));
            };

            return new ManualCircuitBoardRecipe(recipeId, i, j, list, maskItem, maskPositions, result);
        };

        @Override
        @SuppressWarnings("deprecation")
        public void toNetwork(FriendlyByteBuf buffer, ManualCircuitBoardRecipe recipe) {
            buffer.writeVarInt(recipe.getWidth());
            buffer.writeVarInt(recipe.getHeight());
            buffer.writeId(BuiltInRegistries.ITEM, recipe.maskItem);
            buffer.writeItem(recipe.getResultItem(null));
            buffer.writeVarIntArray(recipe.maskPositions);

            int i = 0;
            writeEachIngredient: for (Ingredient ingredient : recipe.getIngredients()) {
                for (int position : recipe.maskPositions) {
                    if (position == i) {
                        i++;
                        continue writeEachIngredient;
                    }
                };
                ingredient.toNetwork(buffer);
                i++;
            };
        };
    };

};