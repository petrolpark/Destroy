package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.Destroy;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@Mixin(Gui.class)
public abstract class GuiMixin {

    private static final ResourceLocation DESTROY_GUI_ICONS_LOCATION = Destroy.asResource("textures/gui/hud_icons.png");

    @Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
    private void inRenderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, int yOffset, boolean renderHighlight, boolean halfHeart, CallbackInfo ci) {
        if (heartType.name().equals("CHEMICAL_POISON")) {
            guiGraphics.blit(DESTROY_GUI_ICONS_LOCATION, x, y, heartType.getX(halfHeart, renderHighlight), yOffset, 9, 9);
            ci.cancel();
        };
    };
};
