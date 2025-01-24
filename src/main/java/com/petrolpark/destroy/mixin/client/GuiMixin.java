package com.petrolpark.destroy.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.config.DestroyClientConfigs;
import com.petrolpark.destroy.entity.player.ExtendedInventory;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    abstract Player getCameraPlayer();

    @Shadow
    public abstract void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed);

    private static final ResourceLocation DESTROY_GUI_ICONS_LOCATION = Destroy.asResource("textures/gui/hud_icons.png");

    @Inject(
        method = "renderHeart",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inRenderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, int yOffset, boolean renderHighlight, boolean halfHeart, CallbackInfo ci) {
        if ("CHEMICAL_POISON".equals(heartType.name())) {
            guiGraphics.blit(DESTROY_GUI_ICONS_LOCATION, x, y, heartType.getX(halfHeart, renderHighlight), yOffset, 9, 9);
            ci.cancel();
        };
    };

    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private void renderOffhandItemOffset(Gui gui, GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed) {
        Player player = getCameraPlayer();
        if (pStack == pPlayer.getOffhandItem()) { // If we're rendering the offhand Item
            ExtendedInventory inv = ExtendedInventory.get(getCameraPlayer());
            pGuiGraphics.pose().pushPose();
            if (player.getMainArm().getOpposite() == HumanoidArm.LEFT) {
                pX -= 20 * DestroyClientConfigs.getLeftSlots(inv.getExtraHotbarSlots());
            } else {
                pX += 20 * DestroyClientConfigs.getRightSlots(inv.getExtraHotbarSlots());
            };
            pGuiGraphics.pose().popPose();
        };
        renderSlot(pGuiGraphics, pX, pY, pPartialTick, pPlayer, pStack, pSeed);
    };

    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
        )
    )
    private void renderOffhandBackgroundOffset(GuiGraphics pGuiGraphics, ResourceLocation pAtlasLocation, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        if ((pUOffset == 24 || pUOffset == 53) && pVOffset == 22 && pUWidth == 29 && pVHeight == 24) { // If we're rendering the offhand background
            Player player = getCameraPlayer();
            ExtendedInventory inv = ExtendedInventory.get(getCameraPlayer());
            pGuiGraphics.pose().pushPose();
            if (player.getMainArm().getOpposite() == HumanoidArm.LEFT) {
                pX -= 20 * DestroyClientConfigs.getLeftSlots(inv.getExtraHotbarSlots());
            } else {
                pX += 20 * DestroyClientConfigs.getRightSlots(inv.getExtraHotbarSlots());
            };
            pGuiGraphics.pose().popPose();
        };
        pGuiGraphics.blit(pAtlasLocation, pX, pY, pUOffset, pVOffset, pUWidth, pVHeight);
    };
};
