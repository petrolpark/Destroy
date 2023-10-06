package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.world.DestroyDamageSources;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class InebriationMobEffect extends UncurableMobEffect {
    
    public InebriationMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xE88010);
    };

    @SuppressWarnings("null")
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        int pDuration = livingEntity.getEffect(DestroyMobEffects.INEBRIATION.get()).getDuration(); // This is the bit it says is null
        if (!livingEntity.level().isClientSide()) {
            if (amplifier >= 3) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 15, (amplifier - 2), true, false, false));
            };
            if (amplifier >= 6) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 15, (amplifier - 6), true, false, false));
            };
            if (amplifier >= 9) {
                if (pDuration % Math.round(250 / amplifier) == 0) {
                    livingEntity.hurt(DestroyDamageSources.alcohol(livingEntity.level()), 1f);
                };
                if (livingEntity instanceof Player player) {
                    DestroyAdvancements.VERY_DRUNK.award(player.level(), player);
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
