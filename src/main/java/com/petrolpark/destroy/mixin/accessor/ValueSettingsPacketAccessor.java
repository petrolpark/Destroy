package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

@Mixin(ValueSettingsPacket.class)
public interface ValueSettingsPacketAccessor {
    
    @Accessor("row")
    int getRow();

    @Accessor("value")
	int getValue();

    @Accessor("interactHand")
	InteractionHand getInteractHand();

    @Accessor("side")
	Direction getSide();

    @Accessor("ctrlDown")
	boolean getCtrlDown();
};
