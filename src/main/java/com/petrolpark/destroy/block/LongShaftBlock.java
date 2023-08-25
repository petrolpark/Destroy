package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.LongShaftBlockEntity;
import com.simibubi.create.content.contraptions.ITransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LongShaftBlock extends ShaftBlock implements ITransformableBlock {

    public static final BooleanProperty POSITIVE_AXIS_DIRECTION = BooleanProperty.create("positive_axis_direction");

    public LongShaftBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POSITIVE_AXIS_DIRECTION, true));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(POSITIVE_AXIS_DIRECTION);
        super.createBlockStateDefinition(builder);
    };

    @Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (!state.hasProperty(POSITIVE_AXIS_DIRECTION)) return false;
		return face.getAxis() == state.getValue(AXIS) && (face.getAxisDirection() == AxisDirection.POSITIVE) != state.getValue(POSITIVE_AXIS_DIRECTION);
	};

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    };
    
    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (tryRemoveBracket(context)) return InteractionResult.SUCCESS;
		return super.onSneakWrenched(state, context);
	};

    @Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, world, pos, newState, isMoving);
	};

    @Override
	public BlockState rotate(BlockState state, Rotation rot) {
        return getStateForDirection(rot.rotate(LongShaftBlockEntity.getDirection(state)));
	};

    @Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return getStateForDirection(mirror.mirror(LongShaftBlockEntity.getDirection(state)));
	};

    @Override
	public BlockState transform(BlockState state, StructureTransform transform) {
        return getStateForDirection(transform.mirrorFacing(transform.rotateFacing(LongShaftBlockEntity.getDirection(state))));
	};

    public BlockState getStateForDirection(Direction direction) {
        return defaultBlockState().setValue(AXIS, direction.getAxis()).setValue(POSITIVE_AXIS_DIRECTION, direction.getAxisDirection() == AxisDirection.POSITIVE);
    };

    @Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
		return DestroyBlockEntityTypes.LONG_SHAFT.get();
	};

    
};
