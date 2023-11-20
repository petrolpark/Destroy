package com.petrolpark.destroy.client.gui.button;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.ItemStack;

/**
 * A button leading directly to Destroy's configurations
 */
public class OpenDestroyMenuButton extends Button {

    public static final ItemStack ICON = DestroyItems.LOGO.asStack();

	public OpenDestroyMenuButton(int x, int y) {
		super(x, y, 20, 20, Components.immutableEmpty(), OpenDestroyMenuButton::click, DEFAULT_NARRATION);
	}

    @Override
	public void renderString(GuiGraphics graphics, Font pFont, int pColor) {
		graphics.renderItem(ICON, getX() + 2, getY() + 2);
	};
	
	public static void click(Button b) {
        Minecraft minecraft = Minecraft.getInstance();
		ScreenOpener.open(new BaseConfigScreen(minecraft.screen, Destroy.MOD_ID));
	};
    
};
