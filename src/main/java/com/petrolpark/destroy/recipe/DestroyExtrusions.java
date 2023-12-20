package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.util.BlockExtrusion;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DestroyExtrusions {
    
    public static void register() {
        // Vanilla
        BlockExtrusion.register(Blocks.QUARTZ_BLOCK, (state, direction) -> Blocks.QUARTZ_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, direction.getAxis()));
        BlockExtrusion.register(Blocks.PURPUR_BLOCK, (state, direction) -> Blocks.PURPUR_PILLAR.defaultBlockState().setValue(BlockStateProperties.AXIS, direction.getAxis()));

        // Destroy
        BlockExtrusion.register(DestroyBlocks.CORDITE_BLOCK.get(), (state, direction) -> DestroyBlocks.EXTRUDED_CORDITE_BLOCK.getDefaultState().setValue(BlockStateProperties.AXIS, direction.getAxis()));
    };
};
