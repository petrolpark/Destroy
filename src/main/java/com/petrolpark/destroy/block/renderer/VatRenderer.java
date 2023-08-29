package com.petrolpark.destroy.block.renderer;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.petrolpark.destroy.util.vat.Vat;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class VatRenderer extends SafeBlockEntityRenderer<VatControllerBlockEntity> {

    private static final float dialPivot = 5.75f / 16;
    
    public VatRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(VatControllerBlockEntity controller, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!controller.getVatOptional().isPresent()) return;
        Vat vat = controller.getVatOptional().get();
        BlockState state = controller.getBlockState();
        VertexConsumer vbSolid = bufferSource.getBuffer(RenderType.solid());
        VertexConsumer vbCutout = bufferSource.getBuffer(RenderType.cutout());
        
        Vec3 relativeInternalLowerCorner = Vec3.atLowerCornerOf(vat.getInternalLowerCorner().subtract(controller.getBlockPos()));
        Vec3 relativeInternalUpperCorner = Vec3.atLowerCornerOf(vat.getUpperCorner().subtract(controller.getBlockPos()));

        for (BlockPos sidePos : vat.getSideBlockPositions()) {
            Optional<VatSideBlockEntity> vatSideOptional = controller.getLevel().getBlockEntity(sidePos, DestroyBlockEntityTypes.VAT_SIDE.get());
            if (vatSideOptional.isEmpty()) continue;
            VatSideBlockEntity vatSide = vatSideOptional.get();
            Direction facing = vatSide.direction;
            switch (vatSide.getDisplayType()) {
                case PIPE: {
                    CachedBufferer.partialFacing(DestroyPartials.VAT_SIDE_PIPE, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    break;

                } case BAROMETER: {
                    CachedBufferer.partialFacing(DestroyPartials.VAT_SIDE_BAROMETER, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    if (facing.getAxis() == Axis.Y) break;
                    ms.pushPose();
                    CachedBufferer.partial(AllPartialModels.BOILER_GAUGE_DIAL, state)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .centre()
                        .rotateY((facing.getAxis() == Axis.X ? facing.getClockWise() : facing.getCounterClockWise()).toYRot())
                        .unCentre()
                        .translate(2 / 16f, 0, 0)
                        .translate(0, dialPivot, dialPivot)
                        .rotateX(-90 * controller.getPercentagePressure())
                        .translate(0, -dialPivot, -dialPivot)
                        .light(light)
                        .renderInto(ms, vbSolid);
                    ms.popPose();
                    break;
                } case THERMOMETER: {
                    CachedBufferer.partialFacing(DestroyPartials.VAT_SIDE_THERMOMETER, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbCutout);
                    break;
                } default: {}
            };
        };

        FluidStack fluidStack = controller.getTank().getFluid();
        if (fluidStack.isEmpty()) return;

        // Render Fluid
        FluidRenderer.renderFluidBox(fluidStack,
            (float)relativeInternalLowerCorner.x, (float)relativeInternalLowerCorner.y, (float)relativeInternalLowerCorner.z,
            (float)relativeInternalUpperCorner.x, (float)(relativeInternalLowerCorner.y + controller.getRenderedFluidLevel(partialTicks)), (float)relativeInternalUpperCorner.z,
            bufferSource, ms, light, true);
    };

    @Override
    public boolean shouldRenderOffScreen(VatControllerBlockEntity controller) {
        return true;
    };
    
};
