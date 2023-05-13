package com.petrolpark.destroy.world.loot;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.world.explosion.SmartExplosion;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class DestroyLootContextParams {

    public static final LootContextParam<SmartExplosion> SMART_EXPLOSION = create("smart_explosion");
    
    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(Destroy.asResource(id));
    };
};
