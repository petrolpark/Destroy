package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.DifferentialBlockEntity;
import com.petrolpark.destroy.util.KineticsHelper;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DifferentialBlock extends CogWheelBlock {

    public DifferentialBlock(Properties properties) {
        super(true, properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    };

    @Override
    @SuppressWarnings("null")
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        withBlockEntityDo(level, pos, be -> {
            Direction directionBetween = KineticsHelper.directionBetween(pos, neighbor);
            Direction differentialDirection = DirectionalRotatedPillarKineticBlock.getDirection(state);
            if (be instanceof DifferentialBlockEntity differential && directionBetween.getAxis() == differentialDirection.getAxis()) {
                differential.getLevel().setBlockAndUpdate(pos, DestroyBlocks.DUMMY_DIFFERENTIAL.getDefaultState().setValue(AXIS, state.getValue(AXIS)).setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, state.getValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION))); // It thinks getLevel() might be null
            };
        });
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == DirectionalRotatedPillarKineticBlock.getDirection(state);
    };

    @Override
    public boolean isLargeCog() {
        return true;
    };

    @Override
	public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.DIFFERENTIAL.get();
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    };
    
};
