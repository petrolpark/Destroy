package com.petrolpark.destroy.content.processing.sieve;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalSieveRenderer extends KineticBlockEntityRenderer<MechanicalSieveBlockEntity> {

    public MechanicalSieveRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(MechanicalSieveBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState state = be.getBlockState();
        boolean x = state.getValue(MechanicalSieveBlock.X);
        VertexConsumer vc = buffer.getBuffer(RenderType.cutoutMipped());
        float angle = getAngleForBe(be, be.getBlockPos(), x ? Axis.X : Axis.Z);

        ms.pushPose();
        if (x) TransformStack.of(ms)
            .rotateYDegrees(90);
        ms.pushPose();

        ms.translate(Mth.sin(angle) * 2 / 16d + (x ? -1d : 0d), 0d , 0d);
        CachedBuffers.partial(DestroyPartials.MECHANICAL_SIEVE, state)
            .renderInto(ms, vc);

        ms.pushPose();
        TransformStack.of(ms)
            .center()
            .rotateZ(-angle)
            .uncenter();
        CachedBuffers.partial(DestroyPartials.MECHANICAL_SIEVE_LINKAGES, state)
            .renderInto(ms, vc);
        ms.popPose();


        ms.popPose();
        ms.popPose();

    };

    @Override
    protected SuperByteBuffer getRotatedModel(MechanicalSieveBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacingVertical(DestroyPartials.MECHANICAL_SIEVE_SHAFT, state, state.getValue(MechanicalSieveBlock.X) ? Direction.EAST : Direction.SOUTH);
    };
    
};
