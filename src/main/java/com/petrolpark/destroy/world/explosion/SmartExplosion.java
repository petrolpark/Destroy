package com.petrolpark.destroy.world.explosion;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public abstract class SmartExplosion extends Explosion {

    public SmartExplosion(Level level, Entity source, double toBlowX, double toBlowY, double toBlowZ, float radius, List<BlockPos> positions) {
        super(level, source, toBlowX, toBlowY, toBlowZ, radius, positions);
    };

    //TODO replace with something better
    public boolean shouldDoSpecialDrops() {
        return false;
    };
    
};
