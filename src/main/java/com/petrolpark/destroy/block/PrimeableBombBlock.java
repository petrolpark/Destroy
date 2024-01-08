package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.entity.PrimedBomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PrimeableBombBlock extends TntBlock {

    protected PrimedBombEntityFactory factory;

    @FunctionalInterface
    public static interface PrimedBombEntityFactory {
        PrimedBomb create(Level level, BlockPos pos, @Nullable BlockState state, @Nullable LivingEntity igniter);
    };

    public PrimeableBombBlock(Properties properties, PrimedBombEntityFactory factory) {
        super(properties);
        this.factory = factory;
        DispenserBlock.registerBehavior(this, new PrimeableBombDispenseBehaviour());
    };

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (factory == null || level.isClientSide()) return;
        PrimedBomb entity = factory.create(level, pos, state, igniter);
        level.addFreshEntity(entity);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    };

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide() && factory != null) {
            PrimedBomb entity = factory.create(level, pos, null, explosion.getIndirectSourceEntity());
            int i = entity.getFuse();
            entity.setFuse((short)(level.random.nextInt(i / 4) + i / 8));
            level.addFreshEntity(entity);
        };
    };

    /**
     * Get the fuse time for this bomb.
     * This does not get called when the entity is exploded by another explosion.
     */
    public int getFuseTime(Level world, BlockPos pos, BlockState state) {
        return 80;
    };

    public class PrimeableBombDispenseBehaviour extends OptionalDispenseItemBehavior {

        @Override
        protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
            Level level = blockSource.getLevel();
            BlockPos pos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
            PrimedBomb bomb = factory.create(level, pos, null, null);
            level.addFreshEntity(bomb);
            level.playSound(null, bomb.getX(), bomb.getY(), bomb.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
            stack.shrink(1);
            return stack;
        };
    };
    
};
