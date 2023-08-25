package com.petrolpark.destroy.block.entity;

import com.petrolpark.destroy.block.CoaxialGearBlock;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.LongShaftBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CoaxialGearBlockEntity extends BracketedKineticBlockEntity {

    public CoaxialGearBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        Direction direction = directionBetween(target.getBlockPos(), getBlockPos());
        if (direction != null) {
            if (stateTo.getBlock() instanceof IRotate iRotate && iRotate.hasShaftTowards(getLevel(), target.getBlockPos(), stateTo, direction)) CoaxialGearBlock.updatePropagationOfLongShaft(stateFrom, level, getBlockPos());
        };
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
	};

    public static Direction directionBetween(BlockPos posFrom, BlockPos posTo) {
        for (Direction direction : Direction.values()) {
            if (posFrom.relative(direction).equals(posTo)) return direction;
        };
        return null;
    };

    @Override
    public void tick() {
        super.tick();
        if (!hasLevel()) return;
        if (getBlockState().getValue(CoaxialGearBlock.HAS_SHAFT) && !level.isClientSide()) {
            Axis axis = getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
            boolean longShaftExists = false;
            for (AxisDirection axisDirection : AxisDirection.values()) {
                BlockState longShaftState = level.getBlockState(getBlockPos().relative(Direction.get(axisDirection, axis)));
                if (DestroyBlocks.LONG_SHAFT.has(longShaftState) && longShaftState.getValue(RotatedPillarKineticBlock.AXIS) == axis && longShaftState.getValue(LongShaftBlock.POSITIVE_AXIS_DIRECTION) == (axisDirection != AxisDirection.POSITIVE)) {
                    longShaftExists = true;
                    break;
                };
            };
            if (!longShaftExists) {
                getLevel().setBlockAndUpdate(getBlockPos(), AllBlocks.SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, axis));
                Block.popResource(getLevel(), getBlockPos(), DestroyBlocks.COAXIAL_GEAR.asStack());
            };
        };
    };
    
};
