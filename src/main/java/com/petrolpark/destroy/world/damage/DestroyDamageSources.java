package com.petrolpark.destroy.world.damage;

import com.petrolpark.destroy.chemistry.Molecule;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class DestroyDamageSources {

    public static DamageSource alcohol(Level level) {
        return source(DestroyDamageTypes.Keys.ALCOHOL, level);
    };

    public static DamageSource chemicalBurn(Level level) {
        return source(DestroyDamageTypes.Keys.CHEMICAL_BURN, level);
    };

    public static ChemicalDamageSource chemicalPoison(Level level, Molecule molecule) {
        return chemicalSource(DestroyDamageTypes.Keys.CHEMICAL_POISON, level, molecule);
    };

    public static DamageSource headache(Level level) {
        return source(DestroyDamageTypes.Keys.HEADACHE, level);
    };

    public static DamageSource babyBlueOverdose(Level level) {
        return source(DestroyDamageTypes.Keys.BABY_BLUE_OVERDOSE, level);
    };

    public static DamageSource selfNeedle(Level level) {
        return source(DestroyDamageTypes.Keys.SELF_NEEDLE, level);
    };

    public static DamageSource extrusionDie(Level level) {
        return source(DestroyDamageTypes.Keys.EXTRUSION_DIE, level);
    };

    public static DamageSource needle(Level level, Entity entity) {
        return source(DestroyDamageTypes.Keys.NEEDLE, level, entity);
    };

    /**
     * Copied from the {@link com.simibubi.create.foundation.damageTypes.CreateDamageSources Create source code}.
     */
    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(key));
	};

    /**
     * Copied from the {@link com.simibubi.create.foundation.damageTypes.CreateDamageSources Create source code}.
     */
    private static DamageSource source(ResourceKey<DamageType> key, LevelReader level, Entity entity) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new DamageSource(registry.getHolderOrThrow(key), entity);
	};

    private static ChemicalDamageSource chemicalSource(ResourceKey<DamageType> key, LevelReader level, Molecule molecule) {
		Registry<DamageType> registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
		return new ChemicalDamageSource(registry.getHolderOrThrow(key), molecule);
	};
    
};
