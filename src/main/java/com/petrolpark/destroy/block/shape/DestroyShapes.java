package com.petrolpark.destroy.block.shape;

import com.simibubi.create.AllShapes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DestroyShapes {

    public static final VoxelShape CENTRIFUGE = shape(0, 0, 0, 16, 4, 16).add(2, 4, 2, 14, 12, 14)
			.add(0, 12, 0, 16, 16, 16)
			.build();

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
