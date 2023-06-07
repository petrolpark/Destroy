package com.petrolpark.destroy.world.explosion;

import java.util.HashSet;
import java.util.LinkedList;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ExcavationExplosion extends SmartExplosion {

    private final BlockPos center;
    private final AABB explosionArea;

    public ExcavationExplosion(Level level, Entity source, BlockPos position, AABB explosionArea) {
        super(level, source, null, null, VecHelper.getCenterOf(position), 0f, 0f);
        this.center = position;
        this.explosionArea = explosionArea;
    };

    public ExplosionResult getExplosionResult() {
        Set<BlockPos> blocksToExplode = new HashSet<>(); // Blocks which will be exploded by the end of this
        Set<BlockPos> blocksTriedToExplode = new HashSet<>(); // Blocks which have ever been marked for checking to see if they should explode
        Queue<BlockPos> blocksToTryExplode = new LinkedList<>(); // Blocks which need to be checked to see if they should explode
        blocksToTryExplode.add(center);

        // Flood fill the Explosion area (this ensures Blocks protected by unbreakable Blocks do not get destroyed)
        while (!blocksToTryExplode.isEmpty()) {
            BlockPos pos = blocksToTryExplode.remove();
            if (shouldExplode(pos)) {
                blocksToExplode.add(pos);
                tryExplodeMoreBlocks: for (Direction direction : Direction.values()) {
                    BlockPos newPos = pos.relative(direction);
                    if (blocksTriedToExplode.contains(newPos)) continue tryExplodeMoreBlocks;
                    blocksTriedToExplode.add(newPos);
                    blocksToTryExplode.add(newPos);
                };
            };
        };

        return new ExplosionResult(blocksToExplode, Map.of());
    };

    private boolean shouldExplode(BlockPos pos) {
        if (!level.isInWorldBounds(pos)) return false;
        if (!explosionArea.contains(VecHelper.getCenterOf(pos))) return false;
        return level.getBlockState(pos).getExplosionResistance(level, pos, this) < 1000f;
    };

    @Override
    public void effects(boolean spawnParticles) {
        // Sounds
        if (level.isClientSide()) {
            level.playLocalSound(position.x, position.y, position.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, (1.0f + (random.nextFloat() * 0.4f)) * 0.7f, false);
        };
        // Particles
        if (spawnParticles) {
            for (double x = explosionArea.minX; x < explosionArea.maxX; x += 2d) {
                for (double y = explosionArea.minY; y < explosionArea.maxY; y += 2d) {
                    for (double z = explosionArea.minZ; z < explosionArea.maxZ; z += 2d) {
                        level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0d, 0d, 0d);
                    };
                };
            };
        };
    };

    @Override
    public void explodeBlock(BlockPos pos) {
        // Do nothing (this type of explosion does not drop Block Items)
    };
    
};
