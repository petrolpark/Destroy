package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlacklightBlock extends Block implements IWrenchable {

    public static final DirectionProperty SIDE = BlockStateProperties.FACING;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

    public BlacklightBlock(Properties properties) {
        super(properties);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FLIPPED) ? DestroyShapes.BLACKLIGHT_FLIPPED.get(state.getValue(SIDE)) : DestroyShapes.BLACKLIGHT.get(state.getValue(SIDE));
    };

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 3;
    };

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flipped = context.getClickedFace().getAxis() == Axis.Y && context.getHorizontalDirection().getAxis() == Axis.X;
        return defaultBlockState()
            .setValue(SIDE, context.getClickedFace().getOpposite())
            .setValue(FLIPPED, flipped);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SIDE, FLIPPED);
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(FLIPPED, !state.getValue(FLIPPED)));
        return InteractionResult.SUCCESS;
    };
    
};
