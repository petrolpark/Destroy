package com.petrolpark.destroy;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Destroy.MOD_ID);

    // PLASTICS
    public static final RegistryObject<Item> POLYETHENE_TEREPHTHALATE = ITEMS.register("polyethene_terephthalate",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYVINYL_CHLORIDE = ITEMS.register("polyvinyl_chloride",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYETHENE = ITEMS.register("polyethene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYPROPENE = ITEMS.register("polypropene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYSTYRENE = ITEMS.register("polystyrene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ABS = ITEMS.register("abs",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYTETRAFLUOROETHENE = ITEMS.register("polytetrafluoroethene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NYLON = ITEMS.register("nylon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYSTYRENE_BUTADIENE = ITEMS.register("polystyrene_butadiene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> RUBBER = ITEMS.register("rubber",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    // RAW METALS
    public static final RegistryObject<Item> PURE_GOLD_DUST = ITEMS.register("pure_gold_dust",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
