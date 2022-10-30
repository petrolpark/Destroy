package com.petrolpark.destroy.util;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class AlcoholHandler {

    public static void increaseInebriation(LivingEntity entity, int level) {

        MobEffect INEBRIATION = DestroyMobEffects.INEBRIATION.get();
        int INEBRIATION_DURATION = DestroyAllConfigs.SERVER.substances.inebriationDuration.get();
        //int INEBRIATION_DURATION = 1200;

        if (entity.hasEffect(INEBRIATION)) {
            int currentAmplifier = entity.getEffect(INEBRIATION).getAmplifier();
            int currentDuration = entity.getEffect(INEBRIATION).getDuration();
            entity.removeEffect(INEBRIATION);
            entity.addEffect(new MobEffectInstance(INEBRIATION, Math.max(currentDuration + (INEBRIATION_DURATION * level), 0), Math.max(currentAmplifier + level, 0), false, false, true));
        } else if (level >= 1) { // If the Entity is not already inebriated and we are attempting to add levels
            entity.addEffect(new MobEffectInstance(INEBRIATION, INEBRIATION_DURATION * level, level - 1, false, false, true));
        };
    }
};
