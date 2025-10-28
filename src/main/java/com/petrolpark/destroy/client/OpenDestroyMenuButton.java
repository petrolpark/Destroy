package com.petrolpark.destroy.client;

import java.util.function.Supplier;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableObject;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.mixin.accessor.MenuRowsAccessor;
import com.simibubi.create.infrastructure.gui.OpenCreateMenuButton.MenuRows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * A button leading directly to Destroy's configurations
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class OpenDestroyMenuButton extends Button {

    public static final Supplier<ItemStack> ICON = DestroyItems.LOGO::asStack;

	public OpenDestroyMenuButton(int x, int y) {
		super(x, y, 20, 20, Component.empty(), OpenDestroyMenuButton::click, DEFAULT_NARRATION);
	};

    @Override
	public void renderString(GuiGraphics graphics, Font pFont, int pColor) {
		graphics.renderItem(ICON.get(), getX() + 2, getY() + 2);
	};
	
	public static void click(Button b) {
        Minecraft minecraft = Minecraft.getInstance();
		ScreenOpener.open(new BaseConfigScreen(minecraft.screen, Destroy.MOD_ID));
	};

	/**
     * Add buttons to open Destroy's configurations to the main and pause menus.
     * All copied from the {@link com.simibubi.create.infrastructure.gui.OpenCreateMenuButton.OpenConfigButtonHandler#onGuiInit Create source code}.
     * @param event
     */
    @SubscribeEvent
    public static final void onGuiInit(ScreenEvent.Init event) {
        Screen gui = event.getScreen();

        MenuRows menu = null;
        int rowIdx = 0;
        int offsetX = 0;

        if (gui instanceof TitleScreen) {
            menu = MenuRows.MAIN_MENU;
            rowIdx = DestroyAllConfigs.CLIENT.configurationButtons.mainMenuConfigButtonRow.get();
            offsetX =  DestroyAllConfigs.CLIENT.configurationButtons.mainMenuConfigButtonOffsetX.get();
        } else if (gui instanceof PauseScreen) {
            menu = MenuRows.INGAME_MENU;
            rowIdx =  DestroyAllConfigs.CLIENT.configurationButtons.pauseMenuConfigButtonRow.get();
            offsetX =  DestroyAllConfigs.CLIENT.configurationButtons.pauseMenuConfigButtonOffsetX.get();
        };

        if (rowIdx != 0 && menu != null) {
            boolean onLeft = offsetX < 0;
            String target =  I18n.get((onLeft ? ((MenuRowsAccessor)menu).getLeftButtons() : ((MenuRowsAccessor)menu).getRightButtons()).get(rowIdx - 1));

            int offsetX_ = offsetX;
            MutableObject<GuiEventListener> toAdd = new MutableObject<>(null);
            event.getListenersList()
                .stream()
                .filter(w -> w instanceof AbstractWidget)
                .map(w -> (AbstractWidget) w)
                .filter(w -> w.getMessage()
                    .getString()
                    .equals(target)
                ).findFirst()
                .ifPresent(w ->
                    toAdd.setValue(new OpenDestroyMenuButton(w.getX() + offsetX_ + (onLeft ? -20 : w.getWidth()), w.getY()))
                );
            if (toAdd.getValue() != null) event.addListener(toAdd.getValue());
        };
    };
    
};
