package com.petrolpark.destroy.item;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AspirinSyringeItem extends SyringeItem {

    public AspirinSyringeItem(Properties properties) {
        super(properties);
    };

    @Override
    public void onInject(ItemStack itemStack, Level level, LivingEntity target) {
        target.heal(10);
        if (!target.removeEffect(DestroyMobEffects.HANGOVER.get())) return;
        if (target instanceof Player player) {
            DestroyAdvancements.CURE_HANGOVER.award(level, player);
        };
    };
    
};
