package com.petrolpark.destroy.item;

import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class ContactExplosiveItem extends Item {

    public ContactExplosiveItem(Properties properties) {
        super(properties);
        DispenserBlock.registerBehavior(this, new ContactExplosiveDispenseBehaviour());
    };

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.onGround()) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 0.5f, Level.ExplosionInteraction.NONE);
            entity.kill();
            return true;
        };
        return super.onEntityItemUpdate(stack, entity);
    };

    public static class ContactExplosiveDispenseBehaviour extends OptionalDispenseItemBehavior {
        
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Vec3 position = Vec3.atCenterOf(source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING)));
            source.getLevel().explode(null, position.x, position.y, position.z, 0.5f, Level.ExplosionInteraction.NONE);
            setSuccess(true);
            stack.shrink(1);
            return stack;
        };
    };
};
