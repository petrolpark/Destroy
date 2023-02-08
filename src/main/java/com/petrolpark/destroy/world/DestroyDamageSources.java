package com.petrolpark.destroy.world;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DestroyDamageSources {
    public static final DamageSource ACID_BURN = (new DamageSource("acidBurn"));
    public static final DamageSource ALCOHOL = (new DamageSource("alcohol"));
    public static final DamageSource BASE_BURN = (new DamageSource("baseBurn"));
    public static final DamageSource HEADACHE = (new DamageSource("headache")).bypassArmor();
    public static final DamageSource BABY_BLUE_OVERDOSE = (new DamageSource("methOverdose")).bypassArmor();
    public static final DamageSource SELF_NEEDLE = (new DamageSource("selfNeedle"));

    public static DamageSource needle(LivingEntity injectingEntity) {
        return new EntityDamageSource("needle", injectingEntity);
    };
}
