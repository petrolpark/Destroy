package com.petrolpark.destroy.item.compostable;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class DestroyCompostables {

    private static Map<ItemLike, Float> DESTROY_COMPOSTABLES;

    // Shouldn't be called until it's time to register
    static {
        DESTROY_COMPOSTABLES = new HashMap<>();
        add(0.75f, DestroyItems.HEFTY_BEETROOT.get());
        add(0.85f, DestroyItems.COAL_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.COPPER_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.DIAMOND_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.EMERALD_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.FLUORITE_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.GOLD_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.IRON_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.LAPIS_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.NICKEL_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.REDSTONE_INFUSED_BEETROOT.get());
        add(0.85f, DestroyItems.ZINC_INFUSED_BEETROOT.get());
        add(0.75f, DestroyItems.BIFURICATED_CARROT.get());
        add(0.75f, DestroyItems.POTATE_O.get());
        add(0.7f, DestroyItems.MASHED_POTATO.get());
        add(0.4f, DestroyItems.YEAST.get());
        add(0.6f, DestroyItems.AGAR.get());
        add(1.0f, DestroyBlocks.AGAR_BLOCK.get());
        add(1.0f, DestroyBlocks.MASHED_POTATO_BLOCK.get());
        add(1.0f, DestroyBlocks.YEAST_COVERED_AGAR_BLOCK.get());
    };

    private static void add(float chance, ItemLike item) {
        DESTROY_COMPOSTABLES.put(item.asItem(), chance);
    };

    @SuppressWarnings("deprecation")
    public static void register() {
        DESTROY_COMPOSTABLES.forEach((itemLike, chance) -> {
            ComposterBlock.COMPOSTABLES.put(itemLike.asItem(), chance);
        });
    };
};
