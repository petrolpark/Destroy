package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class VatSideRenderer extends SafeBlockEntityRenderer<VatSideBlockEntity> {

    private static final FluidStack THERMOMETER_FLUID;

    static {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        mixture.addMolecule(DestroyMolecules.MERCURY, DestroyMolecules.MERCURY.getPureConcentration());
        THERMOMETER_FLUID = MixtureFluid.of(1000, mixture, "");
    };

    public VatSideRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    @SuppressWarnings("null")
    protected void renderSafe(VatSideBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getController() == null) return;
        VertexConsumer vb = bufferSource.getBuffer(RenderType.cutout());
        float dialPivot = 5.75f / 16;

        Direction facing = be.direction.getOpposite();

        switch (be.getDisplayType()) {
            case NORMAL: {
                break;
            } case PIPE: {
                transformed(DestroyPartials.VAT_SIDE_PIPE, state, facing)
                    .renderInto(ms, vb);
                
                if (be.spoutingTicks == 0 || be.isPipeSubmerged(true, partialTicks) || facing == Direction.UP) break;
                // Render Fluid stream if necessary
                float fluidLevel = be.getController().getRenderedFluidLevel(partialTicks);
                if (facing == Direction.DOWN) {
                    FluidRenderer.renderFluidBox(be.spoutingFluid, 6.5f / 16f, fluidLevel - be.getVatOptional().get().getInternalHeight(), 6.5f / 16f, 
                        9.5f / 16f, 0 / 16f, 9.5f / 16f,
                        bufferSource, ms, light, false
                    );
                } else {
                    ms.pushPose();
                    ms.translate(0.5, 0.5, 0.5);
                    TransformStack.cast(ms)
                        .rotateY(AngleHelper.horizontalAngle(facing))
                        .rotateX(AngleHelper.verticalAngle(facing));
                    ms.translate(-0.5, -0.5, -0.5);
                    FluidRenderer.renderFluidBox(be.spoutingFluid, 6.5f / 16f, 4 / 16f, 17 / 16f, 9.5f / 16f, 6 / 16f, 19 / 16f, bufferSource, ms, light, false);
                    FluidRenderer.renderFluidBox(be.spoutingFluid, 6.5f / 16f, fluidLevel - be.pipeHeightAboveVatBase() + 4 / 16f, 19 / 16f, 
                        9.5f / 16f, 6 / 16f, 22 / 16f,
                        bufferSource, ms, light, false
                    );
                    ms.popPose();
                };
                break;

            } case BAROMETER: {
                transformed(DestroyPartials.VAT_SIDE_BAROMETER, state, facing)
                    .centre()
                    .rotateY(90)
                    .unCentre()
                    .light(light)
                    .renderInto(ms, vb);
                transformed(AllPartialModels.BOILER_GAUGE_DIAL, state, facing)
                    .centre()
                    .rotateY(90)
                    .unCentre()
                    .translate(2 / 16d, 0d, 0d)
                    .translate(0, dialPivot, dialPivot)
                    .rotateX(-90 * be.getPercentagePressure())
                    .translate(0, -dialPivot, -dialPivot)
                    .light(light)
                    .renderInto(ms, vb);
                break;

            } case THERMOMETER: {
                transformed(DestroyPartials.VAT_SIDE_THERMOMETER, state, facing)
                    .renderInto(ms, vb);

                ms.pushPose();
                ms.translate(0.5, 0.5, 0.5);
                TransformStack.cast(ms)
                    .rotateY(AngleHelper.horizontalAngle(facing))
                    .rotateX(AngleHelper.verticalAngle(facing));
                ms.translate(-0.5, -0.5, -0.5);

                Vec3 v0 = new Vec3(7 / 16f, 1.5f / 16f, -2 / 16f);
                Vec3 v1 = new Vec3(9 / 16f, 3.9f / 16f, 0 / 16f);
                Vec3 v2 = new Vec3(7.5f / 16f, 3.9f / 16f, -1.5f / 16f);
                Vec3 v3 = new Vec3(8.5f / 16f, (10.1f * Mth.clamp((be.getController().getTemperature() - 298f) / 202f, 0, 1) + 3.9f) / 16f, -0.5f / 16f);
                FluidRenderer.renderFluidBox(THERMOMETER_FLUID, (float)v0.x, (float)v0.y, (float)v0.z, (float)v1.x, (float)v1.y, (float)v1.z, bufferSource, ms, light, true);
                FluidRenderer.renderFluidBox(THERMOMETER_FLUID, (float)v2.x, (float)v2.y, (float)v2.z, (float)v3.x, (float)v3.y, (float)v3.z, bufferSource, ms, light, false);
                ms.popPose();
                break;
            }
        };
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBufferer.partial(model, blockState)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
            .rotateX(AngleHelper.verticalAngle(facing))
			.unCentre();
	};

    @Override
    public boolean shouldRenderOffScreen(VatSideBlockEntity be) {
        return true;
    };
};
