package com.petrolpark.destroy.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.entity.PollutometerBlockEntity;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PollutometerRenderer extends SafeTileEntityRenderer<PollutometerBlockEntity> {

    public PollutometerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(PollutometerBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        // TODO Auto-generated method stub
    };


};