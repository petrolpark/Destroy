package com.petrolpark.destroy.world.loot.providers.number;

import com.petrolpark.destroy.Destroy;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DestroyNumberProviders {

    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, Destroy.MOD_ID);

    public static final RegistryObject<LootNumberProviderType> POLLUTION = register("pollution", new PollutionNumberProvider.Serializer());

    private static RegistryObject<LootNumberProviderType> register(String name, Serializer<? extends NumberProvider> serializer) {
        return NUMBER_PROVIDERS.register(name, () -> new LootNumberProviderType(serializer));
    };

    public static void register(IEventBus eventBus) {
        NUMBER_PROVIDERS.register(eventBus);
    };
};
