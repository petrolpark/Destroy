package com.petrolpark.destroy.item;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BabyBlueSyringeItem extends SyringeItem {

    private int duration;
    private int amplifier;

    public BabyBlueSyringeItem(Properties properties, int babyBlueEffectDuration, int babyBlueEffectAmplifier) {
        super(properties);
        this.duration = babyBlueEffectDuration;
        this.amplifier = babyBlueEffectAmplifier;
    };

    @Override
    public void onInject(ItemStack itemStack, Level level, LivingEntity target) {
        target.addEffect(new MobEffectInstance(DestroyMobEffects.BABY_BLUE_HIGH.get(), duration, amplifier));
    };
    
}
