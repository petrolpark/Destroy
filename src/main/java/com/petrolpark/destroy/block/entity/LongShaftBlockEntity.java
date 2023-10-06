package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.CoaxialGearBlock;
import com.petrolpark.destroy.block.LongShaftBlock;
import com.petrolpark.destroy.mixin.accessor.RotationPropagatorAccessor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LongShaftBlockEntity extends BracketedKineticBlockEntity {

    public LongShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    @SuppressWarnings("null") // It thinks getLevel() might be null (it's not)
    public void tick() {
        super.tick();
        if (isVirtual() || !hasLevel()) return;
        BlockState coaxialGearState = getLevel().getBlockState(getBlockPos().relative(LongShaftBlockEntity.getDirection(getBlockState())));
        if (!CoaxialGearBlock.isCoaxialGear(coaxialGearState) || coaxialGearState.getValue(RotatedPillarKineticBlock.AXIS) != getBlockState().getValue(RotatedPillarKineticBlock.AXIS) || !coaxialGearState.getValue(CoaxialGearBlock.HAS_SHAFT)) {
            getLevel().setBlockAndUpdate(getBlockPos(), AllBlocks.SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, getBlockState().getValue(RotatedPillarKineticBlock.AXIS)));
        };
    };

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        Direction direction = getDirection(stateFrom);
        if (connectedToLongShaft(target, this, BlockPos.ZERO.subtract(diff))) return 1 / RotationPropagatorAccessor.invokeGetAxisModifier(target, direction.getOpposite());
        return 0f;
	};

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        super.addPropagationLocations(block, state, neighbours);
        neighbours.add(getBlockPos().relative(getDirection(state), 2));
        return neighbours;
	};

    @Override
    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
		if (otherState.getBlock() instanceof IRotate defTo) {
            Direction direction = getDirection(state);
            return (BlockPos.ZERO.relative(direction, 2).equals(other.getBlockPos().subtract(getBlockPos())) && defTo.hasShaftTowards(getLevel(), other.getBlockPos(), otherState, direction.getOpposite()));
        };
        return false;
	};

    public static Direction getDirection(BlockState state) {
        return Direction.get(state.getValue(LongShaftBlock.POSITIVE_AXIS_DIRECTION) ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, state.getValue(RotatedPillarKineticBlock.AXIS));
    };

    /**
     * Returns {@code true} if the {@code KineticBlockEntity} at the relative location is a {@code LongShaftBlockEntity} that is pointing in the right direction.
     * @param be
     * @param potentialLongShaft
     * @param diff {@code potentialLongShaft}'s position - {@code be}'s position
     */
    public static boolean connectedToLongShaft(KineticBlockEntity be, KineticBlockEntity potentialLongShaft, BlockPos diff) {
        if (potentialLongShaft instanceof LongShaftBlockEntity longShaft && be.getBlockState().getBlock() instanceof IRotate defFrom) {
            Direction direction = getDirection(longShaft.getBlockState());
            return diff.equals(BlockPos.ZERO.relative(direction.getOpposite(), 2)) // If the Long Shaft is in the right orientation and position
            && defFrom.hasShaftTowards(be.getLevel(), be.getBlockPos(), be.getBlockState(), direction.getOpposite()); // If this has a Shaft in the right direction
        };
        return false;
    };
    
};
