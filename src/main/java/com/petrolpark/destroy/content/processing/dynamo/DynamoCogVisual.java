package com.petrolpark.destroy.content.processing.dynamo;

import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class DynamoCogVisual extends SingleAxisRotatingVisual<KineticBlockEntity> {

    public DynamoCogVisual(VisualizationContext ctx, KineticBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick, Models.partial(blockEntity.getBlockState().getValue(DynamoBlock.ARC_FURNACE) ? DestroyPartials.ARC_FURNACE_SHAFT : DestroyPartials.DYNAMO_SHAFT));
    };

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pos);
    };
};
