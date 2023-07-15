package com.petrolpark.destroy.world.loot;

import com.petrolpark.destroy.world.loot.condition.DestroyLootConditions;
import com.petrolpark.destroy.world.loot.providers.number.DestroyNumberProviders;

import net.minecraftforge.eventbus.api.IEventBus;

public class DestroyLoot {
    
    public static void register(IEventBus eventBus) {
        DestroyLootConditions.register(eventBus);
        DestroyNumberProviders.register(eventBus);
    };
};
