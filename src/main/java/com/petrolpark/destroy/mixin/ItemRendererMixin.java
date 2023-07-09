package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.item.renderer.ILayerTintsWithAlphaItem;
import com.petrolpark.destroy.mixin.accessor.ItemRendererAccessor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * Mostly copied from the {@link net.minecraft.client.renderer.entity.ItemRenderer Minecraft source code}, allowing colored Item layers to have alphas.
     */
    @Overwrite
    public void renderQuadList(PoseStack pPoseStack, VertexConsumer pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay) {
        boolean stackExists = !pItemStack.isEmpty();
        PoseStack.Pose pose = pPoseStack.last();

        for(BakedQuad bakedQuad : pQuads) {
            int color = -1;
            if (stackExists && bakedQuad.isTinted()) {
                color = ((ItemRendererAccessor)this).getItemColors().getColor(pItemStack, bakedQuad.getTintIndex());
            };

            float a = pItemStack.getItem() instanceof ILayerTintsWithAlphaItem ? (float)(color >> 24 & 255) / 255.0f : 1f;
            float r = (float)(color >> 16 & 255) / 255.0f;
            float g = (float)(color >> 8 & 255) / 255.0f;
            float b = (float)(color & 255) / 255.0f;
            pBuffer.putBulkData(pose, bakedQuad, r, g, b, a, pCombinedLight, pCombinedOverlay, true);
        };
    };
};
