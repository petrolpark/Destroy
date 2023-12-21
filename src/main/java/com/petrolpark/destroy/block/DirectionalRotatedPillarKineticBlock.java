package com.petrolpark.destroy.block;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class DirectionalRotatedPillarKineticBlock extends RotatedPillarKineticBlock {

    public static BooleanProperty POSITIVE_AXIS_DIRECTION = BooleanProperty.create("positive_axis_direction");

    public DirectionalRotatedPillarKineticBlock(Properties properties) {
        super(properties);
    };

    public static Direction getDirection(BlockState state) {
        return Direction.get(state.getValue(POSITIVE_AXIS_DIRECTION) ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, state.getValue(AXIS));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POSITIVE_AXIS_DIRECTION);
    };

    
};
