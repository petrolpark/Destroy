package com.petrolpark.destroy.world.damage;

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
            ALCOHOL = key("alcohol"),
            CHEMICAL_BURN = key("chemical_burn"),
            CHEMICAL_POISON = key("chemical_poison"),
            HEADACHE = key("headache"),
            BABY_BLUE_OVERDOSE = key("baby_blue_overdose"),
            SELF_NEEDLE = key("self_needle"),
            NEEDLE = key("needle"),
            EXTRUSION_DIE = key("extrusion_die");
    };

    public static void register() {};

    private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, Destroy.asResource(name));
	};

    public static final DamageType 

    ALCOHOL = new DamageTypeBuilder(Keys.ALCOHOL)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        //.tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(),

    CHEMICAL_BURN = new DamageTypeBuilder(Keys.CHEMICAL_BURN)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        .build(),

    CHEMICAL_POISON = new DamageTypeBuilder(Keys.CHEMICAL_POISON)
        .exhaustion(0.1f)
        .scaling(DamageScaling.ALWAYS)
        .effects(DamageEffects.BURNING)
        //.tag(DamageTypeTags.BYPASSES_ARMOR)
        .build(), //Bypass armor

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
