package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class VatControllerBlock extends Block implements IBE<VatControllerBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public VatControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return onBlockEntityUse(level, pos, be -> {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (be.getVatOptional().isPresent()) {
                return InteractionResult.PASS;
            } else {
                boolean success = be.tryMakeVat();
                SoundEvent sound = success ? AllSoundEvents.CONFIRM.getMainEvent() : AllSoundEvents.DENY.getMainEvent();
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.BLOCKS, 1f, 1f);
                if (success) DestroyAdvancementBehaviour.setPlacedBy(level, pos, player);
                return InteractionResult.SUCCESS;
            }
        });
    };

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        withBlockEntityDo(level, pos, be -> {
            be.tryMakeVat();
        });
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        DestroyAdvancementBehaviour.setPlacedBy(level, pos, placer);
        super.setPlacedBy(level, pos, state, placer, stack);
    };

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    };

    @Override
    public Class<VatControllerBlockEntity> getBlockEntityClass() {
        return VatControllerBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends VatControllerBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.VAT_CONTROLLER.get();
    };
    
};
