package com.petrolpark.destroy.mixin;

import java.util.Optional;

import net.createmod.ponder.foundation.ui.PonderTagScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.client.DestroyPonderTags;
import com.petrolpark.destroy.mixin.accessor.PonderTagScreenAccessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

@Mixin(PonderTagScreen.class)
public class PonderTagScreenMixin {
    
    /**
     * Mostly copied from the {@link net.createmod.ponder.foundation.ui.PonderTagScreen Create source code}.
     * This replaces the Tooltips of Blocks and Items in the Vat Materials category to show their Vat Material
     * properties.
     * @param graphics
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param ci
     */
    @Inject(
        method = "Lnet/createmod/ponder/foundation/ui/PonderTagScreen;renderWindowForeground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inRenderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (DestroyPonderTags.VAT_SIDE_BLOCKS.equals(((PonderTagScreenAccessor)this).getTag().getId())) {
            ItemStack hoveredItem = ((PonderTagScreenAccessor)this).getHoveredItem();
            if (hoveredItem.isEmpty()) return;
            
            RenderSystem.disableDepthTest();
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(0, 0, 200);

            Minecraft minecraft = Minecraft.getInstance();
            graphics.renderTooltip(minecraft.font, DestroyLang.vatMaterialTooltip(hoveredItem, DestroyLang.GRAYS), Optional.empty(), hoveredItem, mouseX, mouseY);

            ms.popPose();
            RenderSystem.enableDepthTest();
            ci.cancel();
        };
    };
};
