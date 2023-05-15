package com.petrolpark.destroy.world.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class DelicateExplosion extends SmartExplosion {

    private final ItemStack silkTouch;

    public DelicateExplosion(Level level, Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, Vec3 position, float radius, float smoothness) {
        super(level, source, damageSource, damageCalculator, position, radius, smoothness);
        silkTouch = new ItemStack(Items.NETHERITE_PICKAXE);
        silkTouch.enchant(Enchantments.SILK_TOUCH, 1);
    };

    @Override
    public void explodeEntity(Entity entity, float strength) {
        super.explodeEntity(entity, strength * 0.3f);
    };

    @Override
    public void modifyLoot(BlockPos pos, Builder builder) {
        builder.withParameter(LootContextParams.TOOL, silkTouch);
    };
    
};
