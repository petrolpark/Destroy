package com.petrolpark.destroy.world.explosion;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.world.loot.DestroyLootContextParams;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class SmartExplosion extends Explosion {

    /**
     * The center (global co-ordinates) of this Explosion.
     */
    protected final Vec3 position;
    /**
     * The Item Stacks which this Explosion will create.
     */
    protected Map<BlockPos, List<ItemStack>> stacksToCreate;
    /**
     * How spherical this Explosion isn't: {@code 0} is perfectly spherical (for Blocks of uniform blast resistance) and {@code 1} is very irregular.
     * Default vanailla explosions have smoothness {@code 0.6}.
     */
    protected final float smoothness;

    /**
     * Create a more flexible Explosion.
     * @param level The Level in which the Explosion is occuring
     * @param source The Entity that caused the Explosion (e.g. a Primed TNT or Creeper)
     * @param damageSource The type of damage this Explosion should deal (defaults to {@code minecraft:explosion})
     * @param damageCalculator The class to calculate the damage done to Blocks (defaults to that of vanilla TNT)
     * @param position The center (global co-ordinates) of this Explosion
     * @param radius How large this Explosion should be
     * @param smoothness How uniform this explosion is ({@code 1f} is spherical)
     */
    public SmartExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, Vec3 position, float radius, float smoothness) {
        super(level, source, damageSource, damageCalculator, position.x, position.y, position.z, radius, false, Explosion.BlockInteraction.KEEP);
        this.position = position;
        this.smoothness = smoothness > 1f ? 1f : smoothness;
        stacksToCreate = new HashMap<>();
    };

    /**
     * {@link SmartExplosion#getBlocksToExplode Determine} the Block Positions affected by an Explosion (stored in {@link net.minecraft.world.level.Explosion#toBlow toBlow}),
     * and {@link SmartExplosion#explodeEntity deal with Entities}. There should be little need to override this.
     */
    @Override
    public void explode() {
        level.gameEvent(source, GameEvent.EXPLODE, position);

        ExplosionResult result = getExplosionResult();  // 'Do' the Explosion
        toBlow.addAll(result.blocksToDestroy()); // Mark all the Exploded Block States for removal

        List<Entity> entities = result.entities().keySet().stream().toList();
        ForgeEventFactory.onExplosionDetonate(level, this, entities, radius * 2); // Allow events to modify affected entities
        for (Entity entity : entities) { // Explode each Entity
            explodeEntity(entity, result.entities().get(entity));
        };
    };

    @Override
    public void finalizeExplosion(boolean spawnParticles) {

        boolean createExperience = getDirectSourceEntity() instanceof Player || shouldAlwaysDropExperience();

        // Generate the Block drops and do anything else necessary
        for (BlockPos pos : toBlow) {
            BlockState state = level.getBlockState(pos);
            explodeBlock(pos);
            // Do special things (slightly deprecated I think)
            if (level instanceof ServerLevel level) {
                state.spawnAfterBreak(level, pos, ItemStack.EMPTY, createExperience);
            };
            // Actually remove the Block
            state.onBlockExploded(level, pos, this);
        };
        // Create all the Item Entities
        for (Entry<BlockPos, List<ItemStack>> entry : stacksToCreate.entrySet()) {
            BlockPos pos = entry.getKey();
            if (level.getBlockState(pos).canDropFromExplosion(level, pos, this)) { // Check the Block State should drop Items
                for (ItemStack stack : entry.getValue()) {
                    Block.popResource(level, pos, stack);
                };
            };
        };

        if (getIndirectSourceEntity() instanceof Player player) DestroyAdvancements.DETONATE.award(level, player);
        
        // Do effects
        effects(spawnParticles);
    };

    /**
     * Get the list of Blocks Positions this explosion should destroy, and the Entities this Explosion should affect.
     * Default implementation is mostly copied from the Minecraft {@link net.minecraft.world.level.Explosion#explode source code}.
     */
    public ExplosionResult getExplosionResult() {
        Set<BlockPos> blocks = new HashSet<>();
        Map<Entity, Float> entities = new HashMap<>();

        int resolution = 8;

        float maxMomentum = radius * (1f + smoothness / 2f); // The maximum momentum any Block or Entity could experience from this Explosion

        // Imagine a cube around the center of the explosion with a (2 * resolution) by (2 * resolution) grid on each face.
        // For each grid square on each face...
        for (int i = -resolution; i <= resolution; i++) {
            for (int j = -resolution; j <= resolution; j++) {
                for (int k = -resolution; k <= resolution; k++) {
                    if (i == -resolution || i == resolution || j == -resolution || j == resolution || k == -resolution || k == resolution) {
                        // ...we determine the direction vector from the center of the Explosion to the center of that square.
                        Vec3 direction = new Vec3(i, j, k).normalize();

                        // We pick a (slightly randomised) 'momentum' in this direction, based on the radius and smoothness of this Explosion
                        // Every time the line of this direction vector runs into a Block or Entity, the momentum will decrease slightly.
                        float momentum = radius * ((1f - smoothness / 2f) + random.nextFloat() * smoothness);

                        // We start at the center of the explosion
                        Vec3 positionToExplode = new Vec3(x, y, z);

                        while (momentum > 0f) {

                            // If there's an Entity in the way...
                            EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(level, source, position, positionToExplode, new AABB(position, positionToExplode), entity -> !entity.ignoreExplosion());
                            if (hitResult != null) {
                                Entity entity = hitResult.getEntity();
                                // ...update the Entity to experience the maximum strength it could experience from this Explosion...
                                entities.merge(hitResult.getEntity(), momentum / maxMomentum, (existingStrength, strength) -> Math.max(existingStrength, strength));
                                // ...and decrease the momentum of the explosion in this direction
                                if (entity instanceof LivingEntity livingEntity) {
                                    momentum -= 0.1f + (0.125f
                                        * (1d - ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, momentum)) // Increase the momentum loss if the Entity has Blast Resistance
                                        * (livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))); // Increase the momentum loss if the Entity has knockback resistance
                                } else {
                                    momentum -= 0.1f;
                                };
                            };

                            BlockPos blockPosToExplode = BlockPos.containing(positionToExplode);
                            BlockState blockState = level.getBlockState(blockPosToExplode);
                            FluidState fluidState = level.getFluidState(blockPosToExplode);

                            // Don't continue if we've moved outside the world
                            if (!level.isInWorldBounds(blockPosToExplode)) {
                                break;
                            };

                            // Determine the decrease in momentum due to the Block or Fluid State
                            Optional<Float> optional = damageCalculator.getBlockExplosionResistance(this, level, blockPosToExplode, blockState, fluidState);
                            if (optional.isPresent()) { // If it's not just air...
                                momentum -= (optional.get() + 0.3f) * 0.3f; //... decrease the momentum in this direction
                            };

                            // Determine whether the Block should be removed
                            if (momentum > 0.0f && damageCalculator.shouldBlockExplode(this, level, blockPosToExplode, blockState, momentum)) {
                                blocks.add(blockPosToExplode); // If so, add it to the list of Blocks to remove
                            };

                            // Move along the line of the direction vector
                            positionToExplode = positionToExplode.add(direction.scale(0.3f));

                            // Decrease the momentum due to the extra distance moved
                            momentum -= 0.225f;
                        };
                    };
                };
            };
        };

        return new ExplosionResult(blocks, entities);
    };

    /**
     * Deal with each Block State exploded (before it is destroyed).
     * Block drops are added by default in the {@code super} implementation, but they can be {@link SmartExplosion#modifyLoot modified}.
     * There will be no drops if the Block {@link net.minecraftforge.common.extensions.IForgeBlock#canDropFromExplosion forbids it}.
     * @param pos The position of the Block State
     */
    public void explodeBlock(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (level instanceof ServerLevel serverLevel) {
            BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            LootParams.Builder builder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withParameter(LootContextParams.EXPLOSION_RADIUS, radius)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, source)
                .withOptionalParameter(DestroyLootContextParams.SMART_EXPLOSION, this);
            if (getDirectSourceEntity() instanceof Player player) builder.withLuck(player.getLuck());
            modifyLoot(pos, builder);
            addBlockDrops(pos, state.getDrops(builder));
        };
    };

    /**
     * Change the loot that spawns when a Block is destroyed.
     * @param pos The position of the Block State which is to be destroyed
     * @param builder The loot generation builder
     */
    public void modifyLoot(BlockPos pos, LootParams.Builder builder) {};
    
    /**
     * Deal with each Entity exploded.
     * Damage and knockback should be applied here.
     * @param entity The entity affected by this Explosion
     * @param strength How strongly the entity is affected:
     * {@code 0} is not affected at all and {@code 1} is as if they were standing directly next
     * to the center of Explosion with nothing in the way
     */
    public void explodeEntity(Entity entity, float strength) {
        Vec3 knockback = entity.position().subtract(position).normalize().scale(strength * 5f); // Knock back the Entity away from the center of the Explosion
        if (entity instanceof LivingEntity livingEntity) {
            double damageReduction = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, 1d);
            livingEntity.hurt(getDamageSource(), radius * strength * (float)damageReduction * 5f);
            // If the Entity is a Player special client/server syncronisation needs to be done
            if (entity instanceof Player player) {
                if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                   hitPlayers.put(player, knockback);
                };
            };
            knockback = knockback.scale(damageReduction); // Alter the knockback according to the knockback resistance of the Entity
        };
        entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
    };

    /**
     * Create sounds and particle effects. This is called on both server and client side.
     * Default implementation is copied from the {@link net.minecraft.world.level.Explosion#finalizeExplosion Minecraft source code}.
     * @param spawnParticles Whether particles should be shown.
     */
    public void effects(boolean spawnParticles) {
        // Sounds
        if (level.isClientSide()) {
            level.playLocalSound(position.x, position.y, position.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, (1.0f + (random.nextFloat() * 0.4f)) * 0.7f, false);
        };

        // Particles
        if (spawnParticles) {
            if (radius > 2.0f) {
               level.addParticle(ParticleTypes.EXPLOSION_EMITTER, position.x, position.y, position.z, 1d, 0d, 0d);
            } else {
               level.addParticle(ParticleTypes.EXPLOSION, position.x, position.y, position.z, 1d, 0d, 0d);
            };
        };
    };

    /**
     * Whether this Explosion creates 'obliteration' drops.
    */
    public boolean shouldDoSpecialDrops() {
        return false;
    };

    /**
     * Whether this Explosion should cause experience to spawn even if not caused by a Player.
     * Specifically this passes {@code true} to {@link net.minecraft.world.level.block.Block#spawnAfterBreak this method}.
     */
    public boolean shouldAlwaysDropExperience() {
        return false;
    };

    public float getRadius() {
        return radius;
    };

    private void addBlockDrops(BlockPos pos, List<ItemStack> stacks) {
        stacksToCreate.merge(pos, stacks, (existingStacks, newStacks) -> {
            existingStacks.addAll(newStacks);
            return existingStacks;
        });
    };

    /**
     * @param blocksToDestroy The Blocks which this Explosion should remove
     * @param entities The Entities this Explosion should affect mapped to how strongly they are affected:
     * {@code 0} is not affected at all and {@code 1} is as if they were standing directly next
     * to the center of Explosion with nothing in the way
     */
    public static record ExplosionResult(Collection<BlockPos> blocksToDestroy, Map<Entity, Float> entities) {
        public ExplosionResult {
            Objects.requireNonNullElse(blocksToDestroy, List.of());
            Objects.requireNonNullElse(entities, Map.of());
        };
    };
};
