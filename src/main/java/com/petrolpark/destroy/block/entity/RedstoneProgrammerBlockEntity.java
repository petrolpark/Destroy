package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.RedstoneProgrammerBlock;
import com.petrolpark.destroy.block.entity.behaviour.RedstoneProgrammerBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProgrammerBlockEntity extends SmartBlockEntity {

    public RedstoneProgrammerBehaviour programmer;

    public RedstoneProgrammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        programmer = new RedstoneProgrammerBehaviour(this, () -> getBlockState().getValue(RedstoneProgrammerBlock.POWERED));
        behaviours.add(programmer);
    };
    
};
