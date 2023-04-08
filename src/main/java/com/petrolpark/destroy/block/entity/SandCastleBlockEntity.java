package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.behaviour.SentimentalBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SandCastleBlockEntity extends SmartTileEntity {

    public SentimentalBehaviour sentimentalBehaviour;

    public SandCastleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        sentimentalBehaviour = new SentimentalBehaviour(this);
        behaviours.add(sentimentalBehaviour);
    };
    
};
