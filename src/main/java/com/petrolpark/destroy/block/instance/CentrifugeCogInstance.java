package com.petrolpark.destroy.block.instance;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;

public class CentrifugeCogInstance extends SingleRotatingInstance {

    public CentrifugeCogInstance(MaterialManager modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
    };

    @Override
    public void updateLight() {
        super.updateLight();
        relight(pos);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(DestroyPartials.CENTRIFUGE_COG, blockEntity.getBlockState());
    };
}
