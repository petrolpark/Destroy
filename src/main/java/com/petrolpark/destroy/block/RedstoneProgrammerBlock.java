package com.petrolpark.destroy.block;

import java.util.List;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.RedstoneProgrammerBlockEntity;
import com.petrolpark.destroy.block.entity.behaviour.RedstoneProgrammerBehaviour;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.petrolpark.destroy.item.RedstoneProgrammerBlockItem;
import com.petrolpark.destroy.util.RedstoneProgram;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyShapes.REDSTONE_PROGRAMMER;
    };

    @Override
    @SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide()) return;
		if (!level.getBlockTicks().willTickThisTick(pos, this)) level.scheduleTick(pos, this, 0);
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.getItem() instanceof RedstoneProgrammerBlockItem) {
            RedstoneProgrammerBlockItem.getProgram(stack, level, placer).ifPresent(program -> {
                withBlockEntityDo(level, pos, be -> {
                    be.programmer.program.unload();
                    be.programmer.program = RedstoneProgram.read(() -> be.programmer.new BehaviourRedstoneProgram(), program.write());
                    program.unload();
                });
            });
        };
    };

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        RedstoneProgrammerBehaviour behaviour = BlockEntityBehaviour.get(be, RedstoneProgrammerBehaviour.TYPE);
        if (behaviour != null) return List.of(RedstoneProgrammerBlockItem.withProgram(behaviour.program));
        return List.of();
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        RedstoneProgrammerBehaviour behaviour = BlockEntityBehaviour.get(level, pos, RedstoneProgrammerBehaviour.TYPE);
        if (behaviour != null) return RedstoneProgrammerBlockItem.withProgram(behaviour.program);
        return super.getCloneItemStack(state, target, level, pos, player);
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
