package com.petrolpark.destroy.core.block.entity.instance;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;

import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class HorizontalShaftlessCogwheelVisual extends SingleAxisRotatingVisual<KineticBlockEntity> {

    public HorizontalShaftlessCogwheelVisual(VisualizationContext ctx, KineticBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick, Models.partial(AllPartialModels.SHAFTLESS_COGWHEEL));
    };
    
};
