package com.petrolpark.destroy.world.explosion;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class DebrisMiningExplosion extends SmartExplosion {

    public static class FluidDestroyingDamageCalculator extends ExplosionDamageCalculator {

        @Override
        public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, FluidState fluid) {
            if (!fluid.isEmpty()) return Optional.of(0f);
            return state.isAir() ? Optional.empty() : Optional.of(state.getExplosionResistance(reader, pos, explosion));
        };
    };

    public DebrisMiningExplosion(Level level, Entity source, Vec3 position, float radius, float smoothness) {
        super(level, source, null, new FluidDestroyingDamageCalculator(), position, radius, smoothness);
    };

    @Override
    public void explodeEntity(Entity entity, float strength) {
        // Do nothing (this type of explosion does not affect Entities)
    };

    @Override
    public void explodeBlock(BlockPos pos) {
        // Do nothing (this type of explosion does not drop Block Items)
    };
    
};
