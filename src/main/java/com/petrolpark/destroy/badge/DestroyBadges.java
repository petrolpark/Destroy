package com.petrolpark.destroy.badge;

import com.petrolpark.destroy.Destroy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class DestroyBadges {
    public static final DeferredRegister<Badge> BADGES = DeferredRegister.create(Destroy.asResource("badge"), Destroy.MOD_ID);

    public static final RegistryObject<Badge> DEVELOPER = BADGES.register("developer", Badge::new);

    public static void register(IEventBus eventBus) {
        BADGES.makeRegistry(RegistryBuilder::new);
        BADGES.register(eventBus);
    };

    public static RegistryObject<Badge> getBadgeFromId(ResourceLocation id) {
        return BADGES.getEntries().stream().filter(badgeEntry -> badgeEntry.getId().equals(id)).findFirst().orElse(null);
    };
};
