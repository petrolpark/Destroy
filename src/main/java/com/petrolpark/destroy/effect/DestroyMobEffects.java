package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.Destroy;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
        = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Destroy.MOD_ID);

    public static final RegistryObject<MobEffect> FRAGRANCE = MOB_EFFECTS.register("fragrance",
        () -> new DummyMobEffect(MobEffectCategory.BENEFICIAL, 0xF294D9)
    );

    public static final RegistryObject<MobEffect> HANGOVER = MOB_EFFECTS.register("hangover",
        () -> new HangoverMobEffect(MobEffectCategory.HARMFUL, 0x59390B)
    );

    public static final RegistryObject<MobEffect> INEBRIATION = MOB_EFFECTS.register("inebriation",
        () -> new InebriationMobEffect(MobEffectCategory.HARMFUL, 0xE88010)
    );

    public static final RegistryObject<MobEffect> BABY_BLUE_HIGH = MOB_EFFECTS.register("baby_blue_high",
        () -> new BabyBlueHighMobEffect(MobEffectCategory.BENEFICIAL, 0x8BDCEB)
    );

    public static final RegistryObject<MobEffect> BABY_BLUE_WITHDRAWAL = MOB_EFFECTS.register("baby_blue_withdrawal",
        () -> new BabyBlueWithdrawalMobEffect(MobEffectCategory.HARMFUL, 0x91B1B7)
    );

    public static final RegistryObject<MobEffect> SUN_PROTECTION = MOB_EFFECTS.register("sun_protection",
        () -> new DummyMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
