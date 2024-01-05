package com.petrolpark.destroy.block.renderer;

import com.petrolpark.destroy.block.entity.KeypunchBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class KeypunchRenderer extends KineticBlockEntityRenderer<KeypunchBlockEntity> {

    public KeypunchRenderer(Context context) {
        super(context);
    };

	@Override
	protected SuperByteBuffer getRotatedModel(KeypunchBlockEntity be, BlockState state) {
		return CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, state)
            .rotateToFace(Direction.UP);
	};
    
};
