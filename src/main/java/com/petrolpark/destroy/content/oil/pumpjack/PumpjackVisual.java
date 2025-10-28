package com.petrolpark.destroy.content.oil.pumpjack;

import com.petrolpark.destroy.client.DestroyPartials;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PumpjackVisual extends AbstractBlockEntityVisual<PumpjackBlockEntity> implements SimpleDynamicVisual {

    protected final TransformedInstance cam;
    protected final TransformedInstance linkage;
    protected final TransformedInstance beam;
    protected final TransformedInstance pump;

    private static final double beamRotation = Math.asin(5 / 17d);

    public PumpjackVisual(VisualizationContext ctx, PumpjackBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        cam = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_CAM)).createInstance();
        linkage = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_LINKAGE)).createInstance();
        beam = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_BEAM)).createInstance();
        pump = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_PUMP)).createInstance();

        animate(partialTick);
    }

    @Override
    public void beginFrame(Context ctx) {
        animate(ctx.partialTick());
    }

    private void animate(float pt) {
        float angle = blockEntity.getRenderAngle();

        Direction facing = PumpjackBlock.getFacing(blockState);

        transformed(cam, facing)
            .translate(0d, 0d, 1d)
            .center()
            .rotateX(angle - Mth.HALF_PI)
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter()
            .setChanged();

        transformed(linkage, facing)
            .translate(0d, -4.5 / 16d, 1d)
            .translate(0d, Mth.sin(angle) * 5 / 16d, -Mth.cos(angle) * 5 / 16d)
            .center()
            .rotateX((float) ((Mth.cos(angle)) * beamRotation * 0.73d))
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter()
            .setChanged();

        transformed(beam, facing)
            .translate(0d, 1d, 0d)
            .center()
            .rotateX((float) ((Mth.sin(angle)) * -beamRotation))
            .center()
            .translate(0d, -1d, 0d)
            .uncenter()
            .uncenter()
            .setChanged();

        transformed(pump, facing)
            .translate(0d, -Mth.sin(angle) * 3 / 16d, 0d)
            .setChanged();
    }

    protected TransformedInstance transformed(TransformedInstance modelData, Direction facing) {
        return modelData.setIdentityTransform()
            .translate(pos)
            .center()
            .rotateYDegrees(AngleHelper.horizontalAngle(facing))
            .uncenter();
    };


    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(cam);
        consumer.accept(linkage);
        consumer.accept(beam);
        consumer.accept(pump);
    }

    @Override
    public void updateLight(float v) {
        relight(pos.above(), cam, linkage, beam, pump);
    }

    @Override
    protected void _delete() {
        cam.delete();
        linkage.delete();
        beam.delete();
        pump.delete();
    }
};