package com.petrolpark.destroy.core.explosion.mixedexplosive;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosivePropertiesEntry;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosiveProperty;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosivePropertyCondition;
import com.petrolpark.destroy.core.item.tooltip.DestroyTooltipComponent;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ExplosivePropertiesTooltip extends DestroyTooltipComponent<ExplosivePropertiesTooltip, ExplosivePropertiesTooltip.ClientExplosivePropertiesTooltip>  {

    private final ExplosiveProperties properties;

    public ExplosivePropertiesTooltip(ExplosiveProperties properties) {
        this.properties = properties;
    };
    
    @Override
    public ClientExplosivePropertiesTooltip getClientTooltipComponent() {
        return new ClientExplosivePropertiesTooltip();
    };

    public class ClientExplosivePropertiesTooltip implements ClientTooltipComponent {

        @Override
        public int getHeight() {
            return 77;
        };

        @Override
        public int getWidth(Font pFont) {
            return 76;
        };

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
            PoseStack ms = guiGraphics.pose();
            ms.pushPose();
            ms.translate(x, y, 250f);
            renderProperties(properties, font, guiGraphics, -1d, -1d);
            ms.popPose();
        };

    };

    /**
     * @param properties
     * @param font
     * @param graphics
     * @param mX Mouse co-ordinate relative to the top left of where this Explosive Properties is rendered
     * @param mY
     */
    public static void renderProperties(ExplosiveProperties properties, Font font, GuiGraphics graphics, double mX, double mY) {
        PoseStack ms = graphics.pose();

        DestroyGuiTextures.CUSTOM_EXPLOSIVE_CHART.render(graphics, 0, 0);
        int y = 3;

        Selectable selected = getSelected(properties, mX, mY);
        for (Entry<ExplosiveProperty, ExplosivePropertiesEntry> entry : properties.entrySet()) {

            // Bars
            float value = entry.getValue().value;
            for (int i = 0; i < Math.abs(value); i++) {
                DestroyGuiTextures.CUSTOM_EXPLOSIVE_BAR.render(graphics, 37 + (8 + 3 * i) * (int)(Math.signum(value)), y + 3);
            };

            // Conditions
            for (ExplosivePropertyCondition condition : entry.getValue().conditions) {
                DestroyGuiTextures conditionIcon;
                float renderCenter;
                float renderOffset;
                boolean fulfilled = properties.fulfils(condition);
                if (condition.threshhold == 0f) {
                    conditionIcon = fulfilled ? DestroyGuiTextures.CUSTOM_EXPLOSIVE_FULFILLED_ZERO : DestroyGuiTextures.CUSTOM_EXPLOSIVE_UNFULFILLED_ZERO;
                    renderCenter = 38;
                    renderOffset = 6;
                } else if (condition.negative()) {
                    conditionIcon = fulfilled ? DestroyGuiTextures.CUSTOM_EXPLOSIVE_FULFILLED_LESS : DestroyGuiTextures.CUSTOM_EXPLOSIVE_UNFULFILLED_LESS;
                    renderCenter = 33 + condition.threshhold * 3f;
                    renderOffset = 4;
                } else {
                    conditionIcon = fulfilled ? DestroyGuiTextures.CUSTOM_EXPLOSIVE_FULFILLED_GREATER : DestroyGuiTextures.CUSTOM_EXPLOSIVE_UNFULFILLED_GREATER;
                    renderCenter = 43 + condition.threshhold * 3f;
                    renderOffset = 4;
                };
                ms.pushPose();
                ms.translate(renderCenter, y + 5, 0f);
                if (selected == condition) TransformStack.of(ms).scale(1.2f);
                ms.translate(-renderOffset, -6f, 0f);
                conditionIcon.render(graphics, 0, 0);
                ms.popPose();
            };

            // Symbols
            graphics.drawString(font, entry.getKey().getSymbol(), 34, y + 1, 0xFFFFFF);
            y += 15;
        };
    };

    public static Selectable getSelected(ExplosiveProperties properties, double mX, double mY) {
        if (mX < 0d || mX > 76d) return EMPTY;
        int propertyIndex = (int)((mY - 3D) / 15d);
        if (propertyIndex < 0 || propertyIndex >= ExplosiveProperty.values().length) return EMPTY;
        ExplosiveProperty property = ExplosiveProperty.values()[propertyIndex];
        for (ExplosivePropertyCondition condition : properties.get(property).conditions) {
            double minX;
            double maxX;
            if (condition.threshhold == 0f) {
                minX = 31d;
                maxX = 43d;
            } else if (condition.threshhold < 0f) {
                minX = 25f + 3d * (condition.threshhold);
                maxX = 33f + 3d * (condition.threshhold);
            } else {
                minX = 39f + 3d * (condition.threshhold);
                maxX = 47f + 3d * (condition.threshhold);
            };
            if (mX > minX && mX < maxX) return condition;
        };
        return property;
    };

    public static interface Selectable {
        public List<Component> getTooltip(ExplosiveProperties properties);
    };

    public static final Selectable EMPTY = new Selectable() {

        @Override
        public List<Component> getTooltip(ExplosiveProperties properties) {
            return Collections.emptyList();
        };

    };
};
