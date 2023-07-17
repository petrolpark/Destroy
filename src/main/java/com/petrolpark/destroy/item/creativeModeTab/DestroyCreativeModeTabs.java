package com.petrolpark.destroy.item.creativeModeTab;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyItems;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DestroyCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Destroy.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("base",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.destroy.base"))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.icon(() -> DestroyItems.TEST_TUBE.asStack())
			.build()
    );

    public static void register(IEventBus modEventBus) {
		TABS.register(modEventBus);
	};
};
