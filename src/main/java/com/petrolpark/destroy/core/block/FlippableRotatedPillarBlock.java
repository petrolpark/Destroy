package com.petrolpark.destroy.core.block;

import com.simibubi.create.api.contraption.transformable.TransformableBlock;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class FlippableRotatedPillarBlock extends RotatedPillarBlock implements TransformableBlock, IWrenchable {

    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped"); // For Y, false = X and true = Z. For X and Z false = Y
    
    public FlippableRotatedPillarBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLIPPED);
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        context.getLevel().setBlockAndUpdate(context.getClickedPos(), transform(state, new StructureTransform(BlockPos.ZERO, context.getClickedFace().getAxis(), Rotation.CLOCKWISE_90, Mirror.NONE)));
        return InteractionResult.SUCCESS;
    };

    /*
     *  Axis Rot Flip it?
     *  X    Y   false
     *  X    Z   true
     *  Y    X   false
     *  Y    Z   true
     *  Z    X   false
     *  Z    Y   false
     */
    @Override
    public BlockState transform(BlockState state, StructureTransform transform) {
        if (transform.rotation != Rotation.CLOCKWISE_90 && transform.rotation != Rotation.COUNTERCLOCKWISE_90) return state;
        if (transform.rotationAxis == state.getValue(AXIS)) return state.cycle(FLIPPED); 
        BlockState newState = state.setValue(AXIS, Direction.get(AxisDirection.POSITIVE, state.getValue(AXIS)).getClockWise(transform.rotationAxis).getAxis());
        return transform.rotationAxis == Axis.Z ? newState.cycle(FLIPPED) : newState;
    };
};
