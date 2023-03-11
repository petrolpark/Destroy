package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.entity.DynamoBlockEntity;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DynamoBlock extends KineticBlock implements ITE<DynamoBlockEntity>, ICogWheel {

    public DynamoBlock(Properties properties) {
        super(properties);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction != Direction.UP) return 0;
        return getTileEntityOptional(level, pos).map(DynamoBlockEntity::getRedstoneSignal).orElse(0);
    };

    @Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == Direction.UP;
	};

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyShapes.DYNAMO;
    };

    @Override
    public Class<DynamoBlockEntity> getTileEntityClass() {
        return DynamoBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DynamoBlockEntity> getTileEntityType() {
        return DestroyBlockEntities.DYNAMO.get();
    };
    
}
