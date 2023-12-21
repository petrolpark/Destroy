package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.DirectionalRotatedPillarKineticBlock;
import com.petrolpark.destroy.mixin.accessor.RotationPropagatorAccessor;
import com.petrolpark.destroy.util.KineticsHelper;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DifferentialBlockEntity extends SplitShaftBlockEntity {

    public DifferentialBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (connectedViaAxes || LongShaftBlockEntity.connectedToLongShaft(this, target, diff)) {
            return ratio(stateFrom)
            * Math.signum(RotationPropagatorAccessor.invokeGetAxisModifier(target, KineticsHelper.directionBetween(target.getBlockPos(), getBlockPos())))
            ;
        };
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
	};
    

    @Override
    @SuppressWarnings("null")
    public void removeSource() {
        super.removeSource();
        if (hasLevel()) getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().cycle(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION));
    };

    @SuppressWarnings("null")
    public float ratio(BlockState stateFrom) {
        Direction towardsInput = DirectionalRotatedPillarKineticBlock.getDirection(stateFrom);
        Direction towardsControl = towardsInput.getOpposite();
        BlockPos inputPos = getBlockPos().relative(towardsInput);
        BlockPos controlPos = getBlockPos().relative(towardsControl);

        BlockEntity inputBE = getLevel().getBlockEntity(inputPos);
        float inputSpeed = 0f;
        if (propagatesToMe(inputPos, towardsControl) && inputBE instanceof KineticBlockEntity inputKBE) inputSpeed = getPropagatedSpeed(inputKBE);

        BlockEntity controlBE = getLevel().getBlockEntity(controlPos);
        float controlSpeed = 0f;
        if (propagatesToMe(controlPos, towardsInput) && controlBE instanceof KineticBlockEntity controlKBE) controlSpeed = getPropagatedSpeed(controlKBE);

        if (inputSpeed + controlSpeed == 0f) return 0f;
        return 2f * inputSpeed / (inputSpeed + controlSpeed);
    };

    @SuppressWarnings("null")
    public boolean propagatesToMe(BlockPos pos, Direction directionToMe) {
        if (!hasLevel()) return false;
        BlockState state = getLevel().getBlockState(pos);
        return state.getBlock() instanceof KineticBlock kineticBlock && kineticBlock.hasShaftTowards(getLevel(), pos, state, directionToMe);
    };

    public float getPropagatedSpeed(KineticBlockEntity from) {
        if (from instanceof DifferentialBlockEntity) return 0f;
        return from.getSpeed();
    };

    @Override
	public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
	    super.addPropagationLocations(block, state, neighbours);
		KineticsHelper.addLargeCogwheelPropagationLocations(worldPosition, neighbours);
		return neighbours;
	};

    @Override
    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
		return true;
	};

    @Override
    public float getRotationSpeedModifier(Direction face) {
        return ratio(getBlockState());
    };
    
};
