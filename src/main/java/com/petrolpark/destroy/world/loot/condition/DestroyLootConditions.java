package com.petrolpark.destroy.world.loot.condition;

import com.petrolpark.destroy.Destroy;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DestroyLootConditions {

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, Destroy.MOD_ID);

    public static final RegistryObject<LootItemConditionType>
    OBLITERATION = registerLootCondition("obliteration", new ObliterationCondition.Serializer());

    private static RegistryObject<LootItemConditionType> registerLootCondition(String name, Serializer<? extends LootItemCondition> serializer) {
        return LOOT_CONDITIONS.register(name, () -> new LootItemConditionType(serializer));
    };

    public static void register(IEventBus eventBus) {
        LOOT_CONDITIONS.register(eventBus);
    };
};
