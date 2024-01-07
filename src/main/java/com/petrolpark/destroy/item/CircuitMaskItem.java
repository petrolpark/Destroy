package com.petrolpark.destroy.item;

import java.util.function.Consumer;

import com.petrolpark.destroy.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.destroy.item.renderer.CircuitMaskItemRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class CircuitMaskItem extends CircuitPatternItem {

    public CircuitMaskItem(Properties properties) {
        super(properties);
    };

    @Override
    public void launch(DirectionalTransportedItemStack stack, Direction launchDirection) {
        // If it is 'flipped', the Item has been rotated around 180 the north-south axis before being rotated around the up-down axis
        CompoundTag tag = stack.stack.getOrCreateTag();
        boolean alreadyFlipped = tag.contains("Flipped");
        tag.remove("Flipped");

        Rotation rotation = stack.getRotation();
        if (
            launchDirection.getAxis() == Axis.Z && (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_90)
            || launchDirection.getAxis() == Axis.X && (rotation == Rotation.COUNTERCLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180)
        ) {
            stack.rotate(Rotation.CLOCKWISE_180); // Fix the Rotation if certain orientations are flipped over certain axes
        };

        if (!alreadyFlipped) tag.putBoolean("Flipped", true);
        super.launch(stack, launchDirection);
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        stack.getOrCreateTag().remove("Flipped");
        stack.getOrCreateTag().remove("RotationWhileFlying");
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CircuitMaskItemRenderer()));
    };
    
};
