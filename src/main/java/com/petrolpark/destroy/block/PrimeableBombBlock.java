package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.entity.PrimedBomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PrimeableBombBlock extends TntBlock {

    protected PrimedBombEntityFactory factory;

    @FunctionalInterface
    public static interface PrimedBombEntityFactory {
        PrimedBomb create(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity igniter);
    };

    public PrimeableBombBlock(Properties properties, PrimedBombEntityFactory factory) {
        super(properties);
        this.factory = factory;
    };

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (factory == null || level.isClientSide()) return;
        PrimedBomb entity = factory.create(level, pos, state, igniter);
        level.addFreshEntity(entity);
        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    };

    public int getFuseTime(Level world, BlockPos pos, BlockState state) {
        return 80;
    };
    
};
