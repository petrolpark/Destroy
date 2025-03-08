package com.petrolpark.destroy.mixin.accessor;

import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

@Mixin(value = ValueSettingsPacket.class, remap = false)
public interface ValueSettingsPacketAccessor {
    
    @Accessor(
        value = "row",
        remap = false
    )
    int getRow();

    @Accessor(
        value = "value",
        remap = false
    )
	int getValue();

    @Accessor(
        value = "interactHand",
        remap = false
    )
	InteractionHand getInteractHand();

    @Accessor(
        value = "side",
        remap = false
    )
	Direction getSide();

    @Accessor(
        value = "ctrlDown",
        remap = false
    )
	boolean getCtrlDown();


    @Accessor(
            value = "hitResult",
            remap = false
    )
    BlockHitResult getHitResult();
};
