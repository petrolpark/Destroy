package com.petrolpark.destroy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
@MoveToPetrolparkLibrary
public class ServerPlayerGameModeMixin {
    private static ThreadLocal<Boolean> currentItemStackIgnoresCreative = new ThreadLocal<>();

    @Inject(
        method = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;",
            ordinal = 0
        )
    )
    private void checkCurrentItemStack(ServerPlayer pPlayer, Level pLevel, ItemStack pStack, InteractionHand pHand, BlockHitResult pHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        currentItemStackIgnoresCreative.set(pStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IPickUpPutDownBlock);
    }

    @WrapOperation(
        method = "Lnet/minecraft/server/level/ServerPlayerGameMode;useItemOn(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V"
        )
    )
    private void dontRevertItemStack(ItemStack instance, int pCount, Operation<Void> original) {
        if(currentItemStackIgnoresCreative.get())
            currentItemStackIgnoresCreative.set(false);
        else
            original.call(instance, pCount);
    }
}
