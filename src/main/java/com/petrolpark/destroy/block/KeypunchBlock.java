package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.KeypunchBlockEntity;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class KeypunchBlock extends HorizontalKineticBlock implements IBE<KeypunchBlockEntity>, ICogWheel {

    public KeypunchBlock(Properties properties) {
        super(properties);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

    @Override
    public Class<KeypunchBlockEntity> getBlockEntityClass() {
        return KeypunchBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends KeypunchBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.KEYPUNCH.get();
    };
    
};
