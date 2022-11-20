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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CentrifugeBlock extends KineticBlock implements ITE<CentrifugeBlockEntity>, ICogWheel {

    public static final DirectionProperty DENSE_OUTPUT_FACE = DirectionProperty.create("direction");

    public CentrifugeBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(DENSE_OUTPUT_FACE, Direction.WEST));
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DestroyShapes.CENTRIFUGE;
    };

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.hasBlockEntity() && (pState.getBlock() != pNewState.getBlock() || !pNewState.hasBlockEntity())) { //if the Block Entity is getting removed
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (!(be instanceof CentrifugeBlockEntity)) {
                return;
            }
            pLevel.removeBlockEntity(pPos);
        };
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        getTileEntity(level, pos).updateDenseOutputFace(); //this also updates the Block State
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DENSE_OUTPUT_FACE);
        super.createBlockStateDefinition(pBuilder);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    }

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
