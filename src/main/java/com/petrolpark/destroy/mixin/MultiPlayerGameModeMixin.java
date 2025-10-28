package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

@MoveToPetrolparkLibrary
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    private static ThreadLocal<Boolean> currentItemStackIgnoresCreative = new ThreadLocal<>();

    @Inject(
        method = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;performUseItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            ordinal = 0
        )
    )
    private void checkCurrentItemStack(LocalPlayer pPlayer, InteractionHand pHand, BlockHitResult pResult, CallbackInfoReturnable<InteractionResult> cir) {
        currentItemStackIgnoresCreative.set(pPlayer.getItemInHand(pHand).getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPickUpPutDownBlock);
    };

    @WrapOperation(
        method = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;performUseItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V"
        )
    )
    private void dontRevertItemStack(ItemStack instance, int pCount, Operation<Void> original) {
        if (currentItemStackIgnoresCreative.get()) currentItemStackIgnoresCreative.set(false);
        else original.call(instance, pCount);
    };
}
