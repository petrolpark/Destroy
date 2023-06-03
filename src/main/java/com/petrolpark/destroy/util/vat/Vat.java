package com.petrolpark.destroy.util.vat;

import java.util.EnumMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Vat {

    /**
     * 
     * @param level
     * @param pos The position of the first space in the Vat
     */
    public static void tryConstruct(Level level, BlockPos pos) {

        boolean successful = true;

        EnumMap<Direction, Integer> dimensions = new EnumMap<>(Direction.class);

        tryExpandInDirection: for (Direction direction : Direction.values()) {

            // Don't expand more 
            if (dimensions.get(direction) > 2) { // TODO make better
                successful = false;
                break tryExpandInDirection;
            };

            while (true) {
                
                BlockPos newPlaneCentre = new BlockPos(pos).relative(direction, dimensions.get(direction));
                AABB aabb = new AABB(newPlaneCentre);
                tryAddNewPlane: for (Direction secondaryDirection : Direction.values()) {
                    if (direction.getAxis() == secondaryDirection.getAxis()) continue tryAddNewPlane;
                    aabb = aabb.expandTowards(new Vec3(secondaryDirection.getStepX(), secondaryDirection.getStepY(), secondaryDirection.getStepZ()).scale(dimensions.get(secondaryDirection)));
                };

                boolean allAir = true;
                boolean allWalls = true;
                for (BlockPos blockPos : BlockPos.betweenClosedStream(aabb).toList()) {
                    BlockState state = level.getBlockState(blockPos);
                    if (state.isAir()) {
                        
                    } else {
                        allAir = false;
                        if (!VatMaterial.isValid(state.getBlock())) allWalls = false;
                    }
                };

                // If we have reached a boundary where everything is a wall
                if (allWalls) {
                    continue tryExpandInDirection;
                } else if (!allAir) {
                    successful = false;
                    break tryExpandInDirection;
                };

                // Increment the value in that direction
                dimensions.merge(direction, 1, (i1, i2) -> i1++);
            }
        };
    };
};
