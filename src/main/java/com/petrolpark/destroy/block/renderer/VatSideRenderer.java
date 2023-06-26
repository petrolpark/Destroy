package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
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
        VertexConsumer vbSolid = bufferSource.getBuffer(RenderType.solid());

        switch (be.getDisplayType()) {
            case NORMAL: {
                break;
            } case PIPE: {
                transformed(DestroyPartials.VAT_SIDE_PIPE, state, be.direction)
                    .renderInto(ms, vbSolid);
                break;
            } case BAROMETER: {
                break;
            } case THERMOMETER: {
                break;
            }
        }
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBufferer.partial(model, blockState)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing.getOpposite()))
			.unCentre();
	};
    
};
