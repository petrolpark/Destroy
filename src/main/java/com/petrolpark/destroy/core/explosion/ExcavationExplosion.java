package com.petrolpark.destroy.core.explosion;

import java.util.HashSet;
import java.util.LinkedList;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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

        if (DestroyAllConfigs.SERVER.blocks.dynamiteExplodesResistant.get()) {
            blocksToExplode.addAll(BlockPos.betweenClosedStream(explosionArea).toList());
        } else {
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
        if (spawnParticles && level instanceof ServerLevel serverLevel) {
            for (double x = explosionArea.minX; x < explosionArea.maxX; x += 3d) {
                for (double y = explosionArea.minY; y < explosionArea.maxY; y += 3d) {
                    for (double z = explosionArea.minZ; z < explosionArea.maxZ; z += 3d) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0d, 0d, 0d, 0.15d);
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
