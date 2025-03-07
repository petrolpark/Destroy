package com.petrolpark.destroy.content.processing.distillation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.Mth;

public class BubbleCapRenderer extends SmartBlockEntityRenderer<BubbleCapBlockEntity> {

    public BubbleCapRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(BubbleCapBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        TankSegment visualTank = be.getTankToRender();
        
        if (!visualTank.isEmpty(partialTicks)) {

            boolean bottom = be.getBlockState().getValue(BubbleCapBlock.BOTTOM); // Whether this Bubble Cap is at the bottom of a Tower
            boolean top = be.getBlockState().getValue(BubbleCapBlock.TOP); // Whether this Bubble Cap is at the top of a Tower

            float
            bottomLevel = 0f, // How full the bottom pipe section is
            centerLevel = 0f, // How full the main center section is
            tolevel = 0f, // How full the top pipe section is

            totalLevel = Mth.clamp(visualTank.getTotalUnits(partialTicks) / be.getTank().getCapacity(), 0, 1); // How full the Bubble cap as a whole is

            if (bottom && top) {
                centerLevel = totalLevel;
            } else if (bottom && !top) {
                centerLevel = Math.min(1, totalLevel * (13 / 10f));
                tolevel = Math.max(0, totalLevel - (10 / 13f)) * (13 / 3f);
            } else if (!bottom && !top) {
                bottomLevel = Math.min(1, totalLevel * (16 / 3f));
                centerLevel = Math.min(1, Math.max(0, (totalLevel - (3 / 16f))) * (16 / 10f));
                tolevel = Math.max(0, totalLevel - (13 / 16f)) * (16 / 3f);
            } else if (!bottom && top) {
                bottomLevel = Math.min(1, totalLevel * (13 / 3f));
                centerLevel = Math.max(0, totalLevel - (3 / 13f)) * (13 / 10f);
            };

            if (!bottom && bottomLevel > 0f) FluidRenderer.renderFluidBox(visualTank.getRenderedFluid().getFluid(), visualTank.getRenderedFluid().getAmount(), 4 / 16f, 0 / 16f, 4 / 16f, 12 / 16f, 0 / 16f + (bottomLevel * 3 / 16f), 12 / 16f, buffer, ms, light, true, true);
            if (centerLevel > 0f) FluidRenderer.renderFluidBox(visualTank.getRenderedFluid().getFluid(), visualTank.getRenderedFluid().getAmount(), 3 / 16f, 3 / 16f, 3 / 16f, 13 / 16f, 3 / 16f + (centerLevel * 10 / 16f), 13 / 16f, buffer, ms, light, true, true);
            if (!top && tolevel > 0f) FluidRenderer.renderFluidBox(visualTank.getRenderedFluid().getFluid(), visualTank.getRenderedFluid().getAmount(), 4 / 16f, 13 / 16f, 4 / 16f, 12 / 16f, 13 / 16f + (tolevel * 3 / 16f), 12 / 16f, buffer, ms, light, false, true);
        };
    };
    
};
