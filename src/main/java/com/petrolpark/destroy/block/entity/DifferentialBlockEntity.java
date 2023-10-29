package com.petrolpark.destroy.block.entity;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
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
            return shaftOutputs(target, diff);
        };
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
	};

    @SuppressWarnings("null")
    public float shaftOutputs(KineticBlockEntity target, BlockPos diff) {
        Axis axis = getBlockState().getValue(CogWheelBlock.AXIS);
        BlockPos front = getBlockPos().relative(Direction.get(AxisDirection.POSITIVE, axis));
        BlockPos back = getBlockPos().relative(Direction.get(AxisDirection.NEGATIVE, axis));

        if (!hasSource()) return 2f;

        BlockEntity sourceBlockEntity1 = getLevel().getBlockEntity(source);
        if (sourceBlockEntity1 == null || !(sourceBlockEntity1 instanceof KineticBlockEntity kbe1))
        return 2f;

        List<KineticBlockEntity> sources = new ArrayList<>(2);
        sources.add(kbe1);
        sources.add(null);

        for (BlockPos pos : List.of(front, back)) { // Search for the second source
            if (pos.equals(source)) continue;
            BlockEntity blockEntity = getLevel().getBlockEntity(pos);
            if (blockEntity != null && blockEntity instanceof KineticBlockEntity kbe) {
                if ((kbe.hasSource() && !kbe.source.equals(getBlockPos())) || kbe.isSource()) { // If the kinetic Block entity has a source which isn't this Differential
                    sources.set(1, kbe);
                } else {
                    Destroy.LOGGER.info("theres no source here "+getLevel().getBlockState(pos).getBlock().getDescriptionId());
                    Destroy.LOGGER.info("its source is at "+kbe.source);
                };
            };
        };

        if (sources.get(1) == null) { // If there is only one source
            Destroy.LOGGER.info("only one source");
            if (sources.get(0).getBlockPos().equals(front) || sources.get(0).getBlockPos().equals(back)) { // If the source is a shaft
                Destroy.LOGGER.info(":()");
                if (target.equals(sources.get(0))) return 2f;
                return 0f;
            } else { // If the source is the big cog
                Destroy.LOGGER.info("Im powered by a big cog");
            };
        } else if ((sources.get(0).getBlockPos().equals(front) && sources.get(1).getBlockPos().equals(back)) || (sources.get(0).getBlockPos().equals(back) && sources.get(1).getBlockPos().equals(front))) { // If powered by two shafts
            float actualSpeed = (sources.get(0).getSpeed() + sources.get(1).getSpeed()) / 2f;
            Destroy.LOGGER.info("Setting speed to "+(target.getSpeed() / actualSpeed));
            if (actualSpeed == 0f)
                return 0f;
            return target.getSpeed() / actualSpeed;
        };

        return 0f;
    };

    @Override
	public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
	    super.addPropagationLocations(block, state, neighbours);
		BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.distSqr(BlockPos.ZERO) == 2)
					neighbours.add(worldPosition.offset(offset));
			});
		return neighbours;
	};

    @Override
    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
		return true;
	};

    @Override
    public float getRotationSpeedModifier(Direction face) {
        BlockPos diff = BlockPos.ZERO.relative(face);
        BlockEntity blockEntity = getLevel().getBlockEntity(getBlockPos().relative(face));
        if (blockEntity == null || !(blockEntity instanceof KineticBlockEntity kbe)) return 0f;
        return shaftOutputs(kbe, diff);
    };
    
};
