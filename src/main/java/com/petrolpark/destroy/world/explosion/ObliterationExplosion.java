package com.petrolpark.destroy.world.explosion;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ObliterationExplosion extends SmartExplosion {

    public ObliterationExplosion(Level level, Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, Vec3 position, float radius, float smoothness) {
        super(level, source, damageSource, damageCalculator, position, radius, smoothness);
    };

    @Override
    public boolean shouldDoSpecialDrops() {
        return true;
    };
    
};
