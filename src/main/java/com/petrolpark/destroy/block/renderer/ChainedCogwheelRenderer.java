package com.petrolpark.destroy.block.renderer;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.IChainableBlock;
import com.petrolpark.destroy.block.entity.ChainedCogwheelBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.petrolpark.destroy.util.MathsHelper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ChainedCogwheelRenderer extends KineticBlockEntityRenderer<ChainedCogwheelBlockEntity> {

    public static final double linkLength = 4 / 16f;

    public ChainedCogwheelRenderer(Context context) {
        super(context);
    };

    /**
     * Largely copied from the {@link com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer Create source code}.
     */
    @Override
    @SuppressWarnings("null") // It thinks getLevel() might be null
    protected void renderSafe(ChainedCogwheelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (Backend.canUseInstancing(be.getLevel())) return;
        BlockState state = getRenderedBlockState(be);
        if (state == null) return;

        Axis axis = getRotationAxisOf(be);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());
        Direction facing = Direction.fromAxisAndDirection(axis, AxisDirection.POSITIVE);
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float targetAngle = ((time * be.getSpeed() * 3f / 10) % 360);

        if (be.controller && be.hasLevel()) {
            BlockEntity otherBE = be.getLevel().getBlockEntity(new BlockPos(MathsHelper.add(be.getBlockPos(), be.partner)));
            if (!(otherBE instanceof ChainedCogwheelBlockEntity otherCBE)) return;
            ms.pushPose();
            for (Pair<Vec3, Double> posAndRot : getLinkPositionsAndRotations(axis, state, be.partner, otherCBE.copiedState, targetAngle)) {
                CachedBufferer.partial(DestroyPartials.CHAIN_LINK, state)
                    .translate(posAndRot.getFirst())
                    .rotate(posAndRot.getSecond() + (axis == Axis.Z ? 90d : 0d), axis)
                    .rotateX(axis == Axis.Z ? 90 : 0)
                    .rotateZ(axis == Axis.X ? 90 : 0)
                    .renderInto(ms, vc);
            };
            ms.popPose();
        };

        if (AllBlocks.LARGE_COGWHEEL.has(state)) {
            renderRotatingBuffer(be, CachedBufferer.partialFacingVertical(AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, be.getBlockState(), facing), ms, vc, light);

            float angle = BracketedKineticBlockEntityRenderer.getAngleForLargeCogShaft(be, axis);
            SuperByteBuffer shaft = CachedBufferer.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, be.getBlockState(), facing);
            kineticRotationTransform(shaft, be, axis, angle, light);
            shaft.renderInto(ms, vc);
        } else {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        };
    };

    /**
     * 
     * @param axis
     * @param thisCog
     * @param otherPos Relative position of lower corner of partner Cogwheel
     * @param otherCog
     * @return
     */
    public static List<Pair<Vec3, Double>> getLinkPositionsAndRotations(Axis axis, BlockState thisCog, BlockPos otherPos, BlockState otherCog, float cogAngle) {
        Vec3 thisCenter = IChainableBlock.getRelativeCenterOfRotation(thisCog);
        Vec3 thatCenter = Vec3.atLowerCornerOf(otherPos).add(IChainableBlock.getRelativeCenterOfRotation(otherCog));
        Vec3 thisToThat = thatCenter.subtract(thisCenter);
        double d = thisToThat.length();
        double r1 = IChainableBlock.getRadius(thisCog);
        double r2 = IChainableBlock.getRadius(otherCog);
        double l = Math.sqrt(d * d - (r1 - r2) * (r1 - r2));
        Vec3 forward = new Vec3(axis == Axis.Y ? Direction.EAST.step() : Direction.UP.step());
        Vec3 up = new Vec3(Direction.get(AxisDirection.POSITIVE, axis).step());
        double angleToThat = MathsHelper.angleBetween(forward, thatCenter.subtract(thisCenter), up);
        double angle1 = Math.acos((r1 - r2) / d) * 180d / Mth.PI;
        double angle2 = 180d - angle1;

        List<Pair<Vec3, Double>> pandrs = new ArrayList<>();

        // double linearAngle1 = (angleToThat + angle1 - 90d) % 360d;
        // Vec3 linearDirection1 = MathsHelper.rotate(forward, up, linearAngle1);
        // double linearAngle2 = (angleToThat - angle1 + 90d) % 360d;
        // Vec3 linearDirection2 = MathsHelper.rotate(forward, up, linearAngle2);

        // // Segments around this Cog
        // double angleIncrement1 = 2 * Math.asin(linkLength / (2 * r1)) * 180d / Mth.PI;
        // double linkRotation1 = angleIncrement1 + Math.acos(linkLength / (2 * r1)) * 180d / Mth.PI;
        // int i = 0;
        // for (double a = linearAngle1 + 90d; a > 0d; a -= angleIncrement1) {
        //     double aa = angleToThat + angle1 + a + (cogAngle % angleIncrement1);
        //     double pointing = (aa + linkRotation1) % 360;
        //     if (i == 0) {
        //         pointing = 90d + Math.min(linearAngle1, linearAngle1 - pointing) * (cogAngle % angleIncrement1 / angleIncrement1);
        //     };
        //     pandrs.add(Pair.of(
        //         thisCenter.add(MathsHelper.rotate(forward.scale(r1), up, aa)),
        //         pointing
        //     ));
        //     i++;
        // };

        // // Segments around that cog
        // double angleIncrement2 = 2 * Math.asin(linkLength / (2 * r2)) * 180d / Mth.PI;
        // double linkRotation2 = angleIncrement2 + Math.acos(linkLength / (2 * r2)) * 180d / Mth.PI;
        // for (double a = 0d; a < 360d - (2 * angle2); a += angleIncrement2) {
        //     double aa = 180d + angleToThat + angle2 + a + ((cogAngle * IChainableBlock.getPropagatedSpeed(otherCog) / IChainableBlock.getPropagatedSpeed(thisCog)) % angleIncrement2);
        //     pandrs.add(Pair.of(
        //         thatCenter.add(MathsHelper.rotate(forward.scale(r2), up, aa)),
        //         aa + linkRotation2
        //     ));
        // };

        // int numberOfLinearLinks = (int)((l / linkLength) + 0.5d);
        // double linearLinkLength = l / numberOfLinearLinks;

        // // Straight section 1
        // Vec3 startPos1 = thisCenter.add(MathsHelper.rotate(forward.scale(r1), up, angleToThat - angle1));
        // for (int j = 1; j < numberOfLinearLinks; j++) {
        //     double a = linearAngle1;
        //     pandrs.add(Pair.of(
        //         startPos1.add(linearDirection1.scale(j * linearLinkLength + linearLinkLength * (cogAngle % angleIncrement1 / angleIncrement1))),
        //         a
        //     ));
        // };

        // Vec3 startPos2 = thisCenter.add(MathsHelper.rotate(forward.scale(r1), up, angleToThat + angle1));
        // for (int j = 1; j < numberOfLinearLinks; j++) {
        //     double a = linearAngle2 + 180d;
        //     pandrs.add(Pair.of(
        //         startPos2.add(linearDirection2.scale(j * linearLinkLength - linearLinkLength * (cogAngle % angleIncrement1 / angleIncrement1))),
        //         a
        //     ));
        // };

        return pandrs;
    };

    @Override
    protected BlockState getRenderedBlockState(ChainedCogwheelBlockEntity be) {
        return be.copiedState;
    };
    
};
