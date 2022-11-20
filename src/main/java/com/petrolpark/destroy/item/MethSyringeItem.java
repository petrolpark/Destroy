package com.petrolpark.destroy.item;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MethSyringeItem extends SyringeItem {

    private int duration;
    private int amplifier;

    public MethSyringeItem(Properties properties, int methEffectDuration, int methEffectAmplifier) {
        super(properties);
        this.duration = methEffectDuration;
        this.amplifier = methEffectAmplifier;
    };

    @Override
    public void onInject(ItemStack itemStack, Level level, LivingEntity target) {
        target.addEffect(new MobEffectInstance(DestroyMobEffects.METH_HIGH.get(), duration, amplifier));
    }
    
}
