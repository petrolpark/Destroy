package com.petrolpark.destroy.core.chemistry.vat;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
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
import org.jetbrains.annotations.NotNull;

public class VatRenderer extends SafeBlockEntityRenderer<VatControllerBlockEntity> {

    private static final float dialPivot = 8f / 16;
    
    public VatRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    @SuppressWarnings("null")
    protected void renderSafe(VatControllerBlockEntity controller, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (!controller.hasLevel() || !controller.getVatOptional().isPresent()) return;
        Vat vat = controller.getVatOptional().get();
        BlockState state = controller.getBlockState();
        VertexConsumer vbSolid = bufferSource.getBuffer(RenderType.solid());
        VertexConsumer vbCutout = bufferSource.getBuffer(RenderType.cutout());
        SmartInventory inv = controller.inventory;

        Vec3 relativeInternalLowerCorner = Vec3.atLowerCornerOf(vat.getInternalLowerCorner().subtract(controller.getBlockPos()));
        Vec3 relativeInternalUpperCorner = Vec3.atLowerCornerOf(vat.getUpperCorner().subtract(controller.getBlockPos()));

        float fluidLevel = controller.getRenderedFluidLevel(partialTicks);
        float relativeFluidLevel = (float)(relativeInternalLowerCorner.y() + fluidLevel);

        // Vat sides
        for (BlockPos sidePos : vat.getSideBlockPositions()) {
            Optional<VatSideBlockEntity> vatSideOptional = controller.getLevel().getBlockEntity(sidePos, DestroyBlockEntityTypes.VAT_SIDE.get()); // It thinks getLevel() can be null (it can't)
            if (vatSideOptional.isEmpty()) continue;
            VatSideBlockEntity vatSide = vatSideOptional.get();
            Direction facing = vatSide.direction;
            switch (vatSide.getDisplayType()) {
                case PIPE: {
                    CachedBuffers.partialFacing(DestroyPartials.VAT_SIDE_PIPE, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    break;

                } case BAROMETER: {
                    CachedBuffers.partialFacing(DestroyPartials.VAT_SIDE_BAROMETER, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    if (facing.getAxis() == Axis.Y) break;
                    ms.pushPose();
                    CachedBuffers.partial(DestroyPartials.VAT_SIDE_BAROMETER_DIAL, state)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .center()
                        .rotateYDegrees((facing.getAxis() == Axis.X ? facing.getClockWise() : facing.getCounterClockWise()).toYRot())
                        .uncenter()
                        .translate(2 / 16f, 0, 0)
                        .translate(0d, dialPivot, dialPivot)
                        .rotateXDegrees(-90 - 180 * Mth.clamp(controller.getClientPressure(partialTicks) / controller.getVatOptional().get().getMaxPressure(), -1f, 1f))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    ms.popPose();
                    break;
                } case THERMOMETER: {
                    CachedBuffers.partialFacing(DestroyPartials.VAT_SIDE_THERMOMETER, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbCutout);
                    break;
                } case BAROMETER_BLOCKED: case THERMOMETER_BLOCKED: {
                    CachedBuffers.partialFacing(DestroyPartials.VAT_SIDE_REDSTONE_INTERFACE, state, facing)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbCutout);
                    break;
                } case OPEN_VENT: case CLOSED_VENT: {
                    CachedBuffers.partial(DestroyPartials.VAT_SIDE_VENT, state)
                        .translate(sidePos.subtract(controller.getBlockPos()))
                        .light(light)
                        .renderInto(ms, vbSolid);
                    for (boolean top : Iterate.trueAndFalse) {
                        for (int i = 0; i < 5; i++) {
                            CachedBuffers.partial(DestroyPartials.VAT_SIDE_VENT_BAR, state)
                                .translate(sidePos.subtract(controller.getBlockPos()))
                                .translate((4 / 16f) + (i * 2 / 16f), top ? 17.5 / 16f : -1.5 / 16f, 0f)
                                .rotateZDegrees(vatSide.ventOpenness.getValue(partialTicks) * -75)
                                .light(light)
                                .renderInto(ms, vbSolid);
                        };
                    };
                    break;
                } default: {}
            };
        };

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
                    TransformStack.of(ms) // Rotation of Item itself
                        .rotateYDegrees(angle + 35)
                        .rotateXDegrees(65);
                    renderItem(ms, bufferSource, light, overlay, stack);
                    ms.popPose();

                    nthItem++;
                    angle += 10 + 270.f / nthItem;
                };
            };
        };

        // Fluids
        FluidStack fluidStack = controller.getLiquidTankContents();
        if (!fluidStack.isEmpty()) {
            FluidRenderer.renderFluidBox(fluidStack.getRawFluid(), fluidStack.getAmount(),
                    (float)relativeInternalLowerCorner.x + 1 / 32f, (float)relativeInternalLowerCorner.y, (float)relativeInternalLowerCorner.z + 1 / 32f,
                    (float)relativeInternalUpperCorner.x - 1 / 32f, relativeFluidLevel, (float)relativeInternalUpperCorner.z - 1 / 32f,
                    bufferSource, ms, light, true, true, fluidStack.getTag());
        };
        FluidStack gasStack = MixtureFluid.gasOf(controller.getGasTankContents());
        if (!gasStack.isEmpty()) {
            FluidRenderer.renderFluidBox(gasStack.getRawFluid(), gasStack.getAmount(),
                    (float)relativeInternalLowerCorner.x + 1 / 32f, relativeFluidLevel, (float)relativeInternalLowerCorner.z + 1 / 32f,
                    (float)relativeInternalUpperCorner.x - 1 / 32f, (float)relativeInternalUpperCorner.y, (float)relativeInternalUpperCorner.z - 1 / 32f,
                    bufferSource, ms, light, true, true, gasStack.getTag());
        };
    };

    protected void renderItem(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, mc.level, 0);
	};

    @Override
    public boolean shouldRenderOffScreen(@NotNull VatControllerBlockEntity controller) {
        return true;
    };
    
};
