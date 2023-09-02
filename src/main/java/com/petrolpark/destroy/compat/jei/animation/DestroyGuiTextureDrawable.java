package com.petrolpark.destroy.compat.jei.animation;

import com.petrolpark.destroy.client.gui.DestroyGuiTextures;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class DestroyGuiTextureDrawable implements IDrawable {

    private final DestroyGuiTextures texture;

    private DestroyGuiTextureDrawable(DestroyGuiTextures texture) {
        this.texture = texture;
    };

    public static DestroyGuiTextureDrawable of(DestroyGuiTextures texture) {
        return new DestroyGuiTextureDrawable(texture);
    };

    @Override
    public int getWidth() {
        return texture.width;
    };

    @Override
    public int getHeight() {
        return texture.height;
    };

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        texture.render(guiGraphics, xOffset, yOffset);
    };
    
};
