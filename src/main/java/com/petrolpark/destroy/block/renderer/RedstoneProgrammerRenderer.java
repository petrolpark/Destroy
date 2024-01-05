package com.petrolpark.destroy.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.RedstoneProgrammerBlock;
import com.petrolpark.destroy.block.entity.RedstoneProgrammerBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class RedstoneProgrammerRenderer extends SafeBlockEntityRenderer<RedstoneProgrammerBlockEntity> {

    public RedstoneProgrammerRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(RedstoneProgrammerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        Direction direction = be.getBlockState().getValue(RedstoneProgrammerBlock.FACING);
        VertexConsumer vc = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer cylinder = CachedBufferer.partial(DestroyPartials.REDSTONE_PROGRAMMER_CYLINDER, be.getBlockState())
            .centre()
            .rotateY(AngleHelper.horizontalAngle(direction))
            .unCentre();


        cylinder
            .translate(0, 6 / 16d, 10 / 16d)
            .rotateX(AnimationTickHolder.getRenderTime())
            .translateBack(0, 6 / 16d, 10 / 16d);
        
        cylinder
            .renderInto(ms, vc);
    };
    
};
