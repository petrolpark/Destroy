package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.RedstoneProgrammerBlockEntity;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class RedstoneProgrammerBlock extends HorizontalDirectionalBlock implements IBE<RedstoneProgrammerBlockEntity>, IWrenchable, ProperWaterloggedBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RedstoneProgrammerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(POWERED, false)
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false)
        );
    };

    @Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;
		if (!level.getBlockTicks().willTickThisTick(pos, this)) level.scheduleTick(pos, this, 0);
	};

    @Override
	public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
		updatePower(state, worldIn, pos);
	};

    @Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (state.getBlock() == oldState.getBlock() || isMoving) return;
		updatePower(state, worldIn, pos);
	};

    public void updatePower(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        if (state.getValue(POWERED) != level.hasNeighborSignal(pos)) level.setBlock(pos, state.cycle(POWERED), 2);
    };

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        updateWater(level, state, currentPos);
        return state;
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder
            .add(POWERED)
            .add(FACING)
            .add(WATERLOGGED);
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return withWater(defaultBlockState().setValue(FACING, context.getHorizontalDirection()), context);
    };

    @Override
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    };

    @Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

    @Override
    public Class<RedstoneProgrammerBlockEntity> getBlockEntityClass() {
        return RedstoneProgrammerBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends RedstoneProgrammerBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.REDSTONE_PROGRAMMER.get();
    };
    
};
