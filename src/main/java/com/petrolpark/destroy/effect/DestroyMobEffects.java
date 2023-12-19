package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.Destroy;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Destroy.MOD_ID);

    public static final RegistryObject<MobEffect> 

    CANCER = MOB_EFFECTS.register("cancer",
        () -> new DestroyMobEffect(MobEffectCategory.NEUTRAL, 0)
    ),
    CHEMICAL_POISON = MOB_EFFECTS.register("chemical_poison", ChemicalPoisonMobEffect::new),
    CRYING = MOB_EFFECTS.register("crying", CryingMobEffect::new),
    FRAGRANCE = MOB_EFFECTS.register("fragrance",
        () -> new DestroyMobEffect(MobEffectCategory.BENEFICIAL, 0xF294D9)
    ),
    HANGOVER = MOB_EFFECTS.register("hangover", HangoverMobEffect::new),
    INEBRIATION = MOB_EFFECTS.register("inebriation", InebriationMobEffect::new),
    BABY_BLUE_HIGH = MOB_EFFECTS.register("baby_blue_high", BabyBlueHighMobEffect::new),
    BABY_BLUE_WITHDRAWAL = MOB_EFFECTS.register("baby_blue_withdrawal", BabyBlueWithdrawalMobEffect::new),
    SUN_PROTECTION = MOB_EFFECTS.register("sun_protection",
        () -> new DestroyMobEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFE)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    };

    public static MobEffectInstance cancerInstance() {
        return new MobEffectInstance(CANCER.get(), MobEffectInstance.INFINITE_DURATION, 0, false, false, true);
    };
}
