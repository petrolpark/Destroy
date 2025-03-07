package com.petrolpark.destroy.core.chemistry.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ElementTankRenderer extends SafeBlockEntityRenderer<ElementTankBlockEntity> {

    public ElementTankRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(ElementTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!be.getRenderedFluid().isEmpty()) FluidRenderer.renderFluidBox(be.getRenderedFluid().getFluid(), be.getRenderedFluid().getAmount(), 1 / 16f, 1 / 16f, 1 / 16f, 15 / 16f, (1f + 14f * be.getFluidLevel(partialTicks)) / 16f, 15 / 16f, bufferSource, ms, light, true, true);
    };
    
};
