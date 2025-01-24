package com.petrolpark.destroy.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.petrolpark.destroy.entity.player.ExtendedInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    public LocalPlayer player;
    
    @Redirect(
        method = "Lnet/minecraft/client/Minecraft;pickBlock()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    public void sendRightSlotId(MultiPlayerGameMode gameMode, ItemStack stack, int slotId) {
        ExtendedInventory inv = ExtendedInventory.get(player);
        if (Inventory.isHotbarSlot(inv.getSelectedHotbarIndex())) { // If the hotbar slot is 0-9 (vanilla slots)
            gameMode.handleCreativeModeItemAdd(stack, slotId); // The hotbar slots are added to the menu after 36 other slots (4 crafting, offhand, 4 armor, 27 inventory)
        } else { // If the hotbar slot is > 9 (extended) 
            gameMode.handleCreativeModeItemAdd(stack, inv.selected + 5); // Add 5 to account for 5 crafting slots (the 41 inventory slots are already accounted for)
        };
    };
};
