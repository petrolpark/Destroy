package com.petrolpark.destroy.block;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackCamBlock extends AbstractShaftBlock  {

    public PumpjackCamBlock(Properties properties) {
        super(properties);
    };

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockEntityType'");
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        // TODO Auto-generated method stub
        return super.hasShaftTowards(world, pos, state, face);
    };
    
};
