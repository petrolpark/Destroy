package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.capability.player.babyblue.PlayerBabyBlueAddictionProvider;
import com.petrolpark.destroy.world.DestroyDamageSources;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;

public class BabyBlueWithdrawalMobEffect extends UncurableMobEffect {

    public BabyBlueWithdrawalMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x91B1B7);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31875c8a-f500-477c-ac52-70355c6adc12", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "0a7d851c-b38b-47c8-9131-348a492e3af8", (double)-0.45F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "93bfb982-7e97-472f-b2f6-a0c51b4d916f", (double)-1.0F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    @SuppressWarnings("null") // We know the effect isn't null if its ticking
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int duration = livingEntity.getEffect(DestroyMobEffects.BABY_BLUE_WITHDRAWAL.get()).getDuration(); // This is the bit it says is null

            if (livingEntity instanceof Player) {
                livingEntity.getCapability(PlayerBabyBlueAddictionProvider.PLAYER_BABY_BLUE_ADDICTION).ifPresent(babyBlueAddiction -> {
                    if (duration % Math.round(100 / (Math.log(babyBlueAddiction.getBabyBlueAddiction() + 1))) == 0) { // Apply damage at a rate roughly equal to ln(baby blue addiction level)
                        livingEntity.hurt(DestroyDamageSources.babyBlueOverdose(livingEntity.level()), 1f);
                    }
                });
            } else if (duration % 50 == 0) { // For non-players, deal damage at a set rate
                livingEntity.hurt(DestroyDamageSources.babyBlueOverdose(livingEntity.level()), 1f);
            };
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    };
}
