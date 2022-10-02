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
        () -> new DummyMobEffect(MobEffectCategory.BENEFICIAL, 11823615)
    );

    public static final RegistryObject<MobEffect> METH_HIGH = MOB_EFFECTS.register("meth_high",
        () -> new MethHighMobEffect(MobEffectCategory.BENEFICIAL, 139220235)
    );

    public static final RegistryObject<MobEffect> METH_WITHDRAWAL = MOB_EFFECTS.register("meth_withdrawal",
        () -> new MethWithdrawalMobEffect(MobEffectCategory.HARMFUL, 126169177)
    );

    public static final RegistryObject<MobEffect> SUN_PROTECTION = MOB_EFFECTS.register("sun_protection",
        () -> new MethWithdrawalMobEffect(MobEffectCategory.BENEFICIAL, 12038545)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
