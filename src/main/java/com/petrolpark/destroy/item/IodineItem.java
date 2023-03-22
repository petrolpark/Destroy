package com.petrolpark.destroy.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IodineItem extends Item {
    public IodineItem(Properties pProperties) {
        super(pProperties);
    };

    @Override
    public boolean onEntityItemUpdate(ItemStack itemStack, ItemEntity itemEntity) {

        if (itemEntity.isOnFire() && !itemEntity.getLevel().isClientSide()) {
            EnderDragon dummyDragon = new EnderDragon(null, itemEntity.getLevel()); //create a dummy Ender Dragon

            AreaEffectCloud dragonBreath = new AreaEffectCloud(itemEntity.level, itemEntity.getX(), itemEntity.getY(),itemEntity.getZ());
            dragonBreath.setParticle(ParticleTypes.DRAGON_BREATH);
            dragonBreath.setRadius(1.0F);
            dragonBreath.setDuration(100);
            dragonBreath.setRadiusPerTick((2.0F - dragonBreath.getRadius()) / (float)dragonBreath.getDuration());
            dragonBreath.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            dragonBreath.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            dragonBreath.setOwner(dummyDragon);

            itemEntity.getLevel().addFreshEntity(dragonBreath); //summon Dragon's breath

            itemEntity.kill(); //remove thrown Iodine (otherwise for some reason it executes twice)
            dummyDragon.kill(); //remove the dummy Dragon
            return true;
        };

        return super.onEntityItemUpdate(itemStack, itemEntity);
    };
    
}
