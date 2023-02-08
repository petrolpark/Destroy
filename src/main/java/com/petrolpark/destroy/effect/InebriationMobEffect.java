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

    @SuppressWarnings("null") // It's not null; I checked
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        int pDuration = livingEntity.getEffect(DestroyMobEffects.INEBRIATION.get()).getDuration(); // This is the bit it says is null
        if (!livingEntity.level.isClientSide()) {
            if (amplifier >= 3) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, pDuration, (amplifier - 2), true, false, false));
            };
            if (amplifier >= 6) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, pDuration, (amplifier - 6), true, false, false));
            };
            if (amplifier >= 9) {
                if (pDuration % Math.round(250 / amplifier) == 0) {
                    livingEntity.hurt(DestroyDamageSources.ALCOHOL, 1f);
                };
            };
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    };
}
