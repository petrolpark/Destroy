package com.petrolpark.destroy.fluid.pipeEffectHandler;

import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.simibubi.create.content.fluids.OpenEndedPipe;

import net.minecraft.world.effect.MobEffectInstance;

public class DestroyOpenEndedPipeEffects {
  
    public static final void register() {
        OpenEndedPipe.registerEffectHandler(new MixtureOpenEndedPipeEffectHandler());
        OpenEndedPipe.registerEffectHandler(new BurningOpenEndedPipeEffectHandler(DestroyFluids.NAPALM_SUNDAE.get()));
        OpenEndedPipe.registerEffectHandler(new BurningOpenEndedPipeEffectHandler(DestroyFluids.MOLTEN_CINNABAR.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.FRAGRANCE.get(), 21, 0, false, false, true), DestroyFluids.PERFUME.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 0, false, false, true), DestroyFluids.UNDISTILLED_MOONSHINE.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 0, false, false, true), DestroyFluids.ONCE_DISTILLED_MOONSHINE.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 1, false, false, true), DestroyFluids.TWICE_DISTILLED_MOONSHINE.get()));
        OpenEndedPipe.registerEffectHandler(new EffectApplyingOpenEndedPipeEffect(new MobEffectInstance(DestroyMobEffects.INEBRIATION.get(), 21, 2, false, false, true), DestroyFluids.THRICE_DISTILLED_MOONSHINE.get()));
    };
};
