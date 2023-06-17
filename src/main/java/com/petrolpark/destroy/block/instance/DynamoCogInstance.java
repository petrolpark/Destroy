package com.petrolpark.destroy.block.instance;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.petrolpark.destroy.block.entity.DynamoBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

public class DynamoCogInstance extends SingleRotatingInstance<DynamoBlockEntity> {

    public DynamoCogInstance(MaterialManager modelManager, DynamoBlockEntity blockEntity) {
        super(modelManager, blockEntity);
    };

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(DestroyPartials.DYNAMO_COG, blockEntity.getBlockState());
    };
};
