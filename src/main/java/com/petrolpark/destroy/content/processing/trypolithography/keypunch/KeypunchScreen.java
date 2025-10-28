package com.petrolpark.destroy.content.processing.trypolithography.keypunch;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.util.GuiHelper;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class KeypunchScreen extends AbstractSimiScreen {

    private final KeypunchBlockEntity keypunch;
    private int selectedPosition;

    private DestroyGuiTextures background;

    private EditBox nameBox;
    private List<IconButton> buttons;
    private IconButton confirmButton;

    public KeypunchScreen(KeypunchBlockEntity keypunch) {
        background = DestroyGuiTextures.KEYPUNCH;
        selectedPosition = keypunch.getPistonPosition();
        this.keypunch = keypunch;
    };

    @Override
	protected void init() {
		setWindowSize(background.width, background.height);
		super.init();
		clearWidgets();

        buttons = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            IconButton button = new IconButton(guiLeft + 52 + (i % 4) * 19, guiTop + 40 + (i / 4) * 19, GuiHelper.NOTHING);
            int position = i;
            button.withCallback(() -> {
                buttons.forEach(b -> b.active = true);
                button.active = false;
                selectedPosition = position;
                DestroyMessages.sendToServer(new ChangeKeypunchPositionC2SPacket(keypunch.getBlockPos(), position));
            });
            button.active = !(selectedPosition == i);
            buttons.add(button);
            addRenderableWidget(button);
        };

		nameBox = new EditBox(new NoShadowFontWrapper(font), guiLeft + 23, guiTop + 4, background.width - 20, 10, Component.literal(keypunch.name));
		nameBox.setBordered(false);
		nameBox.setMaxLength(25);
		nameBox.setTextColor(0x592424);
		nameBox.setValue(keypunch.name);
		nameBox.setFocused(false);
		nameBox.mouseClicked(0, 0, 0);
		nameBox.setResponder(s -> nameBox.setX(nameBoxX(s, nameBox)));
		nameBox.setX(nameBoxX(nameBox.getValue(), nameBox));
		addRenderableWidget(nameBox);

        confirmButton = new IconButton(guiLeft + background.width - 33, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer();}); // It thinks minecraft and player might be null
		addRenderableWidget(confirmButton);
    };

    @Override
    public void tick() {
        super.tick();
        if (getFocused() != nameBox) {
			nameBox.setCursorPosition(nameBox.getValue().length());
			nameBox.setHighlightPos(nameBox.getCursorPosition());
		};
    };

    @Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (nameBox != null && !nameBox.isFocused() && pMouseY > guiTop && pMouseY < guiTop + 14 && pMouseX > guiLeft && pMouseX < guiLeft + background.width) {
			nameBox.setFocused(true);
			nameBox.setHighlightPos(0);
			setFocused(nameBox);
			return true;
		};
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	};

    @Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		boolean hitEnter = getFocused() instanceof EditBox && (pKeyCode == InputConstants.KEY_RETURN || pKeyCode == InputConstants.KEY_NUMPADENTER);

		if (hitEnter && nameBox.isFocused()) {
			nameBox.setFocused(false);
			syncName();
			return true;
		};

		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	};

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);
        if (nameBox != null) {
            String name = nameBox.getValue();
            if (!nameBox.isFocused()) AllGuiTextures.STATION_EDIT_NAME.render(graphics, nameBoxX(name, nameBox) + font.width(name) + 5, guiTop + 1);
        };
    };

    @Override
	public void removed() {
		super.removed();
		if (nameBox == null) return;
		syncName();
	};

    public void syncName() {
        if (!nameBox.getValue().equals(keypunch.name)){
            keypunch.name = nameBox.getValue();
            DestroyMessages.sendToServer(new NameKeypunchC2SPacket(keypunch.getBlockPos(), nameBox.getValue()));
        };
    };

    /**
     * Copied from {@link com.simibubi.create.content.trains.station.StationScreen Create source code}.
     * @param s
     * @param nameBox
     */
    private int nameBoxX(String s, EditBox nameBox) {
		return guiLeft + background.width / 2 - (Math.min(font.width(s), nameBox.getWidth()) + 10) / 2;
	};
    
};
