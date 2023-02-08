package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.util.CropMutation;

import net.minecraft.world.level.block.Blocks;

public class DestroyCropMutations {

    public static final CropMutation

    TEST_MUTATION = new CropMutation(() -> Blocks.WHEAT, Blocks.ACACIA_PLANKS.defaultBlockState())
    
    ;

    public static void register() {};

}
