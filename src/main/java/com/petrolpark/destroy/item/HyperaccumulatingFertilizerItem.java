package com.petrolpark.destroy.item;

import com.petrolpark.destroy.util.CropMutation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HyperaccumulatingFertilizerItem extends BoneMealItem {

    public HyperaccumulatingFertilizerItem(Properties properties) {
        super(properties);
    };

    @Override
    @SuppressWarnings("null")
    public InteractionResult useOn(UseOnContext context) {

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		Block block = state.getBlock();
        BlockPos cropPos = context.getClickedPos();
        BlockPos potentialOrePos = cropPos.below(2);
        BlockState potentialOreState = context.getLevel().getBlockState(potentialOrePos);

        if (block instanceof BonemealableBlock && state.is(BlockTags.CROPS)) {
            CropMutation mutation = CropMutation.getMutation(state, potentialOreState);
            if (mutation.isSuccessful()) {
                if (context.getLevel().isClientSide()) {
                    BoneMealItem.addGrowthParticles(context.getLevel(), context.getClickedPos(), 100);
                    return InteractionResult.SUCCESS;
                };
                context.getLevel().setBlockAndUpdate(cropPos, mutation.getResultantCropSupplier().get());
                if (mutation.isOreSpecific()) {
                    context.getLevel().setBlockAndUpdate(potentialOrePos, mutation.getResultantBlockUnder(potentialOreState));
                };
                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                };
                return InteractionResult.SUCCESS;
            };
        };
        return super.useOn(context);
    };
    
};
