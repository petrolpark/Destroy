package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ExtrusionDieBlockEntity extends SmartBlockEntity {

    DestroyAdvancementBehaviour advancementBehaviour;

    public ExtrusionDieBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        advancementBehaviour = new DestroyAdvancementBehaviour(this);
        behaviours.add(advancementBehaviour);
    };
    
};
