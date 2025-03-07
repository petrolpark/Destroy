package com.petrolpark.destroy.core.chemistry.storage.measuringcylinder;

import net.createmod.catnip.data.Couple;
import org.joml.Vector3f;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.chemistry.storage.SimpleMixtureTankBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MeasuringCylinderBlockEntity extends SimpleMixtureTankBlockEntity {

    public static final Couple<Vector3f> FLUID_BOX_DIMENSIONS = Couple.create(new Vector3f(6.5f, 2.5f, 6.5f), new Vector3f(9.5f, 11f, 9.5f));

    public MeasuringCylinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tank.setCapacity(DestroyAllConfigs.SERVER.blocks.measuringCylinderCapacity.get());
    };

    @Override
    public Couple<Vector3f> getFluidBoxDimensions() {
        return FLUID_BOX_DIMENSIONS;
    };
    
};
