package com.petrolpark.destroy.block.renderer;

import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeRenderer extends KineticBlockEntityRenderer<CentrifugeBlockEntity> {

    public CentrifugeRenderer(Context context) {
        super(context);
    };

    @Override
    protected SuperByteBuffer getRotatedModel(CentrifugeBlockEntity be, BlockState state) {
        return CachedBufferer.partial(DestroyPartials.CENTRIFUGE_COG, state);
    };

}
