package com.petrolpark.destroy.core.explosion.mixedexplosive;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.client.DestroyPartials;
import com.petrolpark.destroy.core.explosion.PrimedBombEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;

public class MixedExplosiveEntityRenderer extends PrimedBombEntityRenderer<MixedExplosiveEntity> {

    public MixedExplosiveEntityRenderer(Context context) {
        super(context);
    };

    @Override
    public void renderBlock(MixedExplosiveEntity entity, PoseStack ms, MultiBufferSource buffer, int light, int fuse) {
        BlockState state = entity.getBlockStateToRender();
        VertexConsumer vc = buffer.getBuffer(Sheets.cutoutBlockSheet());
        SuperByteBuffer base = CachedBuffers.partial(DestroyPartials.CUSTOM_EXPLOSIVE_MIX_BASE, state)
            .disableDiffuse()
            .light(light)
            .color(entity.color);

        SuperByteBuffer label = CachedBuffers.partial(DestroyPartials.CUSTOM_EXPLOSIVE_MIX_OVERLAY, state)
            .center()
            .light(light);

        if (fuse / 5 % 2 == 0) {
            int overlay = OverlayTexture.pack(OverlayTexture.u(1f), OverlayTexture.WHITE_OVERLAY_V);
            base.overlay(overlay);
            label.overlay(overlay);
        };

        if (entity.hasCustomName()) MixedExplosiveBlockEntityRenderer.renderTruncated(ms, buffer, d -> light, entity.getCustomName().getString());

        base.renderInto(ms, vc);
        label.renderInto(ms, vc);
    };
    
};
