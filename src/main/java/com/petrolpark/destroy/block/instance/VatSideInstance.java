package com.petrolpark.destroy.block.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity.DisplayType;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class VatSideInstance extends BlockEntityInstance<VatSideBlockEntity> implements DynamicInstance {

    private static final float dialPivot = 5.75f / 16;

    protected final ModelData pipe;
    protected final ModelData thermometer;
    protected final ModelData barometer;
    protected final ModelData barometerDial;

    public VatSideInstance(MaterialManager materialManager, VatSideBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        BlockState state = blockEntity.getBlockState();

        pipe = getTransformMaterial()
            .getModel(DestroyPartials.VAT_SIDE_PIPE, state)
            .createInstance();
        
        thermometer = getTransformMaterial()
            .getModel(DestroyPartials.VAT_SIDE_THERMOMETER, state)  
            .createInstance();

        barometer = getTransformMaterial()
            .getModel(DestroyPartials.VAT_SIDE_BAROMETER, state)
            .createInstance();

        barometerDial = getTransformMaterial()
            .getModel(AllPartialModels.BOILER_GAUGE_DIAL)
            .createInstance();
    };

    @Override
    public void beginFrame() {
        if (blockEntity.getController() == null) return;
        switch (blockEntity.getDisplayType()) {
            case PIPE:
                transformed(pipe, blockEntity.direction.getOpposite());
                return;
            case THERMOMETER:
                transformed(thermometer, blockEntity.direction.getOpposite());
                return;
            case BAROMETER:
                transformed(barometer, blockEntity.direction.getOpposite())
                    .centre()
                    .rotateY(90)
                    .unCentre();
                transformed(barometerDial, blockEntity.direction.getOpposite())
                    .centre()
                    .rotateY(90)
                    .unCentre()
                    .translate(2 / 16d, 0d, 0d)
                    .translate(0, dialPivot, dialPivot)
                    .rotateX(-90 * blockEntity.getPercentagePressure())
                    .translate(0, -dialPivot, -dialPivot);
                return;
            default:
                return;
        }
    };

    @Override
    public void update() {
        
    };

    protected ModelData transformed(ModelData modelData, Direction direction) {
		return modelData.loadIdentity()
			.translate(getInstancePosition())
			.centre()
            .rotateY(AngleHelper.horizontalAngle(direction))
            .rotateX(AngleHelper.verticalAngle(direction))
			.unCentre();
	};

    @Override
	public void updateLight() {
		relight(pos, pipe, thermometer, barometer);
	};

    @Override
    protected void remove() {
        pipe.delete();
        thermometer.delete();
        barometer.delete();
        barometerDial.delete();
    };

};
