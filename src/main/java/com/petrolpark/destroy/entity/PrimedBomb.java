package com.petrolpark.destroy.entity;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.PrimeableBombBlock;
import com.petrolpark.destroy.util.ExplosionHelper;
import com.petrolpark.destroy.world.explosion.AnfoExplosion;
import com.petrolpark.destroy.world.explosion.DebrisMiningExplosion;
import com.petrolpark.destroy.world.explosion.ObliterationExplosion;
import com.petrolpark.destroy.world.explosion.SmartExplosion;
import com.petrolpark.destroy.world.explosion.UnderwaterExplosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class PrimedBomb extends PrimedTnt {

    @Nullable
    protected LivingEntity owner;

    public PrimedBomb(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    };

    protected PrimedBomb(EntityType<? extends PrimedTnt> entityType, Level level, BlockPos blockPos, BlockState state, @Nullable LivingEntity owner) {
        super(entityType, level);
        Vec3 pos = Vec3.atBottomCenterOf(blockPos);
        setPos(pos.x, pos.y, pos.z);
        xo = pos.x;
        yo = pos.y;
        zo = pos.z;
        this.owner = owner;
        setFuse(state.getBlock() instanceof PrimeableBombBlock primeableBombBlock ? primeableBombBlock.getFuseTime(level, blockPos, state) : 80);
    };

    @Override
    protected void explode() {
        if (level() instanceof ServerLevel serverLevel)
        ExplosionHelper.explode(serverLevel, getExplosion(serverLevel, position, this));
    };

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    };

    public abstract BlockState getBlockStateToRender();

    public abstract SmartExplosion getExplosion(Level level, Vec3 position, @Nullable Entity source);

    public static class Anfo extends PrimedBomb {

        public Anfo(EntityType<? extends PrimedTnt> entityType, Level level) {
            super(entityType, level);
        };

        public Anfo(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity owner) {
            super(DestroyEntityTypes.PRIMED_ANFO.get(), level, pos, state, owner);
        };

        @Override
        public SmartExplosion getExplosion(Level level, Vec3 position, @Nullable Entity source) {
            return new AnfoExplosion(level, source, position, 5, 0.6f);
        }

        @Override
        public BlockState getBlockStateToRender() {
            return DestroyBlocks.ANFO_BLOCK.getDefaultState();
        };

    };

    public static class PicricAcid extends PrimedBomb {

        public PicricAcid(EntityType<? extends PrimedTnt> entityType, Level level) {
            super(entityType, level);
        };

        public PicricAcid(Level level, BlockPos blockPos, BlockState state, @Nullable LivingEntity owner) {
            super(DestroyEntityTypes.PRIMED_PICRIC_ACID.get(), level, blockPos, state, owner);
        };

        @Override
        public BlockState getBlockStateToRender() {
            return DestroyBlocks.PICRIC_ACID_BLOCK.getDefaultState();
        };

        @Override
        public SmartExplosion getExplosion(Level level, Vec3 position, @Nullable Entity source) {
            return new UnderwaterExplosion(level, source, position, 5, 0.6f);
        };

    };

    public static class Cordite extends PrimedBomb {

        public Cordite(EntityType<? extends PrimedTnt> entityType, Level level) {
            super(entityType, level);
        };

        public Cordite(Level level, BlockPos blockPos, BlockState state, @Nullable LivingEntity owner) {
            super(DestroyEntityTypes.PRIMED_CORDITE.get(), level, blockPos, state, owner);
        };

        @Override
        public BlockState getBlockStateToRender() {
            return DestroyBlocks.CORDITE.getDefaultState();
        };

        @Override
        public SmartExplosion getExplosion(Level level, Vec3 position, @Nullable Entity source) {
            return new DebrisMiningExplosion(level, source, position, 10, 0.6f);
        };

    };

    public static class Nitrocellulose extends PrimedBomb {

        public Nitrocellulose(EntityType<? extends PrimedTnt> entityType, Level level) {
            super(entityType, level);
        };

        public Nitrocellulose(Level level, BlockPos blockPos, BlockState state, @Nullable LivingEntity owner) {
            super(DestroyEntityTypes.PRIMED_NITROCELLULOSE.get(), level, blockPos, state, owner);
        };

        @Override
        public BlockState getBlockStateToRender() {
            return DestroyBlocks.NITROCELLULOSE_BLOCK.getDefaultState();
        };

        @Override
        public SmartExplosion getExplosion(Level level, Vec3 position, @Nullable Entity source) {
            return new ObliterationExplosion(level, source, null, null, position, 3, 0.8f);
        };

    };
    
};
