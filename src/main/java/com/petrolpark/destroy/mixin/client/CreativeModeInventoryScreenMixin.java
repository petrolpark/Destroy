package com.petrolpark.destroy.mixin.client;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.petrolpark.destroy.DestroyClient;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventory;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@Mixin(CreativeModeInventoryScreen.class)
@MoveToPetrolparkLibrary
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<ItemPickerMenu> {

    @Shadow
    private Slot destroyItemSlot;
    
    public CreativeModeInventoryScreenMixin(ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        throw new AssertionError(); // Should never be called
    };

    /**
     * Don't add Extended Inventory Slots to the Survival Inventory section of the Creative Inventory in the normal way.
     */
    @WrapOperation(
        method = "selectTab",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/NonNullList;size()I"
        )
    )
    public int addLimitedSlots(NonNullList<Slot> slots, Operation<Integer> original) {
        return 46;
    };


    /**
     * Add Extended Inventory Slots to the Survival Inventory section of the Creative Inventory.
     */
    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;selectTab(Lnet/minecraft/world/item/CreativeModeTab;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;destroyItemSlot:Lnet/minecraft/world/inventory/Slot;",
            ordinal = 0
        )
    )
    public void inSelectTab(CreativeModeTab tab, CallbackInfo ci) {
        ExtendedInventory inv = ExtendedInventory.get(minecraft.player);
        Int2ObjectMap<Slot> extendedInventorySlots = new Int2ObjectArrayMap<>(); // Map Inventory indices to Slots in the Inventory Menu, as the index of the Slot in the menu is not guaranteed to match the index in the Inventory the Slot encapsulates
        for (Slot slot : minecraft.player.inventoryMenu.slots) {
            if (slot.getSlotIndex() >= inv.getExtraInventoryStartSlotIndex()) extendedInventorySlots.put(slot.getSlotIndex(), slot);
        };
        DestroyClient.EXTENDED_INVENTORY_HANDLER.addSlotsToClientMenu(inv, menu::addSlot, (c, i, x, y) -> new CreativeModeInventoryScreen.SlotWrapper(extendedInventorySlots.get(i), i, x, y));
    };

    @Inject(
        method = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;slotClicked",
        at = @At("HEAD")
    )
    protected void inSlotClicked(@Nullable Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (slot == destroyItemSlot && type == ClickType.QUICK_MOVE) {
            ExtendedInventory inv = ExtendedInventory.get(minecraft.player);
            for (Slot inventorySlot : menu.slots) {
                if (inventorySlot.getSlotIndex() >= inv.getExtraInventoryStartSlotIndex()) minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, inventorySlot.index);
            };
        };
    };
};
