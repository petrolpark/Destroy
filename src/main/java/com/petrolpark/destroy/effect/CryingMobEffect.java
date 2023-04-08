package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.client.particle.TearParticle;
import com.petrolpark.destroy.networking.DestroyMessages;
import com.petrolpark.destroy.networking.packet.CryingS2CPacket;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.Vec3;

public class CryingMobEffect extends MobEffect {

    protected CryingMobEffect() {
        super(MobEffectCategory.NEUTRAL, 0xCBF2F0);
    };

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
        DestroyMessages.sendToClientsTrackingEntity(new CryingS2CPacket(livingEntity, true), livingEntity); // Let the Client know the Effect has been applied (otherwise Tears won't render until reload)
    };

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
        DestroyMessages.sendToClientsTrackingEntity(new CryingS2CPacket(livingEntity, false), livingEntity); // Let the Client know the Effect is gone once it runs out
    };

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        RandomSource rand = livingEntity.getRandom();
        if (livingEntity.level.isClientSide() && rand.nextFloat() > 0.8f) {
            Vec3 pos = livingEntity.getEyePosition();
            livingEntity.level.addParticle(new TearParticle.Data(), pos.x, pos.y, pos.z,
                Mth.cos(Mth.PI * (livingEntity.getYHeadRot() + 90 - 15 + rand.nextFloat() * 30) / 180) * 0.1d, 
                0d,
                Mth.sin(Mth.PI * (livingEntity.getYHeadRot() + 90 - 15 + rand.nextFloat() * 30) / 180) * 0.1d
            );
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    };
    
};
