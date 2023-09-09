package com.petrolpark.destroy.block.renderer;

import java.util.Optional;

import com.jozufozu.flywheel.util.transform.TransformStack;
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
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

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
        ItemStackHandler inv = controller.inventory;

        Vec3 relativeInternalLowerCorner = Vec3.atLowerCornerOf(vat.getInternalLowerCorner().subtract(controller.getBlockPos()));
        Vec3 relativeInternalUpperCorner = Vec3.atLowerCornerOf(vat.getUpperCorner().subtract(controller.getBlockPos()));

        float fluidLevel = controller.getRenderedFluidLevel(partialTicks);
        float relativeFluidLevel = (float)(relativeInternalLowerCorner.y() + fluidLevel);

        // Vat sides
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

        // Items
        int itemCount = 0;
		for (int slot = 0; slot < inv.getSlots(); slot++) if (!inv.getStackInSlot(slot).isEmpty()) itemCount += inv.getStackInSlot(slot).getCount();
        if (itemCount != 0) {
            float offset = ((float)Math.min(relativeInternalUpperCorner.x() - relativeInternalLowerCorner.x(), relativeInternalUpperCorner.z() - relativeInternalLowerCorner.z()) - 0.5f) / (2f * itemCount);
            float angle = 0f;
            Vec3 center = new Vec3(relativeInternalLowerCorner.x() + (float)vat.getInternalWidth() / 2f, relativeFluidLevel - 0.1f, relativeInternalLowerCorner.z() + (float)vat.getInternalLength() / 2f);
            Vec3 south = new Vec3(0, 0, 1);
            int nthItem = 1;
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack.isEmpty()) continue;

                for (int item = 0; item < stack.getCount(); item++) {
                    Vec3 itemPosition = center.add(VecHelper.rotate(south.scale(offset * (nthItem - 1)), angle, Axis.Y)); // Spiral out

                    ms.pushPose();
                    if (fluidLevel > 0) ms.translate(0, (Mth.sin(AnimationTickHolder.getRenderTime(controller.getLevel()) / 12f + angle) + 1.5f) * 1 / 32f, 0); // Bobbing
                    ms.translate(itemPosition.x(), itemPosition.y(), itemPosition.z()); // Position
                    TransformStack.cast(ms) // Rotation of Item itself
                        .rotateY(angle + 35)
                        .rotateX(65);
                    renderItem(ms, bufferSource, light, overlay, stack);
                    ms.popPose();

                    nthItem++;
                    angle += 10 + 270 / nthItem;
                };
            };
        };

        // Fluid
        FluidRenderer.renderFluidBox(fluidStack,
            (float)relativeInternalLowerCorner.x, (float)relativeInternalLowerCorner.y, (float)relativeInternalLowerCorner.z,
            (float)relativeInternalUpperCorner.x, relativeFluidLevel, (float)relativeInternalUpperCorner.z,
            bufferSource, ms, light, true);
    };

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
	};

    @Override
    public boolean shouldRenderOffScreen(VatControllerBlockEntity controller) {
        return true;
    };
    
};
