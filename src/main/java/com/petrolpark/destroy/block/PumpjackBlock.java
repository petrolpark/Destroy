package com.petrolpark.destroy.block;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.entity.PumpjackBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PumpjackBlock extends Block implements IBE<PumpjackBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PumpjackBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };

    public static Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    };

    public static BlockPos getCamPos(BlockState state, BlockPos pos) {
		return pos.relative(getFacing(state), -1);
	};

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getClockWise(Axis.Y);
        for (BlockPos pos : getStructuralBlocks(facing, context.getClickedPos()).keySet()) {
            // Don't place if the structural Blocks won't be able to fit
            if (!context.getLevel().getBlockState(pos).getMaterial().isReplaceable()) return null;
        };
        return defaultBlockState().setValue(FACING, facing);
    };

    @Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!level.getBlockTicks().hasScheduledTick(pos, this)) level.scheduleTick(pos, this, 1);
	};

    @Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Direction from the center of the Pumpjack to the drilling head
		Direction facing = state.getValue(FACING);

        // Check all the structural blocks are still present
        for (Entry<BlockPos, Direction> entry : getStructuralBlocks(facing, pos).entrySet()) {
            // What the State at the given position is
            BlockState occupiedState = level.getBlockState(entry.getKey());
            // What the State at the given position should be
            BlockState requiredState = DestroyBlocks.PUMPJACK_STRUCTURAL.getDefaultState().setValue(PumpjackStructuralBlock.FACING, entry.getValue());
            if (occupiedState == requiredState) continue;
            // If we can't put the structural Block State here, destroy the whole Pumpjack
            if (!occupiedState.getMaterial().isReplaceable()) {
				level.destroyBlock(pos, false);
				return;
			}
			level.setBlockAndUpdate(entry.getKey(), requiredState);
        };
	};

    /**  
     * Map of locations of structural Blocks to the direction they should be pointing to conserve the structure.
     */
    public Map<BlockPos, Direction> getStructuralBlocks(Direction facing, BlockPos pos) {
        /* 
         *  O---O---O---O
         *  |5 v|3 v|2 v|
         *  O---O---O---O
         *  |4 >| c |1 <|
         *  O---O---O---O
         * 'c' is this Block State; each box shows the direction the structural Block State points.
         */
        return Map.of(
            pos.relative(facing, 1), facing.getOpposite(), // 1
            pos.relative(facing, 1).above(), Direction.DOWN, // 2
            pos.above(), Direction.DOWN, // 3
            pos.relative(facing.getOpposite(), 1), facing, // 4
            pos.relative(facing.getOpposite(), 1).above(), Direction.DOWN // 5
        );
    };

    @Override
    public Class<PumpjackBlockEntity> getBlockEntityClass() {
        return PumpjackBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends PumpjackBlockEntity> getBlockEntityType() {
        return DestroyBlockEntities.PUMPJACK.get();
    };
    
}
