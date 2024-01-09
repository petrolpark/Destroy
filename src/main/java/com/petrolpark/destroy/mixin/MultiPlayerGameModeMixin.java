package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

//TODO remove in 1.21
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    
    /**
     * Temporary fix of https://github.com/neoforged/NeoForge/issues/143 until we upgrade to NeoForge or 1.21.
     * @param pPos
     * @return
     */
    @Overwrite
    @SuppressWarnings("null")
    private boolean sameDestroyTarget(BlockPos pPos) {
        Minecraft minecraft = getMinecraft();
        return pPos.equals(getDestroyBlockPos()) && !getDestroyingItem().shouldCauseBlockBreakReset(minecraft.player.getMainHandItem());
    };

    @Accessor("minecraft")
    public abstract Minecraft getMinecraft();

    @Accessor("destroyingItem")
    public abstract ItemStack getDestroyingItem();

    @Accessor("destroyBlockPos")
    public abstract BlockPos getDestroyBlockPos();
};
