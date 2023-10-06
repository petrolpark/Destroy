package com.petrolpark.destroy.item;

import java.util.function.Predicate;

import com.petrolpark.destroy.block.CoaxialGearBlock;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CoaxialGearBlockItem extends CogwheelBlockItem {

    public CoaxialGearBlockItem(CoaxialGearBlock block, Properties properties) {
        super(block, properties);
        PlacementHelpers.register(new GearOnShaftPlacementHelper());
        PlacementHelpers.register(new ShaftOnGearPlacementHelper());
    };

    @Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (ShaftBlock.isShaft(state)) {
            if (CoaxialGearBlock.tryMakeLongShaft(state, level, pos, Direction.getFacingAxis(player, state.getValue(RotatedPillarKineticBlock.AXIS)))) {
                if (!level.isClientSide() && !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                };
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                player.displayClientMessage(DestroyLang.translate("tooltip.coaxial_gear.shaft_too_short").style(ChatFormatting.RED).component(), true);
                return InteractionResult.SUCCESS;
            }
        };
		return super.useOn(context);
	};

    private static final class GearOnShaftPlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return (stack) -> stack.getItem() instanceof BlockItem blockItem && CoaxialGearBlock.isCoaxialGear(blockItem.getBlock());
        };

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return ShaftBlock::isShaft;
        };

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            return PlacementOffset.success(pos, s -> s.setValue(RotatedPillarKineticBlock.AXIS, state.getValue(RotatedPillarKineticBlock.AXIS)));
        };

    };

    private static final class ShaftOnGearPlacementHelper implements IPlacementHelper {

        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return AllBlocks.SHAFT::isIn;
        };

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return CoaxialGearBlock::isCoaxialGear;
        };

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            return PlacementOffset.success(pos, s -> s.setValue(RotatedPillarKineticBlock.AXIS, state.getValue(RotatedPillarKineticBlock.AXIS)));
        };

    };
    
};
