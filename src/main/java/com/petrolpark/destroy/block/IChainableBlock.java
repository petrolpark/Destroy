package com.petrolpark.destroy.block;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface IChainableBlock extends ICogWheel {

    static final Vec3 CENTER_OFFSET = new Vec3(0.5d, 0.5d, 0.5d);
    
    public static boolean isStateChainable(BlockState state) {
        return (state.getBlock() instanceof IChainableBlock chainable && chainable.isChainable(state)) || AllBlocks.COGWHEEL.has(state) || AllBlocks.LARGE_COGWHEEL.has(state);
    };

    public static float getPropagatedSpeed(BlockState state) {
        if (state.getBlock() instanceof IChainableBlock chainable) return chainable.chainSpeedMultiplier(state);
        if (ICogWheel.isLargeCog(state)) return 0.5f;
        return 1f;
    };

    public static Vec3 getRelativeCenterOfRotation(BlockState state) {
        if (state.getBlock() instanceof IChainableBlock chainable) return chainable.getCenterOfRotation(state);
        return CENTER_OFFSET;
    };

    public static float getRadius(BlockState state) {
        if (state.getBlock() instanceof IChainableBlock chainable) return chainable.getChainRadius(state);
        if (ICogWheel.isLargeCog(state)) return 15 / 16f;
        return 9 / 16f;
    };

    default boolean isChainable(BlockState state) {
        return true;
    };
    
    /**
     * How many times faster than a regular Cogwheel this Block should spin if connected to a regular Cogwheel.
     * @param state
     * @return
     */
    default float chainSpeedMultiplier(BlockState state) {
        return isLargeCog() ? 0.5f : 1f;
    };

    /**
     * Get the center of the axis around which this Block rotates, relative to the lower corner of the Block Pos
     * (i.e. the center of the block is {@code (0.5, 0.5, 0.5)}).
     */
    default Vec3 getCenterOfRotation(BlockState state) {
        return CENTER_OFFSET;
    };

    default float getChainRadius(BlockState state) {
        if (isLargeCog()) return 15 / 16f;
        return 9 / 16f;
    };
};
