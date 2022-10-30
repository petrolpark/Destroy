package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.capability.methaddiction.PlayerMethAddictionProvider;
import com.petrolpark.destroy.world.DestroyDamageSources;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

public class MethWithdrawalMobEffect extends UncurableMobEffect {
    public MethWithdrawalMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31875c8a-f500-477c-ac52-70355c6adc12", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "0a7d851c-b38b-47c8-9131-348a492e3af8", (double)-0.45F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "93bfb982-7e97-472f-b2f6-a0c51b4d916f", (double)-1.0F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide()) {
            int pDuration = livingEntity.getEffect(DestroyMobEffects.METH_WITHDRAWAL.get()).getDuration();

            if (livingEntity instanceof Player) {
                livingEntity.getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(methAddiction -> {
                    if (pDuration % Math.round(100 / (Math.log(methAddiction.getMethAddiction() + 1))) == 0) { //Apply damage at a rate roughly equal to ln(meth addiction level)
                        livingEntity.hurt(DestroyDamageSources.METH_OVERDOSE, 1f);
                    }
                });
            } else if (pDuration % 50 == 0) { //for non-players, deal damage at a set rate
                livingEntity.hurt(DestroyDamageSources.METH_OVERDOSE, 1f);
            };
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    };
}
