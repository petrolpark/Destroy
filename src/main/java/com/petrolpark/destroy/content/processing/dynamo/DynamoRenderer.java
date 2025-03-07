package com.petrolpark.destroy.content.processing.dynamo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.petrolpark.compat.create.CreateClient.OUTLINER;

public class DynamoRenderer extends KineticBlockEntityRenderer<DynamoBlockEntity> {

    public DynamoRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(DynamoBlockEntity dynamo, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(dynamo, partialTicks, ms, buffer, light, overlay);
        if ((!dynamo.isRunning() && dynamo.soundDuration <= 0) || !dynamo.hasLevel()) return; // It thinks getLevel() can be null (it can't)
        RandomSource rand = dynamo.getLevel().getRandom();
        if (rand.nextFloat() > 0.1f || !dynamo.shouldRenderArcs()) return;

        boolean northSouthAligned = dynamo.getBlockState().getValue(DynamoBlock.AXIS) == Axis.Z;
        Vec3 electrodePos1 = Vec3.atLowerCornerOf(dynamo.getBlockPos()).add(northSouthAligned ? 3 / 16f : 8 / 16f, -2 / 16f, northSouthAligned ? 8 / 16f : 3 / 16f);
        Vec3 electrodePos2 = Vec3.atLowerCornerOf(dynamo.getBlockPos()).add(northSouthAligned ? 13 / 16f : 8 / 16f, -2 / 16f, northSouthAligned ? 8 / 16f : 13 / 16f);
        Vec3 targetPos = dynamo.getLightningTargetPosition();

        for (boolean whichElectrode : Iterate.trueAndFalse) {
            Vec3 electrodePos = whichElectrode ? electrodePos1 : electrodePos2;
            Vec3 directPath = targetPos.subtract(electrodePos);
            Vec3 firstRandomPos = randomPointOnPlane(directPath, electrodePos.add(directPath.scale(rand.nextFloat() / 2f)), 0.5f, rand);
            Vec3 secondRandomPos = randomPointOnPlane(directPath, electrodePos.add(directPath.scale(0.5f + (rand.nextFloat() / 2f))), 0.5f, rand);

            OUTLINER.showLine("dynamo_line_0_" + (whichElectrode ? "0" : "1"), electrodePos, firstRandomPos)
                .colored(0xa3f0f7)
                .lineWidth(1 / 64f)
                .disableCull();
            OUTLINER.showLine("dynamo_line_1_" + (whichElectrode ? "0" : "1"), firstRandomPos, secondRandomPos)
                .colored(0xa3f0f7)
                .lineWidth(1 / 64f)
                .disableCull();
            OUTLINER.showLine("dynamo_line_2_" + (whichElectrode ? "0" : "1"), secondRandomPos, targetPos)
                .colored(0xa3f0f7)
                .lineWidth(1 / 64f)
                .disableCull();
        };
    };

    private Vec3 randomPointOnPlane(Vec3 normal, Vec3 pointOnPlane, float roughDiameter, RandomSource rand) {
        double radius = roughDiameter / 2;
        double d = normal.dot(pointOnPlane);
        double x = pointOnPlane.x - radius + (rand.nextFloat() * roughDiameter);
        double z = pointOnPlane.z - radius + (rand.nextFloat() * roughDiameter);
        return new Vec3(x, (d - (normal.x * x)- (normal.z * z)) / normal.y, z);
    };

    @Override
    protected SuperByteBuffer getRotatedModel(DynamoBlockEntity be, BlockState state) {
        return CachedBuffers.partial(state.getValue(DynamoBlock.ARC_FURNACE) ? DestroyPartials.ARC_FURNACE_SHAFT : DestroyPartials.DYNAMO_SHAFT, state);
    };

    @Override
    public boolean shouldRenderOffScreen(DynamoBlockEntity pBlockEntity) {
        return true;
    };

}
