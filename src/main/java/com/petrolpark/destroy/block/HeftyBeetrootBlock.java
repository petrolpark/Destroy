package com.petrolpark.destroy.block;

import java.util.function.Supplier;

import com.petrolpark.destroy.block.shape.DestroyShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeftyBeetrootBlock extends FullyGrownCropBlock {

    public HeftyBeetrootBlock(Properties properties, Supplier<Item> seed) {
        super(properties, seed);
    };

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyShapes.HEFTY_BEETROOT;
    };
    
};
