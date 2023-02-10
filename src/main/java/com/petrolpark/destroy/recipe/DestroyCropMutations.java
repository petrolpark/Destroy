package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.util.CropMutation;

import net.minecraft.world.level.block.Blocks;

public class DestroyCropMutations {

    public static final CropMutation

    GOLDEN_CARROTS = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.GOLD_ORE),
    GOLDEN_CARROTS_DEEPSLATE = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.NETHER_GOLD_ORE),
    GOLDEN_CARROTS_NETHER = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.DEEPSLATE_GOLD_ORE),
    BIFURICATED_CARROTS = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.BIFURICATED_CARROTS.getDefaultState()),
    POTATE_OS = new CropMutation(() -> Blocks.POTATOES, () -> DestroyBlocks.POTATE_OS.getDefaultState())
    
    
    ;

    public static void register() {};

}
