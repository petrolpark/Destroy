package com.petrolpark.destroy.content.processing.trypolithography;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.core.item.tooltip.DestroyTooltipComponent;
import com.petrolpark.util.BinaryMatrix4x4;

import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class CircuitPatternTooltipComponent extends DestroyTooltipComponent<CircuitPatternTooltipComponent, CircuitPatternTooltipComponent.ClientCircuitPatternTooltip> {

    private final int pattern;
    private final boolean tileBorderOnTop;
    private final ScreenElement border;
    private final ScreenElement tile;
    private final ScreenElement tileBorder;

    public CircuitPatternTooltipComponent(int pattern, boolean tileBorderOnTop, ScreenElement border, ScreenElement tile, ScreenElement tileBorder) {
        this.pattern = pattern;
        this.tileBorderOnTop = tileBorderOnTop;
        this.border = border;
        this.tile = tile;
        this.tileBorder = tileBorder;
    };

    @Override
    public ClientCircuitPatternTooltip getClientTooltipComponent() {
        return new ClientCircuitPatternTooltip();
    };
    
    public class ClientCircuitPatternTooltip implements ClientTooltipComponent {

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
            PoseStack ms = guiGraphics.pose();
            ms.pushPose();
            ms.translate(x, y, 250f);
            renderCircuitMask(guiGraphics, pattern, tileBorderOnTop, border, tile, tileBorder);
            ms.popPose();
        };

        @Override
        public int getHeight() {
            return 58;
        };

        @Override
        public int getWidth(Font pFont) {
            return 48;
        };

    };

    public static void renderCircuitMask(GuiGraphics graphics, int pattern, boolean tileBorderOnTop, ScreenElement border, ScreenElement tile, ScreenElement tileBorder) {
        if (tileBorderOnTop) border.render(graphics, 0, 0);
        for (int i = 0; i < 16; i++) {
            if (BinaryMatrix4x4.is1(pattern, i)) continue;
            tileBorder.render(graphics, 7 + (i % 4) * 8, 7 + (i / 4) * 8);
        };
        if (!tileBorderOnTop) border.render(graphics, 0, 0);
        for (int i = 0; i < 16; i++) {
            if (BinaryMatrix4x4.is1(pattern, i)) continue;
            tile.render(graphics, 8 + (i % 4) * 8, 8 + (i / 4) * 8);
        };
    };
};
