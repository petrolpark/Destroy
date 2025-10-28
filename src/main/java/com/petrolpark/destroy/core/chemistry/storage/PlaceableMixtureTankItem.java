package com.petrolpark.destroy.core.chemistry.storage;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class PlaceableMixtureTankItem<T extends PlaceableMixtureTankBlock<?>> extends BlockItem implements IMixtureStorageItem {

    protected final T tankBlock;
    
    public PlaceableMixtureTankItem(T block, Properties properties) {
        super(block, properties);
        this.tankBlock = block;
    };

    @Override
    public int getCapacity(ItemStack stack) {
        return tankBlock.getMixtureCapacity();
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = IMixtureStorageItem.defaultUseOn(this, context);
        if (result == InteractionResult.PASS) return super.useOn(context);
        return result;
    };

    @Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return getTank(level, pos, state, null, player, InteractionHand.MAIN_HAND, player.getItemInHand(InteractionHand.MAIN_HAND), false) == null;
	};

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if(context.getPlayer() != null && context.getPlayer().getAbilities().instabuild) {
            context.getPlayer().getAbilities().instabuild = false;
            InteractionResult res = super.place(context);
            context.getPlayer().getAbilities().instabuild = true;
            return res;
        }
        return IPickUpPutDownBlock.removeItemFromInventory(context, super.place(context));
    };

    @Override
    public Component getNameRegardlessOfFluid(ItemStack stack) {
        return super.getName(stack);
    };

    @Override
    public Component getName(ItemStack pStack) {
        return getNameWithFluid(pStack);
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        addContentsDescription(stack, tooltip);
    };

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemMixtureTank(stack, fs  -> {});
    };
    
};
