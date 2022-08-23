package com.petrolpark.destroy;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Destroy.MOD_ID);

    public static final RegistryObject<Item> PURE_GOLD_DUST = ITEMS.register("pure_gold_dust",
        () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
