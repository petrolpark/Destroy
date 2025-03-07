package com.petrolpark.destroy.core.chemistry.vat;

import javax.annotation.Nullable;

import com.petrolpark.compat.create.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem;
import com.petrolpark.destroy.core.chemistry.storage.ISpecialMixtureContainerBlock;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.DistExecutor;

public class VatControllerBlock extends HorizontalDirectionalBlock implements IBE<VatControllerBlockEntity>, ISpecialMixtureContainerBlock, IWrenchable {

    public VatControllerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.FAIL;
    }

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
            if (be.getVatOptional().isPresent()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> displayScreen(be, player));
            } else if (!level.isClientSide()) {
                boolean success = be.tryMakeVat();
                SoundEvent sound = success ? AllSoundEvents.CONFIRM.getMainEvent() : AllSoundEvents.DENY.getMainEvent();
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, SoundSource.BLOCKS, 1f, 1f);
                if (success) AbstractRememberPlacerBehaviour.setPlacedBy(level, pos, player);
            }
            return InteractionResult.SUCCESS;
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
        AbstractRememberPlacerBehaviour.setPlacedBy(level, pos, placer);
        super.setPlacedBy(level, pos, state, placer, stack);
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, isMoving);
    };

    @OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(VatControllerBlockEntity be, Player player) {
		if (!(player instanceof LocalPlayer)) return;
		if (be.getBlockState() == null) return;
		ScreenOpener.open(new VatScreen(be));
	};

    @Override
    public Class<VatControllerBlockEntity> getBlockEntityClass() {
        return VatControllerBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends VatControllerBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.VAT_CONTROLLER.get();
    }

    @Override
    public IFluidHandler getTankForMixtureStorageItems(IMixtureStorageItem item, Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, boolean filling) {
        return item.selectVatTank(level, pos, state, face, player, hand, stack, filling, getBlockEntity(level, pos));
    };
    
};
