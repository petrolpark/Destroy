package com.petrolpark.destroy.item.potatoCannonProjectileType;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonProjectileType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class DestroyPotatoCannonProjectileTypes {

    public static final PotatoCannonProjectileType

    BIFURICATED_CARROT = create("bifuricated_carrot")
        .damage(7)
        .reloadTicks(13)
        .velocity(1.4f)
        .knockback(0.3f)
        .renderTowardMotion(140, 1)
        .soundPitch(1.4f)
        .registerAndAssign(DestroyItems.BIFURICATED_CARROT.get()),

    BOMB_BON = create("bomb_bon")
        .damage(10)
        .reloadTicks(100)
        .velocity(0.8f)
        .knockback(0.5f)
        .renderTumbling()
        .soundPitch(0.95f)
        .onEntityHit(explodeEntity(2, false, Level.ExplosionInteraction.TNT))
        .onBlockHit(explodeBlock(2, false, Level.ExplosionInteraction.TNT))
        .registerAndAssign(DestroyItems.BOMB_BON.get()),

    BUTTER = create("butter")
        .damage(10)
        .reloadTicks(60)
        .velocity(0.8f)
        .knockback(1.5f)
        .renderTumbling()
        .soundPitch(0.9f)
        .sticky()
        .onEntityHit(setEffect(MobEffects.MOVEMENT_SLOWDOWN, 2, 200, false))
        .registerAndAssign(DestroyItems.BUTTER.get()),

    COAL_INFUSED_BEETROOT = create("coal_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.BLINDNESS, 0, 100, false))
        .registerAndAssign(DestroyItems.COAL_INFUSED_BEETROOT.get()),

    COPPER_INFUSED_BEETROOT = create("copper_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.MOVEMENT_SPEED, 0, 100, false))
        .registerAndAssign(DestroyItems.COPPER_INFUSED_BEETROOT.get()),

    DIAMOND_INFUSED_BEETROOT = create("coal_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.POISON, 0, 100, false))
        .registerAndAssign(DestroyItems.DIAMOND_INFUSED_BEETROOT.get()),

    EMERALD_INFUSED_BEETROOT = create("emerald_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.JUMP, 0, 100, false))
        .registerAndAssign(DestroyItems.EMERALD_INFUSED_BEETROOT.get()),

    FLUORITE_INFUSED_BEETROOT = create("fluorite_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.WEAKNESS, 0, 100, false))
        .registerAndAssign(DestroyItems.FLUORITE_INFUSED_BEETROOT.get()),

    FRIES = create("fries")
        .damage(5)
        .reloadTicks(13)
        .velocity(1.1f)
        .renderTumbling()
        .soundPitch(1.3f)
        .registerAndAssign(DestroyItems.FRIES.get()),

    GOLD_INFUSED_BEETROOT = create("gold_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.REGENERATION, 0, 100, false))
        .registerAndAssign(DestroyItems.COAL_INFUSED_BEETROOT.get()),

    HEFTY_BEETROOT = create("hefty_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .registerAndAssign(DestroyItems.HEFTY_BEETROOT.get()),

    IRON_INFUSED_BEETROOT = create("iron_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.DAMAGE_RESISTANCE, 0, 100, false))
        .registerAndAssign(DestroyItems.IRON_INFUSED_BEETROOT.get()),

    LAPIS_INFUSED_BEETROOT = create("lapis_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.GLOWING, 0, 100, false))
        .registerAndAssign(DestroyItems.LAPIS_INFUSED_BEETROOT.get()),

    MASHED_POTATO_BLOCK = create("mashed_potato_block")
        .damage(4)
        .reloadTicks(5)
        .velocity(0.8f)
        .renderTumbling()
        .sticky()
        .registerAndAssign(DestroyBlocks.MASHED_POTATO_BLOCK.get()),

    MASHED_POTATO = create("mashed_potato")
        .damage(1)
        .reloadTicks(3)
        .velocity(0.8f)
        .renderTumbling()
        .sticky()
        .registerAndAssign(DestroyItems.MASHED_POTATO.get()),

    NAPALM_SUNDAE = create("napalm_sundae")
        .damage(7)
        .reloadTicks(20)
        .velocity(0.9f)
        .renderTumbling()
        .preEntityHit(setFire(5))
        .registerAndAssign(DestroyItems.NAPALM_SUNDAE.get()),

    NICKEL_INFUSED_BEETROOT = create("nickel_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.HUNGER, 0, 100, false))
        .registerAndAssign(DestroyItems.NICKEL_INFUSED_BEETROOT.get()),

    POTATE_O = create("potate_o")
        .damage(7)
        .reloadTicks(20)
        .knockback(1.7f)
        .renderTumbling()
        .registerAndAssign(DestroyItems.POTATE_O.get()),

    RAW_FRIES = create("raw_fries")
        .damage(1)
        .reloadTicks(10)
        .velocity(1.1f)
        .renderTumbling()
        .soundPitch(1.5f)
        .registerAndAssign(DestroyItems.RAW_FRIES.get()),

    REDSTONE_INFUSED_BEETROOT = create("redstone_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.HARM, 0, 100, false))
        .registerAndAssign(DestroyItems.REDSTONE_INFUSED_BEETROOT.get()),

    THERMITE_BROWNIE = create("thermite_brownie")
        .damage(6)
        .reloadTicks(15)
        .velocity(1.0f)
        .renderTumbling()
        .preEntityHit(setFire(7))
        .registerAndAssign(DestroyItems.THERMITE_BROWNIE.get()),

    UNSEASONED_FRIES = create("unseasoned_fries")
        .damage(3)
        .reloadTicks(12)
        .velocity(1.1f)
        .renderTumbling()
        .soundPitch(1.3f)
        .registerAndAssign(DestroyItems.UNSEASONED_FRIES.get()),

    ZINC_INFUSED_BEETROOT = create("zinc_infused_beetroot")
        .damage(5)
        .reloadTicks(20)
        .velocity(0.9f)
        .knockback(1.5f)
        .renderTowardMotion(140, 1)
        .onEntityHit(setEffect(MobEffects.DAMAGE_BOOST, 0, 100, false))
        .registerAndAssign(DestroyItems.ZINC_INFUSED_BEETROOT.get())
    ;
    
    private static PotatoCannonProjectileType.Builder create(String name) {
        return new PotatoCannonProjectileType.Builder(Destroy.asResource(name));
    };

    private static Predicate<EntityHitResult> setEffect(MobEffect effect, int amplifier, int ticks, boolean recoverable) {
		return ray -> {
			Entity entity = ray.getEntity();
			if (entity.level().isClientSide())
				return true;
			if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (effect.isInstantenous()) {
			        effect.applyInstantenousEffect(null, null, livingEntity, amplifier, 1.0);
                } else {
                    livingEntity.addEffect(new MobEffectInstance(effect, ticks, amplifier));
                };
            };
            return !recoverable;
		};
	};

    private static Predicate<EntityHitResult> setFire(int seconds) {
        return ray -> {
            ray.getEntity().setSecondsOnFire(seconds);
            return false;
        };
    };

    private static BiPredicate<LevelAccessor, BlockHitResult> explodeBlock(float radius, boolean causesFire, Level.ExplosionInteraction mode) {
        return (world, ray) -> {
            if (world.isClientSide()) return true;
            BlockPos pos = ray.getBlockPos();
            if (world instanceof Level l && !l.isLoaded(pos)) return true;
            Level level = (Level) world;
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, causesFire, mode);
            return false;
        };
    };

    private static Predicate<EntityHitResult> explodeEntity(float radius, boolean causesFire, Level.ExplosionInteraction mode) {
        return ray -> {
            Entity entity = ray.getEntity();
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), radius, causesFire, mode);
            return false;
        };
    };

    public static void register() {};
}
