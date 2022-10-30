package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.Destroy;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//TODO fix the gee dang colours!!

public class DestroyMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
        = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Destroy.MOD_ID);

    public static final RegistryObject<MobEffect> FRAGRANCE = MOB_EFFECTS.register("fragrance",
        () -> new DummyMobEffect(MobEffectCategory.BENEFICIAL, 0)
    );

    public static final RegistryObject<MobEffect> HANGOVER = MOB_EFFECTS.register("hangover",
        () -> new HangoverMobEffect(MobEffectCategory.HARMFUL, 0)
    );

    public static final RegistryObject<MobEffect> INEBRIATION = MOB_EFFECTS.register("inebriation",
        () -> new InebriationMobEffect(MobEffectCategory.HARMFUL, 0)
    );

    public static final RegistryObject<MobEffect> METH_HIGH = MOB_EFFECTS.register("meth_high",
        () -> new MethHighMobEffect(MobEffectCategory.BENEFICIAL, 0)
    );

    public static final RegistryObject<MobEffect> METH_WITHDRAWAL = MOB_EFFECTS.register("meth_withdrawal",
        () -> new MethWithdrawalMobEffect(MobEffectCategory.HARMFUL, 0)
    );

    public static final RegistryObject<MobEffect> SUN_PROTECTION = MOB_EFFECTS.register("sun_protection",
        () -> new DummyMobEffect(MobEffectCategory.BENEFICIAL, 0)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
