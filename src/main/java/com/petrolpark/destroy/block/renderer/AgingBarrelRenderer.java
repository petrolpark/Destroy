package com.petrolpark.destroy.block.renderer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.AgingBarrelBlockEntity;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AgingBarrelRenderer extends SmartTileEntityRenderer<AgingBarrelBlockEntity> {

    private static float minY = 2 / 16f;

    public AgingBarrelRenderer(Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(AgingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(barrel, partialTicks, ms, buffer, light, overlay);
        float fluidLevel = renderFluid(barrel, partialTicks, ms, buffer, light, overlay);
        ms.pushPose();
        ms.translate(8 / 16f, fluidLevel, 8 / 16f);
        renderItem(barrel, partialTicks, ms, buffer, light, overlay, barrel.inventory.getItem(0));
        if (fluidLevel == minY) { //if the barrel is empty of any Fluid
            TransformStack.cast(ms).rotateX(90);
        } else {
            
        };
        ms.popPose();
    };

    protected void renderItem(AgingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        if (stack.isEmpty()) return;
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.GROUND, light, overlay, ms, buffer, 0);
    };

    protected float renderFluid(AgingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        TankSegment tank = barrel.getTankToRender();
        float units = tank.getTotalUnits(partialTicks);
        float maxY = minY + (Mth.clamp(units / 1000f, 0, 1) * 8 / 12f);
        if (units < 1) return minY;
        FluidRenderer.renderFluidBox(tank.getRenderedFluid(), minY, 2 / 16f, 2 / 16f, 14 / 16f, maxY, 14 / 16f, buffer, ms, light, false);
        return maxY;
    };

    @Override
	public int getViewDistance() {
		return 16;
	};
    
}
