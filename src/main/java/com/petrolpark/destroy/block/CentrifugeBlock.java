package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.CentrifugeBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class CentrifugeBlock extends KineticBlock implements ITE<CentrifugeBlockEntity> {

    public CentrifugeBlock(Properties properties) {
        super(properties);
    }

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
    }
    
}
