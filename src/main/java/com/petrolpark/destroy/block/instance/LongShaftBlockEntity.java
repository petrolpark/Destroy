package com.petrolpark.destroy.block.instance;

import java.util.List;

import com.petrolpark.destroy.block.LongShaftBlock;
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
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
		if (!(stateTo.getBlock() instanceof IRotate defTo)) return 0f;
        Direction direction = getDirection(stateFrom);
        if (BlockPos.ZERO.relative(direction, 2) == diff && defTo.hasShaftTowards(getLevel(), target.getBlockPos(), stateTo, direction.getOpposite())) return 1f;
        return 0f;
	};

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
		super.addPropagationLocations(block, state, neighbours);
        neighbours.add(BlockPos.ZERO.relative(getDirection(state), 2));
        return neighbours;
	};

    public static Direction getDirection(BlockState state) {
        return Direction.get(state.getValue(LongShaftBlock.POSITIVE_AXIS_DIRECTION) ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, state.getValue(RotatedPillarKineticBlock.AXIS));
    };
    
};
