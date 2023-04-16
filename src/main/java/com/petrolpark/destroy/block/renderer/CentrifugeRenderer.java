package com.petrolpark.destroy.block.renderer;

import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.millstone.MillstoneRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeRenderer extends MillstoneRenderer {

    public CentrifugeRenderer(Context context) {
        super(context);
    };

    @Override
    protected SuperByteBuffer getRotatedModel(KineticTileEntity be, BlockState state) {
        return CachedBufferer.partial(DestroyPartials.CENTRIFUGE_COG, state);
    };

}
