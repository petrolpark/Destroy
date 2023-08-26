package com.petrolpark.destroy.item;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CisplatinSyringeItem extends SyringeItem {

    public CisplatinSyringeItem(Properties properties) {
        super(properties);
    };

    @Override
    public void onInject(ItemStack itemStack, Level level, LivingEntity target) {
        target.removeEffect(DestroyMobEffects.CANCER.get());
    };
    
};
