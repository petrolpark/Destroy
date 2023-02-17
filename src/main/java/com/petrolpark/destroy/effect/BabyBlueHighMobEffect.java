package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.capability.babyblue.PlayerBabyBlueAddictionProvider;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

public class BabyBlueHighMobEffect extends UncurableMobEffect {
    public BabyBlueHighMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31875c8a-f500-477c-ac52-70355c6adc12", (double)0.3F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "0a7d851c-b38b-47c8-9131-348a492e3af8", (double)0.9F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "93bfb982-7e97-472f-b2f6-a0c51b4d916f", (double)2.0F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    @SuppressWarnings("null") // It's not null; I checked
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level.isClientSide) {
            int duration = livingEntity.getEffect(DestroyMobEffects.BABY_BLUE_HIGH.get()).getDuration(); // This is the bit it says is null
            if (duration == 1) {
                // Apply the Baby Blue Withdrawal Effect as the BabyBlue High Effect runs out.
                if (livingEntity instanceof Player) {
                    livingEntity.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(babyBlueAddiction -> {
                        livingEntity.addEffect(new MobEffectInstance(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get(), (10 + babyBlueAddiction.getBabyBlueAddiction()) * 20)); // Change the length of the effect depending on the Addiction level
                    });
                };
            } else {
                livingEntity.removeEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get());
            };
        }

        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Apply effects every tick
    }
}
