package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ItemDrainBlock.class)
public abstract class ItemDrainBlockMixin extends Block implements IBE<ItemDrainBlockEntity>, IWrenchable {
  
    public ItemDrainBlockMixin(Properties properties) {
        super(properties); // This should never get called
    };

    @Inject(
        method = "Lcom/simibubi/create/content/fluids/drain/ItemDrainBlock;onRemove(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
        at = @At("HEAD")
    )
	private void inOnRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
		IBE.onRemove(state, worldIn, pos, newState);
	};
};
