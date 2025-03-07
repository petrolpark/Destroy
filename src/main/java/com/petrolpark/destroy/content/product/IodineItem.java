package com.petrolpark.destroy.content.product;

import com.petrolpark.destroy.config.DestroySubstancesConfigs;
import com.petrolpark.destroy.core.item.tooltip.IDynamicItemDescription;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Fun fact I'm pretty sure this is the first ever thing I coded
 */
public class IodineItem extends Item implements IDynamicItemDescription {
    public IodineItem(Properties pProperties) {
        super(pProperties);
    };

    @Override
    public boolean onEntityItemUpdate(ItemStack itemStack, ItemEntity itemEntity) {

        if (DestroySubstancesConfigs.iodineDragonsBreath() && itemEntity.isOnFire() && !itemEntity.level().isClientSide()) {
            EnderDragon dummyDragon = new EnderDragon(null, itemEntity.level()); //create a dummy Ender Dragon

            AreaEffectCloud dragonBreath = new AreaEffectCloud(itemEntity.level(), itemEntity.getX(), itemEntity.getY(),itemEntity.getZ());
            dragonBreath.setParticle(ParticleTypes.DRAGON_BREATH);
            dragonBreath.setRadius(1.0F);
            dragonBreath.setDuration(100);
            dragonBreath.setRadiusPerTick((2.0F - dragonBreath.getRadius()) / (float)dragonBreath.getDuration());
            dragonBreath.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            dragonBreath.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            dragonBreath.setOwner(dummyDragon);

            itemEntity.level().addFreshEntity(dragonBreath); //summon Dragon's breath

            itemEntity.kill(); //remove thrown Iodine (otherwise for some reason it executes twice)
            dummyDragon.kill(); //remove the dummy Dragon
            return true;
        };

        return super.onEntityItemUpdate(itemStack, itemEntity);
    };

    @Override
    public ItemDescription getItemDescription() {
        return new ItemDescription.Builder(getPalette())
            .addSummary(Component.translatable("item.destroy.iodine.dynamic_tooltip.summary").getString())
            .addBehaviour(Component.translatable("item.destroy.iodine.dynamic_tooltip.condition").getString(), Component.translatable("item.destroy.iodine.dynamic_tooltip.behaviour").getString())
            .build();
    }

    @Override
    public Palette getPalette() {
        return Palette.STANDARD_CREATE;
    };
    
};
