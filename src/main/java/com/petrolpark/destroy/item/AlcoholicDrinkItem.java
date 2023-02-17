package com.petrolpark.destroy.item;

import com.petrolpark.destroy.util.AlcoholHandler;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AlcoholicDrinkItem extends Item {

    private static final int DRINK_DURATION = 40;
    private int strength;

    /**
     * @param pProperties
     * @param strength How many levels of the Inebriation effect this item adds
     */
    public AlcoholicDrinkItem(Properties properties, int strength) {
        super(properties);
        this.strength = strength;
    };

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        super.finishUsingItem(stack, level, entityLiving);

        if (entityLiving instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        };
   
         if (!level.isClientSide) {
            AlcoholHandler.increaseInebriation(entityLiving, getStrength());
        };
   
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
               ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
               if (!player.getInventory().add(itemstack)) {
                  player.drop(itemstack, false);
               }
            }
   
            return stack;
        }
    };

    /**
     * Returns the number of levels of the Inebriation effect the item should add
    */
    public int getStrength() {
        return this.strength;
    };

    public int getUseDuration(ItemStack pStack) {
        return DRINK_DURATION;
    };
    
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    public SoundEvent getEatingSound() {
        return getDrinkingSound();
    };

    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    };
}
