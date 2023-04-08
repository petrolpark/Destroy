package com.petrolpark.destroy.item;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.SandCastleBlock;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BucketAndSpadeItem extends Item {

    private static final ImmutableList<Block> validBlocks = ImmutableList.of(Blocks.SAND, Blocks.RED_SAND, Blocks.SOUL_SAND);

    public BucketAndSpadeItem(Properties properties) {
        super(properties);
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState blockUnderneath = context.getLevel().getBlockState(context.getClickedPos());
        if (context.getClickedFace() == Direction.UP && canSandCastleBeBuiltOn(blockUnderneath, context.getLevel().getBlockState(context.getClickedPos().above()))) {
            context.getLevel().setBlockAndUpdate(context.getClickedPos().above(), getSandCastleForMaterial(blockUnderneath));
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
            return InteractionResult.SUCCESS;
        };
        return super.useOn(context);
    };

    /**
     * Determines whether a Sand Castle can be naturally placed on the given Block.
     * @param state The Block upon which the Sand Castle would be built
     * @param stateAbove The Block which the Sand Castle would occupy
     */
    public static boolean canSandCastleBeBuiltOn(BlockState state, BlockState stateAbove) {
        return validBlocks.stream().anyMatch(block -> state.is(block)) && stateAbove.isAir();
    };

    /**
     * Determines the Block State of the Sand Castle to be built on the given Block.
     * @param stateUnderneath The Block upon which the Sand Castle will be built
     */
    public static BlockState getSandCastleForMaterial(BlockState stateUnderneath) {
        SandCastleBlock.Material material = SandCastleBlock.Material.SAND;
        if (stateUnderneath.is(Blocks.RED_SAND)) {
            material = SandCastleBlock.Material.RED_SAND;
        } else if (stateUnderneath.is(Blocks.SOUL_SAND)) {
            material = SandCastleBlock.Material.SOUL_SAND;
        };
        return DestroyBlocks.SAND_CASTLE.getDefaultState().setValue(SandCastleBlock.MATERIAL, material);
    };
    
};
