package com.petrolpark.destroy.core.explosion;

import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplodePonderInstruction extends PonderInstruction {

    public final ExplosionFactory explosionFactory;

    public ExplodePonderInstruction(ExplosionFactory explosion) {
        this.explosionFactory = explosion;
    };

    @Override
    public boolean isComplete() {
        return true;
    };

    @Override
    public void tick(PonderScene scene) {
        Explosion explosion = explosionFactory.create(scene.getWorld());
        explosion.explode();
        explosion.finalizeExplosion(true);
    };

    @FunctionalInterface
    public static interface ExplosionFactory {

        public Explosion create(Level level);
    };
    
};
