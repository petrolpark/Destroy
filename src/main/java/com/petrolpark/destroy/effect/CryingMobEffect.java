package com.petrolpark.destroy.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CryingMobEffect extends MobEffect {

    protected CryingMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0xCBF2F0);
    };

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level.isClientSide()) {
            Vec3 pos = livingEntity.getEyePosition();
            livingEntity.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z, Mth.cos(Mth.PI * (livingEntity.getYHeadRot() + 90) / 180) * 0.7d, 0d, Mth.sin(Mth.PI * (livingEntity.getYHeadRot() + 90) / 180) * 0.7d);
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    };
    
};
