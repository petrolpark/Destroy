package com.petrolpark.destroy.world.explosion;

import com.petrolpark.destroy.world.explosion.DebrisMiningExplosion.FluidDestroyingDamageCalculator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class UnderwaterExplosion extends SmartExplosion {

    public static class UnderwaterDamageCalculator extends FluidDestroyingDamageCalculator {

        @Override
        public boolean shouldBlockExplode(Explosion pExplosion, BlockGetter reader, BlockPos pos, BlockState pState, float pPower) {
            return reader.getFluidState(pos).isEmpty();
        };

    };

    public UnderwaterExplosion(Level level, Entity source, Vec3 position, float radius, float smoothness) {
        super(level, source, null, new UnderwaterDamageCalculator(), position, radius, smoothness);
    };
    
};
