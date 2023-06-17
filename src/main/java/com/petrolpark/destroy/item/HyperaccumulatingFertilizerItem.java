package com.petrolpark.destroy.item;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.util.CropMutation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HyperaccumulatingFertilizerItem extends BoneMealItem {

    public HyperaccumulatingFertilizerItem(Properties properties) {
        super(properties);
        registerDispenserBehaviour(this);
    };

    /**
     * Attempts to grow (with Hyperaccumulating Fertilizer) the crop at the given position.
     * @param level The Level the crop is in
     * @param cropPos The position of the crop
     * @return Whether the crop could be successfully grown
     */
    private static boolean grow(Level level, BlockPos cropPos) {

        BlockState crostate = level.getBlockState(cropPos);
        Block cropBlock = crostate.getBlock();
        BlockPos potentialOrePos = cropPos.below(2);
        BlockState potentialOreState = level.getBlockState(potentialOrePos);

        if (cropBlock instanceof BonemealableBlock && crostate.is(BlockTags.CROPS)) {
            CropMutation mutation = CropMutation.getMutation(crostate, potentialOreState);
            if (mutation.isSuccessful()) {
                if (level.isClientSide()) {
                    addGrowthParticles(level, cropPos, 100);
                    return true;
                };
                level.setBlockAndUpdate(cropPos, mutation.getResultantCropSupplier().get());
                if (mutation.isOreSpecific()) {
                    level.setBlockAndUpdate(potentialOrePos, mutation.getResultantBlockUnder(potentialOreState));
                };
                return true;
            };
        };
        return false;
    };

    @Override
    @SuppressWarnings("null")
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        boolean couldGrow = grow(level, context.getClickedPos()); // Try grow the Crop
        if (couldGrow) { // If necessary, use up one Hyperaccumulating Fertilizer
            if (!level.isClientSide() && context.getPlayer() != null && !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            };
            DestroyAdvancements.HYPERACCUMULATE.award(level, context.getPlayer());
            return InteractionResult.SUCCESS;
        };
        return super.useOn(context);
    };

    @SuppressWarnings("deprecation") // BoneMealItem.growCrop() is deprecated but it's used in the Bone Meal Dispenser Behaviour so I don't care
    public static void registerDispenserBehaviour(HyperaccumulatingFertilizerItem item) {
        DispenserBlock.registerBehavior(item, new OptionalDispenseItemBehavior() {
            protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
                Level level = blockSource.getLevel();
                BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
                if (grow(level, blockPos)) { // Try to grow it as Hyperaccumulating Fertilizer
                    stack.shrink(1);
                    this.setSuccess(true);
                } else if (BoneMealItem.growCrop(stack, level, blockPos) || BoneMealItem.growWaterPlant(stack, level, blockPos, (Direction)null)) { // Try to grow as normal Bonemeal
                    // Shrinking the Stack is covered in BoneMealItem.growCrop() or .growWaterPlant()
                    this.setSuccess(true);
                    if (!level.isClientSide()) {
                        level.levelEvent(1505, blockPos, 0); // Don't really know what this does but it's in the Bone Meal Dispenser Behaviour so best to include it
                    };
                } else {
                    this.setSuccess(false);
                };
                return stack;
            };
        });
    };
    
};
