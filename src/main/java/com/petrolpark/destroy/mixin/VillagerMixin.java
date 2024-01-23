package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.mixin.accessor.AbstractVillagerAccessor;
import com.petrolpark.destroy.mixin.accessor.AgeableMobAccessor;
import com.petrolpark.destroy.mixin.accessor.VillagerAccessor;
import com.petrolpark.destroy.util.PollutionHelper;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

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
            || ((AgeableMobAccessor)this).invokeIsBaby() && DestroyItems.BUCKET_AND_SPADE.isIn(pStack);
    };

    /**
     * Make trades more expensive with higher levels of Smog.
     */
    @Inject(method = "updateSpecialPrices", at = @At(value = "RETURN"))
    public void inUpdateSpecialPrices(Player player, CallbackInfo ci) {
        if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.villagersIncreasePrices.get()) return;
        Villager thisVillager = (Villager)(Object)this;
        for (MerchantOffer trade : thisVillager.getOffers()) {
            int change = (int)(50d * (double)PollutionHelper.getPollution(thisVillager.level(), PollutionType.SMOG) / (double)PollutionType.SMOG.max);
            trade.addToSpecialPriceDiff(change);
        };
    };
};
