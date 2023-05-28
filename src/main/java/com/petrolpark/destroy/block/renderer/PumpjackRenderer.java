package com.petrolpark.destroy.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.entity.PumpjackBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PumpjackRenderer extends SafeBlockEntityRenderer<PumpjackBlockEntity> {

    public PumpjackRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(PumpjackBlockEntity pumpjack, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderSafe'");
    };
    
};
