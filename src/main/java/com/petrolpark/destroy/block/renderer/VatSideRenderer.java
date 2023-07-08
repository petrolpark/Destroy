package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.core.PartialModel;
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

        switch (be.getDisplayType()) {
            case NORMAL: {
                break;
            } case PIPE: {
                transformed(DestroyPartials.VAT_SIDE_PIPE, state, be.direction.getOpposite())
                    .renderInto(ms, vb);
                break;
            } case BAROMETER: {
                transformed(DestroyPartials.VAT_SIDE_BAROMETER, state, be.direction.getClockWise())
                    .light(light)
                    .renderInto(ms, vb);
                transformed(AllPartialModels.BOILER_GAUGE_DIAL, state, be.direction.getClockWise())
                    .translate(2 / 16d, 0d, 0d)
                    .translate(0, dialPivot, dialPivot)
                    .rotateX(-90 * be.getPercentagePressure())
                    .translate(0, -dialPivot, -dialPivot)
                    .light(light)
                    .renderInto(ms, vb);
                break;
            } case THERMOMETER: {
                transformed(DestroyPartials.VAT_SIDE_THERMOMETER, state, be.direction.getOpposite())
                    .renderInto(ms, vb);
                FluidRenderer.renderFluidBox(THERMOMETER_FLUID, 7 / 16f, 1.5f / 16f, -2 / 16f, 9 / 16f, 3.9f / 16f, 0 / 16f, bufferSource, ms, light, true);
                FluidRenderer.renderFluidBox(THERMOMETER_FLUID, 7.5f / 16f, 3.9f / 16f, -1.5f / 16f, 8.5f / 16f, (10.1f * Mth.clamp((be.getController().getTemperature() - 298f) / 202f, 0, 1) + 3.9f) / 16f, -0.5f / 16f, bufferSource, ms, light, false);
                break;
            }
        }
    };

    private SuperByteBuffer transformed(PartialModel model, BlockState blockState, Direction facing) {
		return CachedBufferer.partial(model, blockState)
			.centre()
			.rotateY(AngleHelper.horizontalAngle(facing))
			.unCentre();
	};
    
};
