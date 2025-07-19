package com.petrolpark.destroy.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO remove in 1.21
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    
    /**
     * Temporary fix of https://github.com/neoforged/NeoForge/issues/143 until we upgrade to NeoForge or 1.21.
     * @param pPos
     * @return
     */

    @Inject(method = "sameDestroyTarget", at = @At("RETURN"), cancellable = true)
    private void destroy$sameDestroyTarget(BlockPos pPos, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemstack) {
        cir.setReturnValue(pPos.equals(getDestroyBlockPos()) && !getDestroyingItem().shouldCauseBlockBreakReset(itemstack) || cir.getReturnValue());
    };

    @Accessor("destroyingItem")
    public abstract ItemStack getDestroyingItem();

    @Accessor("destroyBlockPos")
    public abstract BlockPos getDestroyBlockPos();
};
