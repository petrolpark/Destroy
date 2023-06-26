package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class VatSideRenderer extends SafeBlockEntityRenderer<VatSideBlockEntity> {

    public VatSideRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(VatSideBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = be.getBlockState();
        VertexConsumer vb = bufferSource.getBuffer(RenderType.solid());
        float dialPivot = 5.75f / 16;

        switch (be.getDisplayType()) {
            case NORMAL: {
                break;
            } case PIPE: {
                transformed(DestroyPartials.VAT_SIDE_PIPE, state, be.direction.getOpposite())
                    .renderInto(ms, vb);
                break;
            } case BAROMETER: {
                transformed(DestroyPartials.VAT_SIDE_BAROMETER, state, be.direction.getClockWise())
                    .light(light)
                    .renderInto(ms, vb);
                transformed(AllPartialModels.BOILER_GAUGE_DIAL, state, be.direction.getClockWise())
                    .translate( 2 / 16d, 0d, 0d)
                    .translate(0, dialPivot, dialPivot)
                    .rotateX(-90 * be.getPercentagePressure())
                    .translate(0, -dialPivot, -dialPivot)
                    .light(light)
                    .renderInto(ms, vb);
                break;
            } case THERMOMETER: {
                break;
            }
        }
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBufferer.partial(model, blockState)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.unCentre();
	};
    
};
