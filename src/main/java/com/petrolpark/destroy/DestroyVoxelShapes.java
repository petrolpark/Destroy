package com.petrolpark.destroy;

import com.petrolpark.destroy.content.oil.pumpjack.IPumpjackStructuralBlock;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllShapes.Builder;

import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DestroyVoxelShapes {

    public static final VoxelShape

    BLOCK = shape(0, 0, 0, 16, 16, 16)
        .build(),
    
    CENTRIFUGE = shape(0, 0, 0, 16, 4, 16)
        .add(2, 4, 2, 14, 12, 14)
        .add(0, 12, 0, 16, 16, 16)
        .build(),

    CROP = shape(0, 0, 0, 16, 8, 16)
        .build(),

    HEFTY_BEETROOT = shape(4, -1, 4, 12, 5, 12)
        .build(),

    MAGIC_BEETROOT_SEEDS = shape(0, 0, 0, 16, 2, 16)
        .build(),

    AGING_BARREL_INTERIOR = shape(0, 0, 0, 16, 16, 16) // Used for detecting when Items are thrown into the Aging Barrel
        .erase(2, 7, 2, 14, 16, 14)
        .build(),

    DYNAMO = shape(0, 0, 0, 16, 16, 16)
        .erase(0, 2, 2, 16, 14, 14)
        .erase(2, 2, 0, 14, 14, 16)
        .add(0, 6, 0, 16, 10, 16)
        .add(5, 0, 5, 11, 16, 11)
        .build(),

    DYNAMO_ARC_FURNACE = shape(0, 0, 0, 16, 16, 16)
        .erase(0, 0, 2, 16, 14, 14)
        .erase(2, 0, 0, 14, 14, 16)
        .add(0, 6, 0, 16, 10, 16)
        .add(5, 5, 5, 11, 16, 11)
        .build(),

    SAND_CASTLE = shape(1, 0, 1, 15, 14, 15)
        .build(),
        
    COOLER = shape(1, -2, 1, 15, 14, 15)
        .build(),

    REDSTONE_PROGRAMMER = shape(1, 0, 1, 15, 3, 15)
        .add(2, 3, 2, 14, 10, 14)
        .build(),

    TREE_TAP = shape(0, 0, 0, 16, 13, 16)
        .build(),

    SEMI_MOLTEN_BLOCK_COLLISION = shape(0, 0, 0, 16, 14, 16)
        .build(),

    TEST_TUBE_RACK_X = shape(0, 0, 6, 16, 8, 10)
        .build(),

    TEST_TUBE_RACK_Z = shape(6, 0, 0, 10, 8, 16)
        .build(),

    MEASURING_CYLINDER = shape(5, 0, 5, 11, 14, 11)
        .build(),

    BEAKER = shape(4, 0, 4, 12, 8, 12)
        .build(),

    ROUND_BOTTOMED_FLASK = shape(5, 0, 5, 11, 11, 11)
        .build(),

    MECHANICAL_SIEVE = shape(0, 6, 0, 16, 10, 16)
        .build(),

    MECHANICAL_SIEVE_COLLISION = shape(0, 6, 0, 16, 7, 16)
        .build();

    public static final VoxelShaper

    ARC_FURNACE_LID = shape(0, 0, 0, 16, 16, 16)
        .erase(0, 2, 2, 16, 16, 14)
        .erase(2, 2, 0, 14, 16, 16)
        .add(2, 2, 6, 6, 8, 10)
        .add(10, 2, 6, 14, 8, 10)
        .forHorizontalAxis(),

    EXTRUSION_DIE = shape(0, 0, 7, 16, 16, 9)
        .forDirectional(Direction.SOUTH),

    POLLUTOMETER = shape(6, 0, 6, 10, 12, 10)
        .add(5, 3, 3, 11, 9, 6)
        .forDirectional(Direction.NORTH),

    AGING_BARREL_OPEN = shape(0, 0, 0, 16, 14, 16)
        .erase(2, 2, 2, 14, 14, 14)
        .add(0, 14, 14, 16, 30, 16)
        .forDirectional(Direction.NORTH),

    AGING_BARREL_OPEN_RAYTRACE = shape(0, 0, 0, 16, 14, 16)
        .add(0, 14, 14, 16, 30, 16)
        .forDirectional(Direction.NORTH),

    BLACKLIGHT = shape(0, 0, 5, 16, 4, 11)
        .forDirectional(Direction.DOWN),

    BLACKLIGHT_FLIPPED = shape(5, 0, 0, 11, 4, 16)
        .forDirectional(Direction.DOWN),

    CATALYTIC_CONVERER = shape(3, 0, 3, 13, 2, 13)
        .add(2, 2, 2, 14, 10, 14)
        .add(3, 10, 3, 13, 12, 13)
        .add(5, 12, 5, 11, 16, 11)
        .forDirectional(Direction.UP),

    CREATIVE_PUMP = shape(3, 0, 3, 13, 16, 13)
        .forAxis();

    public static final VoxelShaper BLOWPIPE = shape(7, 0, 7, 9, 16, 9).forAxis();
        
    public static final VoxelShaper getPumpJackShaper(IPumpjackStructuralBlock.Component component) {
        switch (component) {
            case FRONT:
                return shape(4, 0, 0, 12, 8, 12)
                    .forDirectional(Direction.NORTH);
            case BACK:
                return shape(3, 0, 0, 13, 16, 16)
                    .add(1, 5, 5, 3, 11, 16)
                    .add(13, 5, 5, 15, 11, 16)
                    .forDirectional(Direction.NORTH);
            case MIDDLE:
                return shape(1, 0, 3, 15, 13, 13)
                    .add(4, 0, 13, 12, 8, 16)
                    .add(1, 5, 0, 3, 11, 3)
                    .add(13, 5, 0, 15, 11, 3)
                    .add(4, 13, 6, 6, 16, 10)
                    .add(10, 13, 6, 12, 16, 10)
                    .forDirectional(Direction.NORTH);
            case TOP:
                return shape(4, 0, 6, 6, 12, 10)
                    .add(10, 0, 6, 12, 12, 10)
                    .forDirectional(Direction.NORTH);
            default:
                return null;
        }
    };

    /**
     * Changes the voxel shape of the Aging Barrel based on how far through the aging process the Barrel is.
    */
    public static final VoxelShape agingBarrelClosed(int progress) {
        Builder agingBarrel = shape(0,0,0,16,16,16);
        if (progress == 0) {
            agingBarrel.add(7, 16, 7, 9, 18, 9);
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

    public static final VoxelShape bubbleCap(boolean bottom, boolean top) {
        Builder bubbleCap = shape(2, 2, 2, 14, 14, 14)
            .add(0, 0, 0, 2, 16, 2)
            .add(0, 0, 14, 2, 16, 16)
            .add(14, 0, 0, 16, 16, 2)
            .add(14, 0, 14, 16, 16, 16);
        if (bottom) {
            bubbleCap.add(0, 0, 0, 16, 2, 16);
        } else {
            bubbleCap.add(3, 0, 3, 13, 2, 13);
        };
        if (top) {
            bubbleCap.add(0, 14, 0, 16, 16, 16);
        } else {
            bubbleCap.add(3, 14, 3, 13, 16, 13);
        };
        return bubbleCap.build();
        
    };
    
    public static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new AllShapes.Builder(Block.box(x1, y1, z1, x2, y2, z2));
    };
}
