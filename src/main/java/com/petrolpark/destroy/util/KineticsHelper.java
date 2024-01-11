package com.petrolpark.destroy.util;

import java.util.List;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;

public class KineticsHelper {
    
	public static void addLargeCogwheelPropagationLocations(BlockPos pos, List<BlockPos> neighbours) {
		BlockPos.betweenClosedStream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.distSqr(BlockPos.ZERO) == 2)
					neighbours.add(pos.offset(offset));
			});
	};

    public static Direction directionBetween(BlockPos posFrom, BlockPos posTo) {
        for (Direction direction : Direction.values()) {
            if (posFrom.relative(direction).equals(posTo)) return direction;
        };
        return null;
    };

	public static PoseStack rotateToFace(Direction facing) {
		PoseStack poseStack = new PoseStack();
		TransformStack.cast(poseStack)
				.centre()
				.rotateToFace(facing)
				.multiply(com.mojang.math.Axis.XN.rotationDegrees(-90))
				.unCentre();
		return poseStack;
	};

	public static PoseStack rotateToAxis(Axis axis) {
		Direction facing = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE);
		PoseStack poseStack = new PoseStack();
		TransformStack.cast(poseStack)
				.centre()
				.rotateToFace(facing)
				.multiply(com.mojang.math.Axis.XN.rotationDegrees(-90))
				.unCentre();
		return poseStack;
	};
};
