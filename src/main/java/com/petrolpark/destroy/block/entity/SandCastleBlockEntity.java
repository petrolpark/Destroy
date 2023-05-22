package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.behaviour.SentimentalBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SandCastleBlockEntity extends SmartBlockEntity {

    public SentimentalBehaviour sentimentalBehaviour;

    public SandCastleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        sentimentalBehaviour = new SentimentalBehaviour(this);
        behaviours.add(sentimentalBehaviour);
    };
    
};
