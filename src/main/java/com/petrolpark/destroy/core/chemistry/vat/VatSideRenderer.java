package com.petrolpark.destroy.core.chemistry.vat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
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
    protected void renderSafe(VatSideBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        VatControllerBlockEntity controller = be.getController();
        if (controller == null) return;

        Direction facing = be.direction.getOpposite();

        switch (be.getDisplayType()) {
            case PIPE: {
                if (be.spoutingTicks == 0 || be.isPipeSubmerged(true, partialTicks) || facing == Direction.UP) break;
                // Render Fluid stream if necessary
                float fluidLevel = controller.getRenderedFluidLevel(partialTicks);
                if (facing == Direction.DOWN) {
                    ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(be.spoutingFluid, 6.5f / 16f, fluidLevel - be.getVatOptional().get().getInternalHeight(), 6.5f / 16f,
                        9.5f / 16f, 0 / 16f, 9.5f / 16f,
                        bufferSource, ms, light, false, true
                    );
                } else {
                    ms.pushPose();
                    ms.translate(0.5, 0.5, 0.5);
                    TransformStack.of(ms)
                        .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                        .rotateXDegrees(AngleHelper.verticalAngle(facing));
                    ms.translate(-0.5, -0.5, -0.5);
                    ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(be.spoutingFluid, 6.5f / 16f, 4 / 16f, 17 / 16f, 9.5f / 16f, 6 / 16f, 19 / 16f, bufferSource, ms, light, false, true);
                    ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(be.spoutingFluid,6.5f / 16f, fluidLevel - be.pipeHeightAboveVatBase() + 4 / 16f, 19 / 16f,
                        9.5f / 16f, 6 / 16f, 22 / 16f,
                        bufferSource, ms, light, false, true
                    );
                    ms.popPose();
                };
                break;

            } case THERMOMETER: {

                ms.pushPose();
                ms.translate(0.5, 0.5, 0.5);
                TransformStack.of(ms)
                    .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                    .rotateXDegrees(AngleHelper.verticalAngle(facing));
                ms.translate(-0.5, -0.5, -0.5);

                Vec3 v0 = new Vec3(7 / 16f, 1.5f / 16f, -2 / 16f);
                Vec3 v1 = new Vec3(9 / 16f, 3.9f / 16f, 0 / 16f);
                Vec3 v2 = new Vec3(7.5f / 16f, 3.9f / 16f, -1.5f / 16f);
                Vec3 v3 = new Vec3(8.5f / 16f, (10.1f * Mth.clamp((controller.getClientTemperature(partialTicks) - 298f) / 202f, 0, 1) + 3.9f) / 16f, -0.5f / 16f);
                ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(THERMOMETER_FLUID, (float)v0.x, (float)v0.y, (float)v0.z, (float)v1.x, (float)v1.y, (float)v1.z, bufferSource, ms, light, true, true);
                ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(THERMOMETER_FLUID, (float)v2.x, (float)v2.y, (float)v2.z, (float)v3.x, (float)v3.y, (float)v3.z, bufferSource, ms, light, false, true);
                ms.popPose();
                break;
            } default: {}
        };
    };

    @Override
    public boolean shouldRenderOffScreen(VatSideBlockEntity be) {
        return true;
    };

    @Override
    public boolean shouldRender(VatSideBlockEntity vatSide, Vec3 cameraPos) {
        return true;
    };
};
