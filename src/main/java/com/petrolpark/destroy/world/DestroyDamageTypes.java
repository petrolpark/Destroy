package com.petrolpark.destroy.world;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class DestroyDamageTypes {

    public static class Keys {
        public static ResourceKey<DamageType>
            ACID_BURN = key("acid_burn"),
            ALCOHOL = key("alcohol"),
            BASE_BURN = key("base_burn"),
            HEADACHE = key("headache"),
            BABY_BLUE_OVERDOSE = key("baby_blue_overdose"),
            SELF_NEEDLE = key("self_needle"),
            NEEDLE = key("needle"),
            EXTRUSION_DIE = key("extrusion_die");
    };

    private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, Destroy.asResource(name));
	};

    public static final DamageType 

    ACID_BURN = new DamageTypeBuilder(Keys.ACID_BURN)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        .build(),

    ALCOHOL = new DamageTypeBuilder(Keys.ALCOHOL)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        //.tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(),

    BASE_BURN = new DamageTypeBuilder(Keys.BASE_BURN)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        .build(),

    HEADACHE = new DamageTypeBuilder(Keys.HEADACHE)
        .exhaustion(0.2f)
        .scaling(DamageScaling.ALWAYS)
        //.tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(), //Bypass armor

    BABY_BLUE_OVERDOSE = new DamageTypeBuilder(Keys.BABY_BLUE_OVERDOSE)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        //.tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(), //Bypass armor

    SELF_NEEDLE = new DamageTypeBuilder(Keys.SELF_NEEDLE)
        .exhaustion(0.1f)
        .scaling(DamageScaling.NEVER)
        .build(),

    NEEDLE = new DamageTypeBuilder(Keys.NEEDLE)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .build(),

    EXTRUSION_DIE = new DamageTypeBuilder(Keys.EXTRUSION_DIE)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.POKING)
        .build();
};
