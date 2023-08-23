package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.instance.LongShaftBlockEntity;
import com.simibubi.create.AllBlocks;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LongShaftBlock extends ShaftBlock {

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
		return face.getAxis() == state.getValue(AXIS) && (face.getAxisDirection() == AxisDirection.POSITIVE) == state.getValue(POSITIVE_AXIS_DIRECTION);
	};

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, worldIn, pos, oldState, isMoving);
        checkIfCoaxialGearStillExists(state, worldIn, pos);
    };

    protected void checkIfCoaxialGearStillExists(BlockState state, LevelReader level, BlockPos pos) {
        BlockState coaxialGearState = level.getBlockState(pos.relative(LongShaftBlockEntity.getDirection(state)));
        if (!DestroyBlocks.COAXIAL_GEAR.has(coaxialGearState) || coaxialGearState.getValue(AXIS) != state.getValue(AXIS)) {
            withBlockEntityDo(level, pos, be -> {
                be.getLevel().setBlockAndUpdate(pos, AllBlocks.SHAFT.getDefaultState().setValue(AXIS, state.getValue(AXIS)));
            });
        };
    };
    
    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        checkIfCoaxialGearStillExists(state, level, pos);
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return super.onSneakWrenched(state, context);
	};

    @Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state != newState) {
            BlockPos coaxialGearPos = pos.relative(LongShaftBlockEntity.getDirection(state));
            BlockState coaxialGearState = world.getBlockState(coaxialGearPos);
            if (DestroyBlocks.COAXIAL_GEAR.has(coaxialGearState) && !world.isClientSide() && coaxialGearState.getValue(CoaxialGearBlock.HAS_SHAFT)) {
                world.setBlockAndUpdate(coaxialGearPos, coaxialGearState.setValue(CoaxialGearBlock.HAS_SHAFT, false));
                Block.popResource(world, coaxialGearPos, AllBlocks.SHAFT.asStack());
            };
        };
		super.onRemove(state, world, pos, newState, isMoving);
	};

    @Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
		return DestroyBlockEntityTypes.LONG_SHAFT.get();
	};

    
};
