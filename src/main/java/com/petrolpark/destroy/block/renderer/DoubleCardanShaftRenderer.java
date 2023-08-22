package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.entity.DoubleCardanShaftBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class DoubleCardanShaftRenderer extends KineticBlockEntityRenderer<DoubleCardanShaftBlockEntity> {

    public DoubleCardanShaftRenderer(Context context) {
        super(context);
    };

     @Override
    protected void renderSafe(DoubleCardanShaftBlockEntity doubleCardanShaftBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(doubleCardanShaftBlockEntity.getLevel())) return;
    };
    
};
