package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.world.DestroyDamageSources;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class InebriationMobEffect extends UncurableMobEffect {
    public InebriationMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    };

    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        int pDuration = livingEntity.getEffect(DestroyMobEffects.INEBRIATION.get()).getDuration();
        if (!livingEntity.level.isClientSide()) {
            if (amplifier >= 3) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, (amplifier - 2), true, false, false));
            };
            if (amplifier >= 6) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, (amplifier - 6), true, false, false));
            };
            if (amplifier >= 10) {
                if (pDuration % Math.round(250 / amplifier) == 0) {
                    livingEntity.hurt(DestroyDamageSources.ALCOHOL, 1f);
                };
            };
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

}
