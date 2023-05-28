package com.petrolpark.destructivedelight.block;

import com.petrolpark.destroy.block.DestroyBlocks;

import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

public class YeastColonyBlock extends MushroomColonyBlock {

    public YeastColonyBlock(Properties properties) {
        super(properties, () -> DestroyBlocks.YEAST_MUSHROOM.get().asItem());
    };
    
};
