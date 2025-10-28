package com.petrolpark.destroy.core.chemistry.storage;

import java.util.Collections;
import java.util.List;

import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class PlaceableMixtureTankBlock<T extends BlockEntity> extends Block implements IPickUpPutDownBlock, IBE<T> {

    public PlaceableMixtureTankBlock(Properties properties) {
        super(properties);
    };

    public abstract int getMixtureCapacity();

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.getItem() instanceof IMixtureStorageItem mixtureItem && mixtureItem.getContents(pStack).isPresent()) {
            withBlockEntityDo(pLevel, pPos, be -> {
                be.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
                    fluidHandler.fill(mixtureItem.getContents(pStack).get(), FluidAction.EXECUTE);
                });
            });
        };
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    };

    @Override
    public List<ItemStack> getDrops(BlockState pState, Builder pParams) {
        BlockEntity be = pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return Collections.singletonList(getFilledItemStack(be));
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getFilledItemStack(level.getBlockEntity(pos));
    };

    public ItemStack getFilledItemStack(BlockEntity be) {
        if (be != null && asItem() instanceof IMixtureStorageItem mixtureItem) {
            ItemStack stack = new ItemStack(asItem());
            return be.getCapability(ForgeCapabilities.FLUID_HANDLER).map(fluidHandler -> {
                mixtureItem.setContents(stack, fluidHandler.drain(mixtureItem.getCapacity(stack), FluidAction.SIMULATE));
                return stack;
            }).orElse(ItemStack.EMPTY);
        };
        return ItemStack.EMPTY;
    };
    
};
