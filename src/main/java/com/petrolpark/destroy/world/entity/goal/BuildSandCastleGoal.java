package com.petrolpark.destroy.world.entity.goal;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.behaviour.SentimentalBehaviour;
import com.petrolpark.destroy.item.BucketAndSpadeItem;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class BuildSandCastleGoal extends MoveToBlockGoal {

    private int ticksSinceReachedGoal; // How long the Mob has been 'building' the Sand Castle for
    private boolean mustHaveBucketAndSpade;

    public BuildSandCastleGoal(PathfinderMob mob, boolean mustHaveBucketAndSpade) {
        super(mob, 0.5f, 15);
        this.mustHaveBucketAndSpade = mustHaveBucketAndSpade;
    };

    /**
     * Mostly copied from the similar {@link net.minecraft.world.entity.ai.goal.RemoveBlockGoal#canUse "Remove Block"} Goal.
     */
    @Override
    public boolean canUse() {
        if (mustHaveBucketAndSpade && !(mob instanceof Villager villager && villager.getInventory().hasAnyMatching(DestroyItems.BUCKET_AND_SPADE::isIn))) {
            return false;
        };
        if (nextStartTick > 0) {
            --nextStartTick;
            return false;
        } else if (tryFindBlock()) {
            nextStartTick = reducedTickDelay(20);
            return true;
        } else {
            nextStartTick = nextStartTick(mob);
            return false;
        }
    };

    private boolean tryFindBlock() {
        return blockPos != null && isValidTarget(mob.level(), blockPos) ? true : findNearestBlock();
    };

    @Override
    public void start() {
        ticksSinceReachedGoal = 0;
        super.start();
    };

    @Override
    public void tick() {
        super.tick();
        Level level = mob.level();

        BlockPos targetPos = getPosWithBlock(mob.blockPosition(), level);

        if (isReachedTarget() && targetPos != null) { // We have arrived
            ticksSinceReachedGoal++; // Increment the amount of time for which the Sand Castle has been being built
            BlockState blockState = level.getBlockState(targetPos);

            // Add Particles
            BlockParticleOption particleData = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            RandomSource rand = mob.getRandom();
            Vec3 v = Vec3.atBottomCenterOf(targetPos.above());
            if (!level.isClientSide()) {
                ((ServerLevel)level).sendParticles(particleData, v.x, v.y, v.z, 3, (rand.nextFloat() - 0.5d) * 0.08D, (rand.nextFloat() - 0.5d) * 0.08d, (rand.nextFloat() - 0.5d) * 0.08d, 0.15d);
            };

            // Add Sound
            if (ticksSinceReachedGoal % 12 == 0) {
                level.playSound(null, targetPos.above(), SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 0.5f, 0.9f + rand.nextFloat() * 0.2f);
            };

            // Build Sand Castle
            if (ticksSinceReachedGoal > 60) {
                if (level.setBlockAndUpdate(targetPos.above(), BucketAndSpadeItem.getSandCastleForMaterial(blockState))) { // Place the Sand Castle in the Level
                    SentimentalBehaviour ownerBehaviour = BlockEntityBehaviour.get(level, targetPos.above(), SentimentalBehaviour.TYPE);
                    if (ownerBehaviour != null) ownerBehaviour.setOwner(mob); // Mark this Entity as the owner of it
                    
                    // Damage the Bucket and Spade if necessary
                    if (mob instanceof Villager villager && mustHaveBucketAndSpade) {
                        SimpleContainer inv = villager.getInventory();
                        for (int i = 0; i < inv.getContainerSize(); i++) {
                            ItemStack stack = inv.getItem(i);
                            if (DestroyItems.BUCKET_AND_SPADE.isIn(stack)) stack.hurtAndBreak(1, villager, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                        };
                    };
                };
                ticksSinceReachedGoal = 0;
                stop();
            };
        };
    };

    /**
     * Check for a valid position from which to build the Sand Castle.
     * @param pos The position of the Block on top of which the Sand Castle should be built
     * @param level
     */
    @Nullable
    private BlockPos getPosWithBlock(BlockPos pos, BlockGetter level) {
        if (BucketAndSpadeItem.canSandCastleBeBuiltOn(level.getBlockState(pos), level.getBlockState(pos.above()))) {
            return pos;
        } else {
            BlockPos[] possiblePositions = new BlockPos[] {pos.below(), pos.west(), pos.east(), pos.north(), pos.south(), pos.below().below()};
            for (BlockPos possiblePos : possiblePositions) {
                if (BucketAndSpadeItem.canSandCastleBeBuiltOn(level.getBlockState(possiblePos), level.getBlockState(pos.above()))) {
                    return possiblePos;
                };
            };
            return null;
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        ChunkAccess chunkAccess = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
        if (chunkAccess == null) {
            return false;
        } else {
            return BucketAndSpadeItem.canSandCastleBeBuiltOn(chunkAccess.getBlockState(pos), chunkAccess.getBlockState(pos.above()));
        }
    };
    
};
