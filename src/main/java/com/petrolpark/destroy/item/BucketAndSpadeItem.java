package com.petrolpark.destroy.item;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.SandCastleBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BucketAndSpadeItem extends Item {

    private static final ImmutableList<Block> validBlocks = ImmutableList.of(Blocks.SAND, Blocks.RED_SAND, Blocks.SOUL_SAND);

    public BucketAndSpadeItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new BucketAndSpadeDispenseBehaviour());
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel() == null) return InteractionResult.PASS;
        BlockState blockUnderneath = context.getLevel().getBlockState(context.getClickedPos());
        BlockPos posAbove = context.getClickedPos().above();
        if (buildSandcastle(null, posAbove, blockUnderneath, context.getItemInHand())) {
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
            return InteractionResult.SUCCESS;
        };
        return super.useOn(context);
    };

    /**
     * Construct a sandcastle but do not break the Bucket and Spade.
     * @param level
     * @param posAbove
     * @param stateUnderneath
     * @param stack The Bucket and Spade
     * @return Whether a sandcastle was successfully built
     */
    public static boolean buildSandcastle(Level level, BlockPos posAbove, BlockState stateUnderneath, ItemStack stack) {
        if (level == null) return false;
        if (canSandCastleBeBuiltOn(stateUnderneath, level.getBlockState(posAbove))) {
            if (level.setBlockAndUpdate(posAbove, getSandCastleForMaterial(stateUnderneath))) {
                level.playSound(null, posAbove, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.5f, 1f);
                return true;
            };
        };
        return false;
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

    public static class BucketAndSpadeDispenseBehaviour extends OptionalDispenseItemBehavior {

        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            BlockPos posAbove = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
            if (buildSandcastle(source.getLevel(), posAbove, source.getLevel().getBlockState(posAbove.relative(Direction.DOWN)), stack)) {
                if (stack.hurt(1, source.getLevel().getRandom(), null)) stack.shrink(1);
                setSuccess(true);
            } else {
                setSuccess(false);
            };
            return stack;
        };
    };
    
};
