package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventory.DelayedSlotPopulation;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@MoveToPetrolparkLibrary
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin implements DelayedSlotPopulation {

    @Shadow
    public abstract Slot getSlot(int pSlotId);
    @Shadow
    public abstract boolean isValidSlotIndex(int pSlotIndex);
    @Shadow
    private ItemStack carried;
    @Shadow
    private int stateId;
    @Shadow
    public NonNullList<Slot> slots;

    @Unique
    private Int2ObjectMap<ItemStack> delayedSlotStacks = new Int2ObjectArrayMap<>();

    @Override
    public void populateDelayedSlots() {
        delayedSlotStacks.forEach((i, s) -> getSlot(i).set(s));
    };

    @Overwrite
    public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
        if (!isValidSlotIndex(pSlotId)) {
            delayedSlotStacks.put(pSlotId, pStack);
        } else {
            getSlot(pSlotId).set(pStack);
        };
        stateId = pStateId;
    };

    @Overwrite
    public void initializeContents(int pStateId, List<ItemStack> pItems, ItemStack pCarried) {
        for (int i = 0; i < pItems.size(); ++i) {
            ItemStack stack = pItems.get(i);
            if (!isValidSlotIndex(i)) {
                delayedSlotStacks.put(i, stack);
            } else {
                getSlot(i).set(stack);
            };
        };
        carried = pCarried;
        stateId = pStateId;
    };
};
