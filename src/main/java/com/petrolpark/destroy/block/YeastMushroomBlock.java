package com.petrolpark.destroy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;

public class YeastMushroomBlock extends MushroomBlock {

    public YeastMushroomBlock(Properties properties) {
        super(properties, null);
    };

    @Override
    public boolean growMushroom(ServerLevel level, BlockPos pos, BlockState state, RandomSource pRandom) {
        return false;
    };

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    };
    
};
