package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.ChainedCogwheelBlockEntity;
import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.util.CogwheelChainingHandler;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChainedCogwheelBlock extends AbstractSimpleShaftBlock implements ICogWheel {

    protected final boolean large;

    protected ChainedCogwheelBlock(Properties properties, boolean large) {
        super(properties);
        this.large = large;
    };

    public static ChainedCogwheelBlock large(Properties properties) {
        return new ChainedCogwheelBlock(properties, true);
    };

    public static ChainedCogwheelBlock small(Properties properties) {
        return new ChainedCogwheelBlock(properties, false);
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (getBlockEntity(level, pos) instanceof ChainedCogwheelBlockEntity cbe && cbe.copiedState != null) return cbe.copiedState.getShape(level, pos, context);
        return Shapes.block();
    };

    public static void tryPlace(Player player, Level level, BlockPos pos1, BlockPos pos2) {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!stack.is(Items.CHAIN)) return;
        BlockState state1 = level.getBlockState(pos1);
        BlockState state2 = level.getBlockState(pos2);
        if (!(state1.getBlock() instanceof IRotate rotate1) || !(state2.getBlock() instanceof IRotate rotate2)) return;
        Axis axis1 = rotate1.getRotationAxis(state1);
        Axis axis2 = rotate2.getRotationAxis(state2);
        if (!IChainableBlock.isStateChainable(state1) || !IChainableBlock.isStateChainable(state2)) return;
        if (!CogwheelChainingHandler.canConnect(pos1, axis1, pos2, axis2)) return;
        level.setBlockAndUpdate(pos1, (ICogWheel.isLargeCog(state1) ? DestroyBlocks.CHAINED_LARGE_COGWHEEL : DestroyBlocks.CHAINED_COGWHEEL).getDefaultState().setValue(AXIS, axis1));
        level.setBlockAndUpdate(pos2, (ICogWheel.isLargeCog(state1) ? DestroyBlocks.CHAINED_LARGE_COGWHEEL : DestroyBlocks.CHAINED_COGWHEEL).getDefaultState().setValue(AXIS, axis1));
        BlockEntity be1 = level.getBlockEntity(pos1);
        if (be1 instanceof ChainedCogwheelBlockEntity cbe) {
            cbe.partner = pos2.subtract(pos1);
            cbe.controller = true;
            cbe.copiedState = state1;
        };
        BlockEntity be2 = level.getBlockEntity(pos2);
        if (be2 instanceof ChainedCogwheelBlockEntity cbe) {
            cbe.partner = pos1.subtract(pos2);
            cbe.copiedState = state2;
        };
        if (!player.isCreative()) stack.shrink(1);
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        KineticBlockEntity kbe = getBlockEntity(level, pos);
        if (kbe instanceof ChainedCogwheelBlockEntity cbe && cbe.copiedState != null) return cbe.copiedState.getCloneItemStack(target, level, pos, player);
        return ItemStack.EMPTY;
    };

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        KineticBlockEntity kbe = getBlockEntity(world, pos);
        if (kbe instanceof ChainedCogwheelBlockEntity cbe && cbe.copiedState != null) {
            if (cbe.copiedState.getBlock() instanceof IRotate rotate) return rotate.hasShaftTowards(world, pos, cbe.copiedState, face);
        };
        return false;
    };

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.CHAINED_COGWHEEL.get();
    };

    @Override
    public boolean isLargeCog() {
        return large;
    };
    
};
