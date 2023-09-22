package com.petrolpark.destroy.entity.renderer;

import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.entity.PrimedBomb;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;

public class PrimedBombRenderer extends TntRenderer {

    protected final BlockRenderDispatcher blockRenderer;

    public PrimedBombRenderer(Context context) {
        super(context);
        blockRenderer = context.getBlockRenderDispatcher();
    };

    /**
     * Copied from the {@link net.minecraft.client.renderer.entity.TntRenderer Minecraft source code}.
     */
    @Override
    public void render(PrimedTnt entity, float entityYaw, float partialTicks, PoseStack ms, MultiBufferSource buffer, int packedLight) {
        if (!(entity instanceof PrimedBomb bombEntity)) return;
        ms.pushPose();
        ms.translate(0.0F, 0.5F, 0.0F);
        int i = bombEntity.getFuse();
        if ((float)i - partialTicks < 9f) {
            float f = 1f + (float)Math.pow(Mth.clamp(1f - ((float)i - partialTicks + 1f) / 10f, 0.0f, 1f), 3) * 0.3f; // Grow the Entity as it is about to explode
            ms.scale(f, f, f);
        };

        ms.mulPose(Axis.YP.rotationDegrees(-90f));
        ms.translate(-0.5f, -0.5f, 0.5f);
        ms.mulPose(Axis.YP.rotationDegrees(90f));
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, bombEntity.getBlockStateToRender(), ms, buffer, packedLight, i / 5 % 2 == 0);
        ms.popPose();
    }
    
};
