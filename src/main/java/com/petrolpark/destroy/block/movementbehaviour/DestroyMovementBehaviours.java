package com.petrolpark.destroy.block.movementbehaviour;

import com.simibubi.create.AllMovementBehaviours;

import net.minecraft.world.level.block.Blocks;

public class DestroyMovementBehaviours {
  
    public static void register() {
        AllMovementBehaviours.registerBehaviour(Blocks.CLAY, new ExtrudableMovementBehaviour());
    };
};
