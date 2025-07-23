package com.petrolpark.destroy.content.processing.treetap;

import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.base.ShaftVisual;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public class TreeTapVisual extends ShaftVisual<TreeTapBlockEntity> implements SimpleDynamicVisual {

    protected final TransformedInstance arm;

    public TreeTapVisual(VisualizationContext ctx, TreeTapBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        arm = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.TREE_TAP_ARM))
            .createInstance();

        updateAnimation();
    }

    @Override
    public void beginFrame(Context ctx) {
        updateAnimation();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(pos, arm);
    }

    @Override
    public void _delete() {
        super._delete();
        arm.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(arm);
    }

    private void updateAnimation() {
        Direction facing = blockState.getValue(TreeTapBlock.HORIZONTAL_FACING);

        arm.setIdentityTransform()
            .translate(getVisualPosition())
            .center()
            .rotateDegrees(9f * Mth.sin(KineticBlockEntityRenderer.getAngleForBe(blockEntity, blockEntity.getBlockPos(), facing.getClockWise().getAxis())), facing.getClockWise().getAxis())
            .rotateToFace(facing.getOpposite())
            .uncenter()
            .setChanged();
    }
}