package com.petrolpark.destroy.content.processing.trypolithography.keypunch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class KeypunchRenderer extends KineticBlockEntityRenderer<KeypunchBlockEntity> {

    public KeypunchRenderer(Context context) {
        super(context);
    };

    @Override
	public boolean shouldRenderOffScreen(KeypunchBlockEntity be) {
		return true;
	};

    @Override
    protected void renderSafe(KeypunchBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        CircuitPunchingBehaviour behaviour = be.punchingBehaviour;
        float renderedHeadOffset = behaviour.getRenderedPistonOffset(partialTicks);

        int pistonPos = be.getActualPosition();
        SuperByteBuffer headRender = CachedBuffers.partial(DestroyPartials.KEYPUNCH_PISTON, blockState);
        headRender.translate((4 + 2 * (pistonPos % 4)) / 16f, - (6.1f + (renderedHeadOffset * 12.5f)) / 16f, (4 + 2 * (pistonPos / 4)) / 16f)
            .light(light)
            .renderInto(ms, buffer.getBuffer(RenderType.solid()));
            
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
    };

	@Override
	protected SuperByteBuffer getRotatedModel(KeypunchBlockEntity be, BlockState state) {
		return CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, state);
	};
    
};
