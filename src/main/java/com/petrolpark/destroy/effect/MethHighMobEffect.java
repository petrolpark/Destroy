package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.capability.methaddiction.PlayerMethAddictionProvider;
import com.petrolpark.destroy.world.DestroyDamageSources;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

public class MethHighMobEffect extends UncurableMobEffect {
    public MethHighMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31875c8a-f500-477c-ac52-70355c6adc12", (double)0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "0a7d851c-b38b-47c8-9131-348a492e3af8", (double)0.9F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "93bfb982-7e97-472f-b2f6-a0c51b4d916f", (double)2.0F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide) {
            int duration = livingEntity.getEffect(DestroyMobEffects.METH_HIGH.get()).getDuration();
            if (duration == 1) {
                // Apply the Meth Withdrawal Effect as the Meth High Effect runs out.
                if (livingEntity instanceof Player) {
                    livingEntity.getCapability(PlayerMethAddictionProvider.PLAYER_METH_ADDICTION).ifPresent(methAddiction -> {
                        livingEntity.addEffect(new MobEffectInstance(DestroyMobEffects.METH_WITHDRAWAL.get(), (10 + methAddiction.getMethAddiction()) * 20)); //change the length of the effect depending on the Addiction level
                    });
                };
            } else {
                livingEntity.removeEffect(DestroyMobEffects.METH_WITHDRAWAL.get());
            };
        }

        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Apply effects every tick
    }
}
