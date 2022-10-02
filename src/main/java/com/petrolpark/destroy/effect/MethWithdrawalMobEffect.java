package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.Destroy;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class MethWithdrawalMobEffect extends MobEffect {
    public MethWithdrawalMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.hasEffect(DestroyMobEffects.METH_HIGH.get())) {
            if (livingEntity.getEffect(DestroyMobEffects.METH_HIGH.get()).getDuration() >= 2) {
                livingEntity.removeEffect(DestroyMobEffects.METH_WITHDRAWAL.get());
                // This removes the Meth Withdrawal Effect if the Entity has the Meth High Effect.
                // However, as the Meth Withdrawal Effect is always added when the Meth High Effect has one remaining tick, it should only be removed if the Meth High Effect lasts longer than this.
            }
        };

        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
