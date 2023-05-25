package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.mixin.accessor.AbstractVillagerAccessor;
import com.petrolpark.destroy.mixin.accessor.AgeableMobAccessor;
import com.petrolpark.destroy.mixin.accessor.VillagerAccessor;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Villager.class)
public class VillagerMixin {
    
    /**
     * Overwritten but mostly copied from {@link net.minecraft.world.entity.npc.Villager#wantsToPickUp Villager}.
     * This allows baby Villagers to pick up toys.
     */
    @Overwrite
    public boolean wantsToPickUp(ItemStack pStack) {
        // All this is copied from the Minecraft source code
        Item item = pStack.getItem();
        return (Villager.WANTED_ITEMS.contains(item) 
            || ((VillagerAccessor)this).invokeGetVillagerData().getProfession().requestedItems().contains(item)) && ((AbstractVillagerAccessor)this).invokeGetInventory().canAddItem(pStack)
        //
            || ((AgeableMobAccessor)this).invokeIsBaby() && DestroyItems.BUCKET_AND_SPADE.isIn(pStack); //TODO replace with all toys (not just bucket and spade)
    };
};
