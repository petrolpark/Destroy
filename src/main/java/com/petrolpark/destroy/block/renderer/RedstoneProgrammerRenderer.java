package com.petrolpark.destroy.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.RedstoneProgrammerBlock;
import com.petrolpark.destroy.block.entity.RedstoneProgrammerBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RedstoneProgrammerRenderer extends SafeBlockEntityRenderer<RedstoneProgrammerBlockEntity> {

    public RedstoneProgrammerRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(RedstoneProgrammerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer cylinder = CachedBufferer.partialFacing(DestroyPartials.REDSTONE_PROGRAMMER_CYLINDER, be.getBlockState(), be.getBlockState().getValue(RedstoneProgrammerBlock.FACING));


        cylinder
            .translate(0, 5 / 16d, 10 / 16d)
            .rotateY(AnimationTickHolder.getPartialTicks())
            .translateBack(0, 5 / 16d, 10 / 16d);
        
        cylinder
            .renderInto(ms, vc);
    };
    
};
