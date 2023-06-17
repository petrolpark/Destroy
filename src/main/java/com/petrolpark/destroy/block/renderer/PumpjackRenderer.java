package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.PumpjackBlock;
import com.petrolpark.destroy.block.entity.PumpjackBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackRenderer extends SafeBlockEntityRenderer<PumpjackBlockEntity> {

    public PumpjackRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(PumpjackBlockEntity pumpjack, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        //if (Backend.canUseInstancing(pumpjack.getLevel())) return; Can't use instancing because it can't render cutout for some reason

        Float angle = pumpjack.getTargetAngle();
		if (angle == null) return;

        BlockState blockState = pumpjack.getBlockState();
        Direction facing = PumpjackBlock.getFacing(blockState);
        VertexConsumer vbSolid = bufferSource.getBuffer(RenderType.solid());
        VertexConsumer vbCutout = bufferSource.getBuffer(RenderType.cutout());

        transformed(DestroyPartials.PUMPJACK_CAM, blockState, facing)
            .translate(0d, 0d, 1d)
            .centre()
            .rotateXRadians(angle - Mth.HALF_PI)
            .centre()
            .translate(0d, 0d, -1d)
            .unCentre()
            .unCentre()
            .light(light)
			.renderInto(ms, vbSolid);

        transformed(DestroyPartials.PUMPJACK_LINKAGE, blockState, facing)
            .translate(0d, -5 / 16d, 1d)
            .translate(0d, Mth.sin(angle) * 5 / 16d, -Mth.cos(angle) * 5 / 16d)
            .centre()
            .rotateX(Mth.cos(angle) * 10d)
            .centre()
            .translate(0d, 0d, -1d)
            .unCentre()
            .unCentre()
            .light(light)
			.renderInto(ms, vbSolid);

        transformed(DestroyPartials.PUMPJACK_BEAM, blockState, facing)
            .translate(0d, 1d, 0d)
            .centre()
            .rotateX((Mth.sin(angle) - 1) * -20d)
            .centre()
            .translate(0d, -1d, 0d)
            .unCentre()
            .unCentre()
            .light(light)
			.renderInto(ms, vbCutout);

        transformed(DestroyPartials.PUMPJACK_PUMP, blockState, facing)
            .translate(0d, (3 / 16) - (Mth.sin(angle) * 3 / 16d), 0d)
            .light(light)
			.renderInto(ms, vbSolid);
        
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBufferer.partial(model, blockState)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.rotateX(AngleHelper.verticalAngle(facing))
			.unCentre();
	};
	
	@Override
	public int getViewDistance() {
		return 128;
	};
    
};
