package com.petrolpark.destroy.block.instance;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.block.partial.DestroyBlockPartials;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;

public class CentrifugeCogInstance extends SingleRotatingInstance {

    public CentrifugeCogInstance(MaterialManager modelManager, CentrifugeBlockEntity tile) {
        super(modelManager, tile);
    };

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(DestroyBlockPartials.CENTRIFUGE_COG, blockEntity.getBlockState());
    }
}
