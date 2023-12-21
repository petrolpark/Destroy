package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.DifferentialBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.petrolpark.destroy.block.DirectionalRotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DifferentialRenderer extends KineticBlockEntityRenderer<DifferentialBlockEntity> {

    public DifferentialRenderer(Context context) {
        super(context);
    };

    @Override
    @SuppressWarnings("null") // It thinks getLevel() might be null
    protected void renderSafe(DifferentialBlockEntity differential, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //if (Backend.canUseInstancing(planetaryGearsetBlockEntity.getLevel())) return;
        if (!differential.hasLevel()) return;

		BlockState state = getRenderedBlockState(differential);
        Direction face = DirectionalRotatedPillarKineticBlock.getDirection(state);
        Axis axis = face.getAxis();
		VertexConsumer vbSolid = buffer.getBuffer(RenderType.solid());

        float time = AnimationTickHolder.getRenderTime(differential.getLevel());
		float ringGearOffset = Mth.PI * getRotationOffsetForPosition(differential, differential.getBlockPos(), axis) / 180f;
		float ringGearAngle = ((time * differential.getSpeed() * 3f / 10 + ringGearOffset) % 360) / 180 * Mth.PI;

        BlockPos inputPos = differential.getBlockPos().relative(face);
        BlockPos controlPos = differential.getBlockPos().relative(face.getOpposite());

        BlockEntity inputBE = differential.getLevel().getBlockEntity(inputPos);
        BlockEntity controlBE = differential.getLevel().getBlockEntity(controlPos);

        float inputShaftOffset = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, inputPos) / 180f;
        float controlShaftOffset = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, controlPos) / 180f;

        float inputCogAngle = 0f;
        float controlCogAngle = 0f;

        if (differential.propagatesToMe(inputPos, face.getOpposite()) && inputBE instanceof KineticBlockEntity inputKBE) inputCogAngle = (time * differential.getPropagatedSpeed(inputKBE) * 3f / 10 % 360) / 180 * Mth.PI;
        if (differential.propagatesToMe(controlPos, face) && controlBE instanceof KineticBlockEntity controlKBE) controlCogAngle = (time * differential.getPropagatedSpeed(controlKBE) * 3f / 10 % 360) / 180 * Mth.PI;

        SuperByteBuffer ringGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_RING_GEAR, state, face, () -> rotateToFace(face));
        kineticRotationTransform(ringGear, differential, axis, ringGearAngle + ringGearOffset, light);
        ringGear.renderInto(ms, vbSolid);

        SuperByteBuffer eastGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_EAST_GEAR, state, face, () -> rotateToFace(face));
        kineticRotationTransform(eastGear, differential, axis, ringGearAngle + ringGearOffset, light);
        kineticRotationTransform(eastGear, differential, axis == Axis.X ? Axis.Z : Axis.X, ((controlCogAngle - inputCogAngle) / 2) * (axis == Axis.Z ? -1 : 1), light);
        eastGear.renderInto(ms, vbSolid);

        SuperByteBuffer westGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_WEST_GEAR, state, face, () -> rotateToFace(face));
        kineticRotationTransform(westGear, differential, axis, ringGearAngle + ringGearOffset, light);
        kineticRotationTransform(westGear, differential, axis == Axis.X ? Axis.Z : Axis.X, ((inputCogAngle - controlCogAngle) / 2) * (axis == Axis.Z ? -1 : 1), light);
        westGear.renderInto(ms, vbSolid);

        SuperByteBuffer topGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_CONTROL_GEAR, state, face, () -> rotateToFace(face));
        kineticRotationTransform(topGear, differential, axis, controlCogAngle + ringGearOffset, light);
        topGear.renderInto(ms, vbSolid);

        SuperByteBuffer topShaft = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_CONTROL_SHAFT, state, face, () -> rotateToFace(face));
        kineticRotationTransform(topShaft, differential, axis, controlCogAngle + controlShaftOffset, light);
        topShaft.renderInto(ms, vbSolid);

        SuperByteBuffer bottomGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_INPUT_GEAR, state, face, () -> rotateToFace(face));
        kineticRotationTransform(bottomGear, differential, axis, inputCogAngle + ringGearOffset, light);
        bottomGear.renderInto(ms, vbSolid);

        SuperByteBuffer bottomShaft = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_INPUT_SHAFT, state, face, () -> rotateToFace(face));
        kineticRotationTransform(bottomShaft, differential, axis, inputCogAngle + inputShaftOffset, light);
        bottomShaft.renderInto(ms, vbSolid);
    };

    public static PoseStack rotateToFace(Direction facing) {
		PoseStack poseStack = new PoseStack();
		TransformStack.cast(poseStack)
				.centre()
				.rotateToFace(facing)
				.multiply(com.mojang.math.Axis.XN.rotationDegrees(-90))
				.unCentre();
		return poseStack;
	}
    
};
