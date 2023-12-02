package com.petrolpark.destroy.badge;

import com.petrolpark.destroy.Destroy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class DestroyBadges {
    public static final DeferredRegister<Badge> BADGES = DeferredRegister.create(Destroy.asResource("badge"), Destroy.MOD_ID);


    public static final RegistryObject<Badge>
    
    EARLY_BIRD = BADGES.register("early_bird", Badge::new),
    DEVELOPER = BADGES.register("developer", Badge::new);

    public static void register(IEventBus eventBus) {
        BADGES.makeRegistry(RegistryBuilder::new);
        BADGES.register(eventBus);
    };

    public static RegistryObject<Badge> getBadge(String namespace, String id) {
        return getBadge(new ResourceLocation(namespace, id));
    };

    public static RegistryObject<Badge> getBadge(ResourceLocation id) {
        return BADGES.getEntries().stream().filter(badgeEntry -> badgeEntry.getId().equals(id)).findFirst().orElse(null);
    };

    public static ResourceLocation getId(Badge badge) {
        return BADGES.getEntries().stream().filter(badgeEntry -> badgeEntry.get().equals(badge)).map(RegistryObject::getId).findFirst().orElse(null);
    };
};
