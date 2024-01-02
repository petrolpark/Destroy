package com.petrolpark.destroy.util;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class KineticsHelper {
    
	public static void addLargeCogwheelPropagationLocations(BlockPos pos, List<BlockPos> neighbours) {
		BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.distSqr(BlockPos.ZERO) == 2)
					neighbours.add(pos.offset(offset));
			});
	};

    public static Direction directionBetween(BlockPos posFrom, BlockPos posTo) {
        for (Direction direction : Direction.values()) {
            if (posFrom.relative(direction).equals(posTo)) return direction;
        };
        return null;
    };
};
