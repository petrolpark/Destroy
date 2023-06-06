package com.petrolpark.destroy.block.entity.behaviour;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

public interface SmartValueSettingsBehaviour extends ValueSettingsBehaviour {
    /**
     * Allow this Value Box behaviour to accept additional information when its value is set.
     * @param interactionHand The hand used to set the value
     * @param face The face of the Block on which the value was set
     */
    default void acceptAccessInformation(InteractionHand interactionHand, Direction face) {};
};
