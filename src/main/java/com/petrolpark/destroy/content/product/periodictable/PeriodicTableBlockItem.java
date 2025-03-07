package com.petrolpark.destroy.content.product.periodictable;

import java.util.function.Predicate;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PeriodicTableBlockItem extends BlockItem {

    private final int placementHelperId;

    public PeriodicTableBlockItem(PeriodicTableBlock block, Properties properties) {
        super(block, properties);
        placementHelperId = PlacementHelpers.register(new PeriodicTableBlockPlacementHelper());
    };

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockHitResult ray = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), true);
        return PlacementHelpers.get(placementHelperId).getOffset(context.getPlayer(), context.getLevel(), context.getLevel().getBlockState(context.getClickedPos()), context.getClickedPos(), ray).placeInWorld(context.getLevel(), this, context.getPlayer(), context.getHand(), ray);
    };

    private class PeriodicTableBlockPlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem().equals(PeriodicTableBlockItem.this);
        };

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return PeriodicTableBlock::isPeriodicTableBlock;
        };

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            if (!PeriodicTableBlock.isPeriodicTableBlock(state)) return PlacementOffset.fail();
            Direction facing;
            if (state.getBlock() instanceof HorizontalDirectionalBlock) {
                facing = state.getValue(HorizontalDirectionalBlock.FACING);
            } else if (ray.getDirection().getAxis() != Axis.Y) {
                facing = ray.getDirection();
            } else {
                return PlacementOffset.fail();
            };
            BlockPos offset = PeriodicTableBlock.relative(state.getBlock(), getBlock(), facing);
            if (offset.equals(BlockPos.ZERO) || !world.getBlockState(pos.offset(offset)).canBeReplaced()) return PlacementOffset.fail();
            return PlacementOffset.success(pos.offset(offset), s -> s.setValue(PeriodicTableBlock.FACING, facing));
        };

    };
    
};
