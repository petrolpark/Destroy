package com.petrolpark.destroy.block.entity;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleCardanShaftBlockEntity extends SplitShaftBlockEntity {

    public DoubleCardanShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
	public float getRotationSpeedModifier(Direction face) {
		if (hasSource()) {
            if (face == getSourceFacing()) return 1;
            if (getSourceFacing().getAxisDirection() == face.getAxisDirection()) return -1;
		};
		return 1;
	};

    @Override
    protected boolean isNoisy() {
        return false;
    };
    
};
