package com.petrolpark.destroy.client.gui.menu;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.client.gui.screen.RedstoneProgrammerScreen;
import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class DestroyMenuTypes {

    public static final MenuEntry<RedstoneProgrammerMenu> REDSTONE_PROGRAMMER = register("redstone_programmer", RedstoneProgrammerMenu::new, () -> RedstoneProgrammerScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
		String name, ForgeMenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
		return Destroy.REGISTRATE
			.menu(name, factory, screenFactory)
			.register();
	};

    public static void register() {};
    
};
