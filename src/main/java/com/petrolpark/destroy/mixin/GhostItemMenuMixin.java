package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

@MoveToPetrolparkLibrary
@Mixin(GhostItemMenu.class)
public abstract class GhostItemMenuMixin extends MenuBase<Object> {

    protected GhostItemMenuMixin(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
        throw new AssertionError();
    };
    
    @Inject(
        method = "Lcom/simibubi/create/foundation/gui/menu/GhostItemMenu;clicked(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inClicked(int slotId, int dragType, ClickType clickTypeIn, Player player, CallbackInfo ci) {
        if (slotId >= 0 && slots.get(slotId).container == player.getInventory()) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            ci.cancel();
        };
    };

    @Inject(
        method = "Lcom/simibubi/create/foundation/gui/menu/GhostItemMenu;quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true
    )
    public void inQuickMoveStack(Player playerIn, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index >= 0 && slots.get(index).container == player.getInventory() && index >= 36) {
            cir.setReturnValue(ItemStack.EMPTY);
        };
    };
};
