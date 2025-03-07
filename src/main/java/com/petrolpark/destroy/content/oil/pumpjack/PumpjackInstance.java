package com.petrolpark.destroy.content.oil.pumpjack;

import com.petrolpark.destroy.client.DestroyPartials;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualManager;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static oshi.util.platform.windows.WmiQueryHandler.createInstance;

public class PumpjackInstance extends AbstractBlockEntityVisual<PumpjackBlockEntity> implements SimpleDynamicVisual {

    protected final TransformedInstance cam;
	protected final TransformedInstance linkage;
	protected final TransformedInstance beam;
    protected final TransformedInstance pump;

    public PumpjackInstance(VisualizationContext ctx, PumpjackBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        cam = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_CAM)).createInstance();

        linkage = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_LINKAGE)).createInstance();

        beam = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_BEAM)).createInstance();

        pump = ctx.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(DestroyPartials.PUMPJACK_PUMP)).createInstance();
    };

    @Override
    public void beginFrame(Context ctx) {
        Float angle = blockEntity.getTargetAngle();
		if (angle == null) {
			cam.setZeroTransform();
			linkage.setZeroTransform();
			beam.setZeroTransform();
            pump.setZeroTransform();
			return;
		};

        Direction facing = PumpjackBlock.getFacing(blockState);

        transformed(cam, facing)
            .translate(0d, 0d, 1d)
            .center()
            .rotateX(angle - Mth.HALF_PI)
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter();

        transformed(linkage, facing)
            .translate(0d, -5 / 16d, 1d)
            .translate(0d, Mth.sin(angle) * 5 / 16d, -Mth.cos(angle) * 5 / 16d)
            .center()
            .rotateXDegrees(Mth.cos(angle) * 10)
            .center()
            .translate(0d, 0d, -1d)
            .uncenter()
            .uncenter();

        transformed(beam, facing)
            .translate(0d, 1d, 0d)
            .center()
            .rotateXDegrees((Mth.sin(angle) - 1) * -18)
            .center()
            .translate(0d, -1d, 0d)
            .uncenter()
            .uncenter();

        transformed(pump, facing)
            .translate(0d, (3 / 16) - (Mth.sin(angle) * 3 / 16d), 0d);
    };

    protected TransformedInstance transformed(TransformedInstance modelData, Direction facing) {
		return modelData.setIdentityTransform()
			.translate(pos)
			.center()
			.rotateYDegrees(AngleHelper.horizontalAngle(facing))
			.uncenter();
	};


    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

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
