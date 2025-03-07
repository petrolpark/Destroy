package com.petrolpark.destroy.content.processing.centrifuge;


import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class CentrifugeCogVisual extends SingleAxisRotatingVisual<CentrifugeBlockEntity> {

    public CentrifugeCogVisual(VisualizationContext ctx, CentrifugeBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick, Models.partial(DestroyPartials.CENTRIFUGE_COG));
    };

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pos);
    }
}
