package com.petrolpark.destroy.util.vat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Interface for Blocks which can supply UV light to a {@link Vat}.
 */
public interface IUVLampBlock {
    
    /**
     * Get the power (in watts) of ultraviolet light the given Block State supplies to a {@link Vat}.
     * @param level
     * @param blockState
     * @param blockPos
     * @param face The face of the Block State touching the Vat
     * @return
     */
    float getUVPower(Level level, BlockState blockState, BlockPos blockPos, Direction face);

    public static float getUVPower(Level level, BlockPos blockPos, Direction face) {
        BlockState state = level.getBlockState(blockPos);

        if (state.isAir()) return 0f;

        // IVatHeaters
        if (state.getBlock() instanceof IUVLampBlock heater) {
            return heater.getUVPower(level, state, blockPos, face);
        };

        return 0f;
    };
};
