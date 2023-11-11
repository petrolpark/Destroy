package com.petrolpark.destroy.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.DifferentialBlockEntity;
import com.petrolpark.destroy.block.instance.PlanetaryGearsetInstance;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
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
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DifferentialRenderer extends KineticBlockEntityRenderer<DifferentialBlockEntity> {

    public DifferentialRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(DifferentialBlockEntity differential, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        //if (Backend.canUseInstancing(planetaryGearsetBlockEntity.getLevel())) return;

		BlockState state = getRenderedBlockState(differential);
        Axis axis = state.getValue(RotatedPillarKineticBlock.AXIS);
		VertexConsumer vbSolid = buffer.getBuffer(RenderType.solid());

        float time = AnimationTickHolder.getRenderTime(differential.getLevel());
		float ringGearOffset = Mth.PI * getRotationOffsetForPosition(differential, differential.getBlockPos(), axis) / 180f;
		float ringGearAngle = ((time * differential.getSpeed() * 3f / 10 + ringGearOffset) % 360) / 180 * Mth.PI;

        BlockPos connection1 = differential.getBlockPos().relative(Direction.get(AxisDirection.POSITIVE, axis));
        BlockPos connection2 = differential.getBlockPos().relative(Direction.get(AxisDirection.NEGATIVE, axis));

        float shaft1Offset = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, connection1) / 180f;
        float shaft2Offset = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, connection2) / 180f;

        float cog1Angle = (time * getSpeed(differential, connection1) * 3f / 10 % 360) / 180 * Mth.PI;
        float cog2Angle = (time * getSpeed(differential, connection2) * 3f / 10 % 360) / 180 * Mth.PI;

        SuperByteBuffer ringGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_RING_GEAR, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(ringGear, differential, axis, ringGearAngle + ringGearOffset, light);
        ringGear.renderInto(ms, vbSolid);

        SuperByteBuffer eastGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_EAST_GEAR, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(eastGear, differential, axis, ringGearAngle + ringGearOffset, light);
        kineticRotationTransform(eastGear, differential, axis == Axis.X ? Axis.Z : Axis.X, ((cog2Angle - cog1Angle) / 2) * (axis == Axis.Z ? -1 : 1), light);
        eastGear.renderInto(ms, vbSolid);

        SuperByteBuffer westGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_WEST_GEAR, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(westGear, differential, axis, ringGearAngle + ringGearOffset, light);
        kineticRotationTransform(westGear, differential, axis == Axis.X ? Axis.Z : Axis.X, ((cog1Angle - cog2Angle) / 2) * (axis == Axis.Z ? -1 : 1), light);
        westGear.renderInto(ms, vbSolid);

        SuperByteBuffer topGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_TOP_GEAR, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(topGear, differential, axis, cog2Angle + ringGearOffset, light);
        topGear.renderInto(ms, vbSolid);

        SuperByteBuffer topShaft = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_TOP_SHAFT, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(topShaft, differential, axis, cog2Angle + shaft2Offset, light);
        topShaft.renderInto(ms, vbSolid);

        SuperByteBuffer bottomGear = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_BOTTOM_GEAR, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(bottomGear, differential, axis, cog1Angle + ringGearOffset, light);
        bottomGear.renderInto(ms, vbSolid);

        SuperByteBuffer bottomShaft = CachedBufferer.partialDirectional(DestroyPartials.DIFFERENTIAL_BOTTOM_SHAFT, state, Direction.get(AxisDirection.POSITIVE, axis), () -> PlanetaryGearsetInstance.rotateToAxis(axis));
        kineticRotationTransform(bottomShaft, differential, axis, cog1Angle + shaft1Offset, light);
        bottomShaft.renderInto(ms, vbSolid);
    };

    private float getSpeed(DifferentialBlockEntity differential, BlockPos pos) {
        Level level = differential.getLevel();
        if (level == null) return 0f;
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || !(be instanceof KineticBlockEntity kbe)) return 0f;
        return differential.getPropagatedSpeed(kbe);
    };
    
};
