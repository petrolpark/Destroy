package com.petrolpark.destroy.item.directional;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

public class DirectionalTransportedItemStack extends TransportedItemStack {

    @Nullable
    public Rotation rotation; // Rotation from North

    public DirectionalTransportedItemStack(ItemStack stack) {
        super(stack);
        rotation = Rotation.NONE;
        refreshAngle();
    };

    @Override
    public float getTargetSideOffset() {
        return 0f;
    };

    public void rotate(Rotation appliedRotation) {
        rotation = appliedRotation.getRotated(rotation);
        refreshAngle();
    };

    public void refreshAngle() {
        if (rotation != null) angle = getTargetAngle();
    };

    @SuppressWarnings("null")
    public int getTargetAngle() {
        if (rotation == null) return 0;
        switch (rotation) {
            case NONE: return 0;
            case CLOCKWISE_90: return 270;
            case CLOCKWISE_180: return 180;
            case COUNTERCLOCKWISE_90: return 90;
            default: return 0;
        }
    };

    @Override
    public TransportedItemStack getSimilar() {
		return copy(this);
	};

    @Override
	public TransportedItemStack copy() {
		return copy(this);
	};

    public static DirectionalTransportedItemStack copy(TransportedItemStack stack) {
        DirectionalTransportedItemStack copy = new DirectionalTransportedItemStack(stack.stack.copy());
        // Copied from Create
		copy.beltPosition = stack.beltPosition;
		copy.insertedAt = stack.insertedAt;
		copy.insertedFrom = stack.insertedFrom;
		copy.prevBeltPosition = stack.prevBeltPosition;
		copy.prevSideOffset = stack.prevSideOffset;
		copy.processedBy = stack.processedBy;
		copy.processingTime = stack.processingTime;
        //
        if (stack instanceof DirectionalTransportedItemStack directionalStack) {
            copy.rotation = directionalStack.rotation;
            copy.refreshAngle();
        };
        return copy;
    };

    @Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = super.serializeNBT();
        if (rotation != null) nbt.putInt("Rotation", rotation.ordinal());
        return nbt;
	};
    
};
