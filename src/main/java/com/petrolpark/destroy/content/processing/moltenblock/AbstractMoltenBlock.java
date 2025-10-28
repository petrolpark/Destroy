package com.petrolpark.destroy.content.processing.moltenblock;

import java.util.Optional;

import com.petrolpark.destroy.DestroyVoxelShapes;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractMoltenBlock extends Block implements BucketPickup {

    public AbstractMoltenBlock(Properties properties) {
        super(properties);
    };

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this);
    };
    
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    };

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        if (pLevel.isClientSide()) return;
        pLevel.scheduleTick(pPos, this, 1);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.isClientSide()) return;
        pLevel.scheduleTick(pPos, this, 1);
    };

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        trySolidify(level, pos, random);
    };

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!level.isClientSide()) level.levelEvent(2001, pos, Block.getId(state));
        return new ItemStack(asItem());
    };

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getFeetBlockState().is(this)) entity.makeStuckInBlock(state, new Vec3(0.9d, 1.5d, 0.9d));
        entity.setSecondsOnFire(3);
    };

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (fallDistance >= 4f && entity instanceof LivingEntity livingEntity) {
            LivingEntity.Fallsounds fallSounds = livingEntity.getFallSounds();
            entity.playSound(fallDistance < 7f ? fallSounds.small() : fallSounds.big(), 1f, 1f);
        };
    };

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entitycollisioncontext) {
            Entity entity = entitycollisioncontext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > 2.5f) return DestroyVoxelShapes.SEMI_MOLTEN_BLOCK_COLLISION;
                if (entity instanceof FallingBlockEntity || (entity instanceof LivingEntity livingEntity && livingEntity.canStandOnFluid(Fluids.LAVA.getSource(false)))) return Shapes.block();
            };
        };
        return Shapes.empty();
    };

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    };

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
    };

    public final void trySolidify(Level level, BlockPos pos, RandomSource random) {
        boolean success = false;
        for (Direction direction : Iterate.directions) {
            BlockPos adjPos = pos.relative(direction);
            BlockState state = level.getBlockState(adjPos);
            FluidState fluidState = level.getFluidState(adjPos);
            if (state.is(BlockTags.ICE)) {
                success = true;
                if (state.getBlock() instanceof IceBlock) {
                    if (level.dimensionType().ultraWarm()) {
                        level.removeBlock(adjPos, false);
                    } else {
                        level.setBlockAndUpdate(adjPos, IceBlock.meltsInto());
                    };
                };
            } else if (fluidState.is(FluidTags.WATER)) success = true;
        };
        if (!success) return;
        level.setBlockAndUpdate(pos, getSolidifiedBlockState());
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS);
        if (level instanceof ServerLevel serverLevel) for (int i = 0; i < 8; i++) serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2d, (double)pos.getZ() + random.nextDouble(), 1,0d, 0d, 0d, 0d);
    };

    public abstract BlockState getSolidifiedBlockState(); 
};
