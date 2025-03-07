package com.petrolpark.destroy.core.pollution.pollutometer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class PollutometerRenderer extends SafeBlockEntityRenderer<PollutometerBlockEntity> {

    public PollutometerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(PollutometerBlockEntity pollutometer, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

        BlockState blockState = pollutometer.getBlockState();
        float time = AnimationTickHolder.getRenderTime(pollutometer.getLevel());
        float renderTick = time + (pollutometer.hashCode() % 13) * 16f;
        VertexConsumer vc = bufferSource.getBuffer(RenderType.cutout());

        ms.pushPose();
        SuperByteBuffer anenometerBuffer = CachedBuffers.partial(DestroyPartials.POLLUTOMETER_ANEMOMETER, blockState);
        draw(anenometerBuffer, -renderTick / 8f, ms, vc, light);
        ms.popPose();

        ms.pushPose();
        SuperByteBuffer weathervaneBuffer = CachedBuffers.partial(DestroyPartials.POLLUTOMETER_WEATHERVANE, blockState);
        draw(weathervaneBuffer, Mth.PI / 4 + Mth.sin((float) ((renderTick / 16) % (2 * Math.PI))) / 24f, ms, vc, light);
        ms.popPose();
    };

    private static void draw(SuperByteBuffer buffer, float horizontalAngle, PoseStack ms, VertexConsumer vc, int light) {
		buffer.rotateCentered(horizontalAngle, Direction.UP)
			.light(light)
			.renderInto(ms, vc);
	};


};