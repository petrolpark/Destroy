package com.petrolpark.destroy.content.processing.trypolithography.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitPatternItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class CircuitPatternIngredient extends AbstractIngredient {

    public static final Serializer SERIALIZER = new Serializer();

    protected final CircuitPatternItem item;
    protected final ResourceLocation patternRL;

    public CircuitPatternIngredient(Item item, ResourceLocation patternRL) {
        if (!(item instanceof CircuitPatternItem patternItem)) throw new IllegalArgumentException("Circuit pattern item ingredients must be able to have circuit patterns");
        this.item = patternItem;
        this.patternRL = patternRL;
    };

    @Override
    public boolean test(ItemStack stack) {
        if (!stack.getItem().equals(item)) return false;
        Integer pattern = Destroy.CIRCUIT_PATTERN_HANDLER.getPattern(patternRL);
        return (pattern != null && pattern == CircuitPatternItem.getPattern(stack));
    };

    @Override
    public ItemStack[] getItems() {
        Integer pattern = Destroy.CIRCUIT_PATTERN_HANDLER.getPattern(patternRL);
        if (pattern == null) return new ItemStack[]{};
        ItemStack stack = new ItemStack(item);
        CircuitPatternItem.putPattern(stack, pattern);
        return new ItemStack[]{stack};
    };

    @Override
    public boolean isSimple() {
        return false;
    };

    @Override
    public boolean isEmpty() { return false; };

    @Override
    public IIngredientSerializer<CircuitPatternIngredient> getSerializer() {
        return SERIALIZER;
    };

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(SERIALIZER).toString());
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(item).toString());
        json.addProperty("pattern", patternRL.toString());
        return json;
    };

    public static class Serializer implements IIngredientSerializer<CircuitPatternIngredient> {

        @Override
        public CircuitPatternIngredient parse(FriendlyByteBuf buffer) {
            return new CircuitPatternIngredient(buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS), buffer.readResourceLocation());
        };

        @Override
        public CircuitPatternIngredient parse(JsonObject json) {
            if (json.has("item")) {
                if (json.has("pattern")) {
                    return new CircuitPatternIngredient(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true), new ResourceLocation(GsonHelper.getAsString(json, "pattern")));
                } else {
                    throw new JsonSyntaxException("Must specify the ID of a circuit pattern");
                }
            } else {
                throw new JsonSyntaxException("Must specify an item");
            }
        };

        @Override
        public void write(FriendlyByteBuf buffer, CircuitPatternIngredient ingredient) {
            buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, ingredient.item);
            buffer.writeResourceLocation(ingredient.patternRL);
        };

    };
    
};
