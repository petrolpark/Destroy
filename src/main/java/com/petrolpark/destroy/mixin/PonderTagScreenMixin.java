package com.petrolpark.destroy.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.client.ponder.DestroyPonderTags;
import com.petrolpark.destroy.mixin.accessor.PonderTagScreenAccessor;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.ponder.ui.PonderTagScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

@Mixin(PonderTagScreen.class)
public class PonderTagScreenMixin {
    
    /**
     * Mostly copied from the {@link com.simibubi.create.foundation.ponder.ui.PonderTagScreen Create source code}.
     * @param graphics
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param ci
     */
    @Inject(method = "renderWindowForeground", at = @At(value = "HEAD"), cancellable = true)
    public void inRenderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (((PonderTagScreenAccessor)this).getTag() == DestroyPonderTags.VAT_SIDE_BLOCKS) {
            ItemStack hoveredItem = ((PonderTagScreenAccessor)this).getHoveredItem();
            if (hoveredItem.isEmpty() || DestroyBlocks.VAT_CONTROLLER.isIn(hoveredItem)) return;
            
            RenderSystem.disableDepthTest();
            PoseStack ms = graphics.pose();
            ms.pushPose();
            ms.translate(0, 0, 200);

            Minecraft minecraft = Minecraft.getInstance();
            graphics.renderTooltip(minecraft.font, DestroyLang.vatMaterialTooltip(hoveredItem), Optional.empty(), hoveredItem, mouseX, mouseY);

            ms.popPose();
            RenderSystem.enableDepthTest();
            ci.cancel();
        };
    };
};
