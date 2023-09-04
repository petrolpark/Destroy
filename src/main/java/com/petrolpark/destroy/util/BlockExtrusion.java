package com.petrolpark.destroy.util;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.block.movementbehaviour.ExtrudableMovementBehaviour;
import com.simibubi.create.AllMovementBehaviours;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockExtrusion {

    public static Map<Block, BlockExtrusion> EXTRUSIONS = new HashMap<>();

    /**
     * Get the Block State a Block will produce when pushed through an Extrusion Die in the given Direction.
     * @param state The State being extruded
     * @param extrusionDirection
     * @return The corresponding Block State for extruding, or an air Block State if no extrusion is possible
     */
    BlockState getExtruded(BlockState state, Direction extrusionDirection);

    /**
     * Register a Block Extrusion 'recipe'. This must be done after all Blocks are initialized.
     * @param block
     * @param extrusion
     */
    public static void register(Block block, BlockExtrusion extrusion) {
        AllMovementBehaviours.registerBehaviour(block, new ExtrudableMovementBehaviour(extrusion));
        EXTRUSIONS.put(block, extrusion);
    };
    
};
