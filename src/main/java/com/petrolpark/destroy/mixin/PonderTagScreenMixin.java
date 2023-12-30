package com.petrolpark.destroy.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
     * This replaces the Tooltips of Blocks and Items in the Vat Materials category to show their Vat Material
     * properties.
     * @param graphics
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     * @param ci
     */
    @Inject(
        method = "Lcom/simibubi/create/foundation/ponder/ui/PonderTagScreen;renderWindowForeground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inRenderWindowForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (((PonderTagScreenAccessor)this).getTag() == DestroyPonderTags.VAT_SIDE_BLOCKS) {
            ItemStack hoveredItem = ((PonderTagScreenAccessor)this).getHoveredItem();
            if (hoveredItem.isEmpty()) return;
            
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
