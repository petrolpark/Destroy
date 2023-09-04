package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.util.BlockExtrusion;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DestroyExtrusions {
    
    public static void register() {
        BlockExtrusion.register(Blocks.QUARTZ_BLOCK, (state, direction) -> Blocks.QUARTZ_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, direction.getAxis()));
    };
};
