package com.petrolpark.destroy.block;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

/**
 * Almost entirely copied from the {@link com.simibubi.create.content.kinetics.waterwheel.PumpjackStructuralBlock Create source code}.
 */
//TODO make wrenchable
public class PumpjackStructuralBlock extends DirectionalBlock implements IProxyHoveringInformation {

    protected PumpjackStructuralBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };


	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	};

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	};

	// @Override
	// public InteractionResult onWrenched(BlockState state, UseOnContext context) {
	// 	return InteractionResult.PASS;
	// };

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		return DestroyBlocks.PUMPJACK.asStack();
	};

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (stillValid(level, pos, state, false)) level.destroyBlock(getMaster(level, pos, state), true);
	};

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (stillValid(level, pos, state, false)) {
			BlockPos masterPos = getMaster(level, pos, state);
			level.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
			if (!level.isClientSide() && player.isCreative()) level.destroyBlock(masterPos, false);
		};
		super.playerWillDestroy(level, pos, state, player);
	}

    @Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
		BlockPos currentPos, BlockPos facingPos) {
		if (stillValid(level, currentPos, state, false)) {
			BlockPos masterPos = getMaster(level, currentPos, state);
			if (!level.getBlockTicks().hasScheduledTick(masterPos, DestroyBlocks.PUMPJACK.get())) {
				level.scheduleTick(masterPos, DestroyBlocks.PUMPJACK.get(), 1);
            };
			return state;
		};
		if (!(level instanceof Level realLevel) || realLevel.isClientSide()) return state;
		if (!realLevel.getBlockTicks()
			.hasScheduledTick(currentPos, this))
			realLevel.scheduleTick(currentPos, this, 1);
		return state;
	};

    /**
     * Get the position of the Pumpjack controller Block State to which this structural Block State is attached.
     */
    public static BlockPos getMaster(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);
        // Recursion until we find the master
		if (targetedState.is(DestroyBlocks.PUMPJACK_STRUCTURAL.get()))
			return getMaster(level, targetedPos, targetedState);
		return targetedPos;
	};

    /**
     * Whether the controller Pumpjack Block State still exists.
     */
    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state, boolean directlyAdjacent) {
		if (!state.is(this)) return false;
		Direction direction = state.getValue(FACING);
		BlockPos targetedPos = pos.relative(direction);
		BlockState targetedState = level.getBlockState(targetedPos);

        // If the State is not directly adjacent to the Pumpjack controller
		if (!directlyAdjacent && stillValid(level, targetedPos, targetedState, true)) return true;
        // If the State is right next to the Pumpjack controller
		return targetedState.getBlock() instanceof PumpjackBlock;
	};

    @Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!stillValid(level, pos, state, false)) level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	};

    @Override
    public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
        return stillValid(level, pos, state, false) ? getMaster(level, pos, state) : pos;
    };

    public static class RenderProperties implements IClientBlockExtensions, MultiPosDestructionHandler {

		@Override
		public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
			return true;
		};

        /**
         * Add mining particles to the controller Pumpjack Block State when this one is mined.
         */
		@Override
		public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
			if (target instanceof BlockHitResult bhr) {
				BlockPos targetPos = bhr.getBlockPos();
				PumpjackStructuralBlock pumpjackStructuralBlock = DestroyBlocks.PUMPJACK_STRUCTURAL.get();
				if (pumpjackStructuralBlock.stillValid(level, targetPos, state, false))
					manager.crack(PumpjackStructuralBlock.getMaster(level, targetPos, state), bhr.getDirection());
				return true;
			}
			return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);
		};

        /**
         * Destroy the controller Pumpjack Block State when this one is destroyed.
         */
		@Override
		@Nullable
		public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
			PumpjackStructuralBlock pumpjackStructuralBlock = DestroyBlocks.PUMPJACK_STRUCTURAL.get();
			if (!pumpjackStructuralBlock.stillValid(level, pos, blockState, false)) return null;
			HashSet<BlockPos> set = new HashSet<>();
			set.add(PumpjackStructuralBlock.getMaster(level, pos, blockState));
			return set;
		};
	};
    
};
