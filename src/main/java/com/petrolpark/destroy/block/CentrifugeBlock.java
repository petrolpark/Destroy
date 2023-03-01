package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CentrifugeBlock extends KineticBlock implements ITE<CentrifugeBlockEntity>, ICogWheel {

    public static final DirectionProperty DENSE_OUTPUT_FACE = BlockStateProperties.HORIZONTAL_FACING;

    public CentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(DENSE_OUTPUT_FACE, Direction.WEST));
    };

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() == state.getBlock() || isMoving) return; // So we don't get in an infinite loop of noticing we've been placed, so setting the Block State, so noticing we've been placed, etc.
        withTileEntityDo(worldIn, pos, be -> be.attemptRotation(false));
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DestroyShapes.CENTRIFUGE;
    };

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        pLevel.removeBlockEntity(pPos);
    };

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        withTileEntityDo(level, pos, te -> {
            te.attemptRotation(false);
        });
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(DENSE_OUTPUT_FACE);
        super.createBlockStateDefinition(builder);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		if (!context.getLevel().isClientSide()) {
            CentrifugeBlockEntity be = getTileEntity(context.getLevel(), context.getClickedPos());
            if (be == null) return InteractionResult.PASS;
            if (be.attemptRotation(true)) {
                playRotateSound(context.getLevel(), context.getClickedPos());
                updateAfterWrenched(state, context);
                return InteractionResult.SUCCESS;
            };
        };
		return InteractionResult.PASS;
	};

    @Override
    public Class<CentrifugeBlockEntity> getTileEntityClass() {
        return CentrifugeBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends CentrifugeBlockEntity> getTileEntityType() {
        return DestroyBlockEntities.CENTRIFUGE.get();
    };

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    };
    
}
