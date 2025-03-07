package com.petrolpark.destroy.core.chemistry.vat.observation;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public abstract class AbstractQuantityObservingScreen extends AbstractSimiScreen {
    
    protected final RedstoneQuantityMonitorBehaviour quantityBehaviour;

    protected final DestroyGuiTextures background;
        
    protected EditBox lowerBound;
    protected EditBox upperBound;

    protected IconButton confirmButton;

    protected AbstractQuantityObservingScreen(RedstoneQuantityMonitorBehaviour quantityBehaviour, Component title, DestroyGuiTextures background) {
        super(title);
        this.quantityBehaviour = quantityBehaviour;
        this.background = background;
    };

    protected abstract int getEditBoxY();

    protected int getTitleX() {
        return 16;
    };

    protected abstract void onThresholdChange(boolean upper, float newValue);

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
		super.init();
		clearWidgets();

        confirmButton = new IconButton(guiLeft + background.width - 25, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {
            onClose();
            if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer();
        }); // It thinks minecraft and player might be null
		addRenderableWidget(confirmButton);

        lowerBound = new EditBox(minecraft.font, guiLeft + 15, guiTop + getEditBoxY(), 70, 10, Component.literal(""+quantityBehaviour.upperThreshold));
        lowerBound.setBordered(false);
        lowerBound.setMaxLength(35);
		lowerBound.setFocused(false);
		lowerBound.mouseClicked(0, 0, 0);
		lowerBound.active = false;
        lowerBound.setTooltip(Tooltip.create(DestroyLang.translate("tooltip.vat.menu.quantity_observed.minimum").component()));

        upperBound = new EditBox(minecraft.font, guiLeft + 171, guiTop + getEditBoxY(), 70, 10, Component.literal(""+quantityBehaviour.lowerThreshold));
        upperBound.setBordered(false);
        upperBound.setMaxLength(35);
		upperBound.setFocused(false);
		upperBound.mouseClicked(0, 0, 0);
		upperBound.active = false;
        upperBound.setTooltip(Tooltip.create(DestroyLang.translate("tooltip.vat.menu.quantity_observed.maximum").component()));

        addRenderableWidgets(lowerBound, upperBound);
    };

    @Override
    public void tick() {
        super.tick();
        for (EditBox box : List.of(lowerBound, upperBound)) {
            if (getFocused() != box) {
                box.setCursorPosition(box.getValue().length());
                box.setHighlightPos(box.getCursorPosition());

                // Attempt to update the Vat Side with the given number
                boolean upper = box == upperBound;
                float oldValue = upper ? quantityBehaviour.upperThreshold : quantityBehaviour.lowerThreshold;
                try {
                    float value = Float.valueOf(box.getValue());
                    if (value != oldValue) onThresholdChange(upper, value);
                } catch (NumberFormatException e) {
                    box.setValue(""+oldValue);
                };
            };
        };
    };

    @Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof EditBox && (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER)) for (EditBox box : List.of(lowerBound, upperBound)) {
            if (box.isFocused()) {
                box.setFocused(false);
                return true;
            };
        };
		return super.keyPressed(keyCode, scanCode, modifiers);
	};

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (EditBox box : List.of(lowerBound, upperBound)) {
            if (mouseY > guiTop + getEditBoxY() - 8 && mouseY < guiTop + getEditBoxY() + 6 && mouseX > box.getX() - 4 && mouseX < box.getX() + box.getWidth() + 8 && !box.isFocused()) {
                box.setFocused(true);
                box.setHighlightPos(0);
                setFocused(box);
                return true;
            } else {
                box.setFocused(false);
            };
        };
        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        background.render(graphics, guiLeft, guiTop);
        graphics.drawString(font, title, guiLeft + getTitleX(), guiTop + 4, 0x828c97, false);
        Component currentValue = quantityBehaviour.getLabelledQuantity();
        graphics.drawString(font, currentValue, guiLeft + background.width / 2 - font.width(currentValue) / 2, guiTop + background.height - 18, AllGuiTextures.FONT_COLOR, false);
    };
};
