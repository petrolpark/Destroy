package com.petrolpark.destroy.content.oil.pumpjack;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackRenderer extends SafeBlockEntityRenderer<PumpjackBlockEntity> {

    public PumpjackRenderer(BlockEntityRendererProvider.Context context) {}

    private static final double beamRotation = Math.asin(5 / 17d);

    @Override
    protected void renderSafe(PumpjackBlockEntity pumpjack, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        //if (Backend.canUseInstancing(pumpjack.getLevel())) return; Can't use instancing because it can't render cutout for some reason

        Float angle = pumpjack.getTargetAngle();
		if (angle == null) angle = 0f;

        BlockState blockState = pumpjack.getBlockState();
        Direction facing = PumpjackBlock.getFacing(blockState);
        VertexConsumer vbSolid = bufferSource.getBuffer(RenderType.solid());
        VertexConsumer vbCutout = bufferSource.getBuffer(RenderType.cutout());

        transformed(DestroyPartials.PUMPJACK_CAM, blockState, facing)
            .translate(0d, 0d, 1d)
            .center()
            .rotateX(angle - Mth.HALF_PI)
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter()
            .light(light)
			.renderInto(ms, vbSolid);

        transformed(DestroyPartials.PUMPJACK_LINKAGE, blockState, facing)
            .translate(0d, -4.5 / 16d, 1d)
            .translate(0d, Mth.sin(angle) * 5 / 16d, -Mth.cos(angle) * 5 / 16d)
            .center()
            .rotateX((float)((Mth.cos(angle)) * beamRotation * 0.73d))
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter()
            .light(light)
			.renderInto(ms, vbSolid);

        transformed(DestroyPartials.PUMPJACK_BEAM, blockState, facing)
            .translate(0d, 1d, 0d)
            .center()
            .rotateX((float)((Mth.sin(angle)) * -beamRotation))
            .center()
            .translate(0d, -1d, 0d)
            .uncenter()
            .uncenter()
            .light(light)
			.renderInto(ms, vbCutout);

        transformed(DestroyPartials.PUMPJACK_PUMP, blockState, facing)
            .translate(0d, (3 / 16d) - (Mth.sin(angle) * 3 / 16d), 0d)
            .light(light)
			.renderInto(ms, vbSolid);
        
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBuffers.partial(model, blockState)
			.center()
			.rotateYDegrees(AngleHelper.horizontalAngle(facing))
			.rotateXDegrees(AngleHelper.verticalAngle(facing))
			.uncenter();
	};
	
	@Override
	public int getViewDistance() {
		return 128;
	};
    
};
