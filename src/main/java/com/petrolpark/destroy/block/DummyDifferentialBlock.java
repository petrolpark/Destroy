package com.petrolpark.destroy.block;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DummyDifferentialBlock extends RotatedPillarKineticBlock {

    public DummyDifferentialBlock(Properties properties) {
        super(properties);
    };

    @Deprecated
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, DestroyBlocks.DUMMY_DIFFERENTIAL.get(), 2);
    };

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, DestroyBlocks.DIFFERENTIAL.getDefaultState().setValue(AXIS, state.getValue(AXIS)));
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    };
    
};
