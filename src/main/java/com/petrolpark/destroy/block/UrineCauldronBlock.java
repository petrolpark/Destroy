package com.petrolpark.destroy.block;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UrineCauldronBlock extends AbstractCauldronBlock {

    public UrineCauldronBlock(Properties properties, Map<Item, CauldronInteraction> interactions) {
        super(properties, interactions);
    };

    @Override
    public boolean isFull(BlockState state) {
        return true;
    };

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pPos) {
        return 2;
    };

};