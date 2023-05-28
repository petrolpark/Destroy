package com.petrolpark.destroy.block.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.petrolpark.destroy.block.entity.PumpjackBlockEntity;
import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.core.Direction;

public class PumpjackInstance extends BlockEntityInstance<PumpjackBlockEntity> implements DynamicInstance {

    protected final ModelData cam;
	protected final ModelData linkage;
	protected final ModelData beam;
    protected final ModelData pump;

    public PumpjackInstance(MaterialManager materialManager, PumpjackBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        cam = materialManager.defaultSolid()
            .material(Materials.TRANSFORMED)
            .getModel(DestroyPartials.PUMPJACK_CAM, blockState)
            .createInstance();

        linkage = materialManager.defaultSolid()
            .material(Materials.TRANSFORMED)
            .getModel(DestroyPartials.PUMPJACK_LINKAGE, blockState)
            .createInstance();

        beam = materialManager.defaultSolid()
            .material(Materials.TRANSFORMED)
            .getModel(DestroyPartials.PUMPJACK_BEAM, blockState)
            .createInstance();

        pump = materialManager.defaultSolid()
            .material(Materials.TRANSFORMED)
            .getModel(DestroyPartials.PUMPJACK_PUMP, blockState)
            .createInstance();
    };

    @Override
    public void beginFrame() {
        //TODO
    };

    protected ModelData transformed(ModelData modelData, Direction facing) {
		return modelData.loadIdentity()
			.translate(getInstancePosition())
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.unCentre();
	}

    @Override
    protected void remove() {
        cam.delete();
        linkage.delete();
        beam.delete();
        pump.delete();
    };
    
};
