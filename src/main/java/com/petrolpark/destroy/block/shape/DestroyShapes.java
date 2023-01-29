package com.petrolpark.destroy.block.shape;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllShapes.Builder;
import com.simibubi.create.foundation.utility.VoxelShaper;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DestroyShapes {

    public static final VoxelShape CENTRIFUGE = shape(0, 0, 0, 16, 4, 16)
        .add(2, 4, 2, 14, 12, 14)
        .add(0, 12, 0, 16, 16, 16)
        .build();

    public static final VoxelShaper AGING_BARREL_OPEN = shape(0, 0, 0, 16, 14, 16)
        .erase(2, 2, 2, 14, 14, 14)
        .add(0, 14, 14, 16, 30, 16)
        .forDirectional(Direction.NORTH);

    public static final VoxelShaper AGING_BARREL_OPEN_RAYTRACE = shape(0, 0, 0, 16, 14, 16)
        .add(0, 14, 14, 16, 30, 16)
        .forDirectional(Direction.NORTH);
    
    public static final VoxelShape AGING_BARREL_INTERIOR = shape(0, 0, 0, 16, 16, 16) //used for detecting when Items are thrown into the Aging Barrel
        .erase(2, 7, 2, 14, 16, 14)
        .build();

    /**
     * Changes the voxel shape of the Aging Barrel based on how far through the aging process the Barrel is.
    */
    public static final VoxelShape agingBarrelClosed(int progress) {
        Builder agingBarrel = shape(0,0,0,16,16,16);
        if (progress == 0) {
            agingBarrel.add(7, 16, 7, 9, 18, 8);
        } else if (progress == 1) {
            agingBarrel.add(6, 16, 6, 10, 20, 10);
        } else if (progress == 2) {
            agingBarrel.add(5, 16, 5, 11, 22, 11);
        } else if (progress == 3) {
            agingBarrel.add(4, 16, 4, 12, 24, 12);
        } else if (progress == 4) {
            agingBarrel.add(3, 16, 3, 13, 26, 13);
        };
        return agingBarrel.build();
    };

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    };

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    };

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    };
}
