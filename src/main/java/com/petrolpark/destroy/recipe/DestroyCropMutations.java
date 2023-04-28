package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.util.CropMutation;
import com.simibubi.create.AllBlocks;

import net.minecraft.world.level.block.Blocks;

/**
 * The Crop Mutations (using Hyperaccumulating Fertilzer) added by Destroy.
 * Once initiated, Mutations are automatically made known to the game, and a JEI Recipe entry is automatically created.
 */
public class DestroyCropMutations {

    public static final CropMutation

    GOLDEN_CARROTS = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.GOLD_ORE),
    GOLDEN_CARROTS_DEEPSLATE = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.NETHER_GOLD_ORE),
    GOLDEN_CARROTS_NETHER = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.GOLDEN_CARROTS.getDefaultState(), () -> Blocks.DEEPSLATE_GOLD_ORE),
    BIFURICATED_CARROTS = new CropMutation(() -> Blocks.CARROTS, () -> DestroyBlocks.BIFURICATED_CARROTS.getDefaultState()),
    POTATE_OS = new CropMutation(() -> Blocks.POTATOES, () -> DestroyBlocks.POTATE_OS.getDefaultState()),
    HEFTY_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.HEFTY_BEETROOT.getDefaultState()),
    COAL_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.COAL_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.COAL_ORE),
    COAL_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.COAL_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_COAL_ORE),
    COPPER_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.COPPER_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.COPPER_ORE),
    COPPER_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.COPPER_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_COPPER_ORE),
    DIAMOND_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.DIAMOND_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DIAMOND_ORE),
    DIAMOND_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.DIAMOND_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_DIAMOND_ORE),
    EMERALD_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.EMERALD_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.EMERALD_ORE),
    EMERALD_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.EMERALD_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_EMERALD_ORE),
    FLUORITE_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.FLUORITE_INFUSED_BEETROOT.getDefaultState(), DestroyBlocks.FLUORITE_ORE),
    FLUORITE_INFUSED_BEETROOT_END = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.FLUORITE_INFUSED_BEETROOT.getDefaultState(), DestroyBlocks.END_FLUORITE_ORE),
    FLUORITE_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.FLUORITE_INFUSED_BEETROOT.getDefaultState(), DestroyBlocks.DEEPSLATE_FLUORITE_ORE),
    GOLD_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.GOLD_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.GOLD_ORE),
    GOLD_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.GOLD_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_GOLD_ORE),
    GOLD_INFUSED_BEETROOT_NETHER = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.GOLD_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.NETHER_GOLD_ORE),
    IRON_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.IRON_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.IRON_ORE),
    IRON_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.IRON_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_IRON_ORE),
    LAPIS_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.LAPIS_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.LAPIS_ORE),
    LAPIS_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.LAPIS_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_LAPIS_ORE),
    NICKEL_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.NICKEL_INFUSED_BEETROOT.getDefaultState(), DestroyBlocks.NICKEL_ORE),
    NICKEL_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.NICKEL_INFUSED_BEETROOT.getDefaultState(), DestroyBlocks.DEEPSLATE_NICKEL_ORE),
    REDSTONE_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.REDSTONE_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.REDSTONE_ORE),
    REDSTONE_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.REDSTONE_INFUSED_BEETROOT.getDefaultState(), () -> Blocks.DEEPSLATE_REDSTONE_ORE),
    ZINC_INFUSED_BEETROOT = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.ZINC_INFUSED_BEETROOT.getDefaultState(), AllBlocks.ZINC_ORE),
    ZINC_INFUSED_BEETROOT_DEEPSLATE = new CropMutation(() -> Blocks.BEETROOTS, () -> DestroyBlocks.ZINC_INFUSED_BEETROOT.getDefaultState(), AllBlocks.DEEPSLATE_ZINC_ORE)
    ;

    public static void register() {};

}
