package com.petrolpark.destroy.client.stackedtextbox;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;

import net.createmod.catnip.data.Pair;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@MoveToPetrolparkLibrary
public class StackedTextBox extends AbstractStackedTextBox {

    private static final int PERMANENCE_LIFETIME = 15;

    private FontHelper.Palette palette;
    private String plainText = "";

    private Area activationArea;
    private boolean isActivationAreaHovered;

    /**
     * A list of Activation Areas and the translation keys of the definitions they will show
     */
    private List<Pair<Area, String>> possibleChildActivationAreas;

    private int lifetime;

    private final List<Component> lines;

    /**
     * @param minecraft
     * @param x This may not be respected if the text in this box requires that it be repositioned to fit on the screen
     * @param y Ditto
     * @param parent
     */
    public StackedTextBox(Minecraft minecraft, int x, int y, AbstractStackedTextBox parent) {
        super(x, y, parent, AbstractStackedTextBox.NOTHING);

        this.minecraft = minecraft;

        palette = FontHelper.Palette.GRAY_AND_WHITE;

        activationArea = new Area(x, y, width, height);
        isActivationAreaHovered = true; // Should always be true when first opened, but this is set just in case

        possibleChildActivationAreas = List.of();

        lifetime = 0;
        lines = new ArrayList<>();
    };

    public StackedTextBox withPalette(FontHelper.Palette palette) {
        this.palette = palette;
        return this;
    };

    public StackedTextBox withActivationArea(Area area) {
        activationArea = area;
        return this;
    };

    public StackedTextBox withText(String text) {
        plainText = text;
        updateTextBoxSize(text);
        return this;
    };

    protected void updateTextBoxSize(String text) {
        LinesAndActivationAreas result = getTextAndActivationAreas(text, getX(), getY(), 200, minecraft.screen, minecraft.font, palette, true);

        lines.clear();
        lines.addAll(result.lines());
        possibleChildActivationAreas = result.areas();
        width = result.width();
        height = result.height();

        // Move the text box if necessary to fit it on the screen
        setX(result.startX());
        setY(result.startY());
    };

    @Override
    protected void beforeRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.beforeRender(guiGraphics, mouseX, mouseY, partialTicks);
        isActivationAreaHovered = activationArea.isIn(mouseX, mouseY);

        // If there is a potential child to render
        if (child == AbstractStackedTextBox.NOTHING && lifetime >= PERMANENCE_LIFETIME) {
            for (Pair<Area, String> pair : possibleChildActivationAreas) {
                Area area = pair.getFirst();
                if (area.isIn(mouseX, mouseY)) {
                    child = new StackedTextBox(minecraft, mouseX, mouseY, this)
                        .withActivationArea(area)
                        .withPalette(palette)
                        .withText(Component.translatable(pair.getSecond()).getString());
                };
            };
        };
    };

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // Don't render if the mouse isn't in the right place
        if (!isActive()) return;

        PoseStack ms = guiGraphics.pose();
        ms.pushPose();
        ms.translate(0, 0, 300);

        // Render the text
        Screen screen = minecraft.screen;
        if (screen != null) {
            // Check if the screen has been resized and reposition if so
            if (getX() + width > screen.width || getY() + height > screen.height) {
                updateTextBoxSize(plainText);
                return;
            };
            ms.translate(0, 0, 10);
            List<Component> allLines = new ArrayList<>(lines);
            allLines.add(progressBar());
            RemovedGuiUtils.drawHoveringText(guiGraphics, allLines, getX() - 22, getY() + 5, screen.width, screen.height, -1, minecraft.font);
        };

        // Render the next text box in the chain (if it exists)
        ms.pushPose();
        ms.translate(0, 0, 1);
        child.render(guiGraphics, mouseX, mouseY, partialTicks);
        ms.popPose();

        ms.popPose();
    };
    
    @Override
    public void tick() {
        super.tick();
        if (lifetime < PERMANENCE_LIFETIME) {
            lifetime++;
        };
        if (!isActive()) close();
        child.tick();
    };

    @Override
    public boolean isActive() {
        if (!active || !visible) return false;
        if (child.isActive()) return true;
        if (lifetime < PERMANENCE_LIFETIME) {
            return isActivationAreaHovered;
        } else {
            return isHovered || isActivationAreaHovered;
        }
    };

    @Override
    public void close() {
        lifetime = 0;
        child.close();
        if (parent != AbstractStackedTextBox.NOTHING) parent.child = AbstractStackedTextBox.NOTHING;
    };

    private Component progressBar() {
        float charWidth = minecraft.font.width("|");

        int total = (int) ((width - 5) / charWidth);
        int current = (int) ((float)lifetime * total / (float)PERMANENCE_LIFETIME);

        String bars = "";
        bars += ChatFormatting.GRAY + Strings.repeat("|", current);
        bars += ChatFormatting.DARK_GRAY + Strings.repeat("|", total - current);
        return Component.literal(bars);
    };
};
