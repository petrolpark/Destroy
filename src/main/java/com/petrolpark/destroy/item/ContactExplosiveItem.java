package com.petrolpark.destroy.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;

public class ContactExplosiveItem extends Item {

    public ContactExplosiveItem(Properties properties) {
        super(properties);
    };

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.isOnGround()) {
            entity.getLevel().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 1, Explosion.BlockInteraction.NONE);
            entity.kill();
            return true;
        };
        return super.onEntityItemUpdate(stack, entity);
    };
};
