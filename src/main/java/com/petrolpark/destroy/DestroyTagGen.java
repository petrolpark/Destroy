package com.petrolpark.destroy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;

public class DestroyTagGen {
    public static Map<TagKey<Block>, TagKey<Item>> forgeBlockAndItem(String... forgePaths) {
        Map<TagKey<Block>, TagKey<Item>> map = new LinkedHashMap<>();

        for (String path : forgePaths) {
            ResourceLocation id = new ResourceLocation("forge", path);

            TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, id);
            TagKey<Item>  itemTag  = TagKey.create(Registries.ITEM,  id);

            map.put(blockTag, itemTag);
        }

        return map;
    }
}
