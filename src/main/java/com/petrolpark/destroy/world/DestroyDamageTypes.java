package com.petrolpark.destroy.world;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.damageTypes.DamageTypeData;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;

public class DestroyDamageTypes {

    public static final DamageTypeData 

    ACID_BURN = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("acid_burn"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        .build(),

    ALCOHOL = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("alcohol"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(),

    BASE_BURN = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("base_burn"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        .build(),

    HEADACHE = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("headache"))
        .exhaustion(0.2f)
        .scaling(DamageScaling.ALWAYS)
        .tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(), //Bypass armor

    BABY_BLUE_OVERDOSE = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("baby_blue_overdose"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(), //Bypass armor

    SELF_NEEDLE = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("self_needle"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.NEVER)
        .build(),

    NEEDLE = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("needle"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .build(),

    EXTRUSION_DIE = new DamageTypeData.Builder()
        .simpleId(Destroy.asResource("extrusion_die"))
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.POKING)
        .build();
};
