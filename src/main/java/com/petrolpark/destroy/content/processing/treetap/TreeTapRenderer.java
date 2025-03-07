package com.petrolpark.destroy.content.processing.treetap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TreeTapRenderer extends KineticBlockEntityRenderer<TreeTapBlockEntity> {

    public TreeTapRenderer(Context context) {
        super(context);
    };

    @Override
	protected void renderSafe(TreeTapBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		//if (Backend.canUseInstancing(be.getLevel())) return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(TreeTapBlock.HORIZONTAL_FACING);
		SuperByteBuffer armRenderer = CachedBuffers.partial(DestroyPartials.TREE_TAP_ARM, state);
        armRenderer
            .center()
            .rotate(9f * Mth.sin(getAngleForBe(be, be.getBlockPos(), facing.getClockWise().getAxis())), facing.getClockWise().getAxis())
            .rotateToFace(facing.getOpposite())
            .uncenter()
            .translate(0f, 12 / 16f, 7 / 16f)
            .light(light)
            .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        renderRotatingBuffer(be, getRotatedModel(be, getRenderedBlockState(be)), ms, buffer.getBuffer(getRenderType(be, state)), light);
	};

    @Override
	protected BlockState getRenderedBlockState(TreeTapBlockEntity be) {
		return shaft(getRotationAxisOf(be));
	};
    
};
