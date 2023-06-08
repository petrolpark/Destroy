package com.petrolpark.destroy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Almost entirely copied from the {@link com.simibubi.create.content.kinetics.waterwheel.PumpjackStructuralBlock Create source code}.
 */
public class PumpjackStructuralBlock extends DirectionalBlock implements IPumpjackStructuralBlock {

    protected PumpjackStructuralBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, COMPONENT);
    };

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return IPumpjackStructuralBlock.super.getShape(state, level, pos, context);
	};

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	};

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	};

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return DestroyBlocks.PUMPJACK.asStack();
	};

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		IPumpjackStructuralBlock.onRemove(state, level, pos, newState, isMoving);
	};

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		IPumpjackStructuralBlock.playerWillDestroy(level, pos, state, player);
		super.playerWillDestroy(level, pos, state, player);
	}

    @Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return IPumpjackStructuralBlock.updateShape(state, facing, facingState, level, currentPos, facingPos, this);
	};

    @Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		IPumpjackStructuralBlock.tick(state, level, pos, random);
	};

	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return true;
	};
};
