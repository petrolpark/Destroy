package com.petrolpark.destroy.content.processing.ageing;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyTags.Items;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;

import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.platform.ForgeCatnipServices;
import net.createmod.catnip.render.FluidRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
 
public class AgeingBarrelRenderer extends SmartBlockEntityRenderer<AgeingBarrelBlockEntity> {

    private static float minY = 2 / 16f;

    public AgeingBarrelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    };

    @Override
    protected void renderSafe(AgeingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(barrel, partialTicks, ms, buffer, light, overlay);

        if (!barrel.getBlockState().getValue(AgingBarrelBlock.IS_OPEN)) return;

        //render Fluid
        float fluidLevel = renderFluid(barrel, partialTicks, ms, buffer, light, overlay);
 
        //render Items
        ms.pushPose();
        ms.translate(0.5f, 0.0f, 0.5f);
        IItemHandlerModifiable inv = barrel.itemCapability.orElse(new ItemStackHandler());
        List<ItemStack> itemStacks = new ArrayList<>();
        boolean renderYeast = false;
        //count all the Items
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            if (!inv.getStackInSlot(slot).isEmpty()) {
                if (inv.getStackInSlot(slot).is(Items.YEAST.tag) && renderYeast == false) { //if both Items are Yeast, only do the fancy render for one
                    renderYeast = true;
                } else {
                    itemStacks.add(inv.getStackInSlot(slot));
                };
            };
        };
        float anglePartition = 360f / itemStacks.size();
        //render each Item
        for (int i = itemStacks.size(); i > 0; i--) {
            if (itemStacks.get(i - 1).isEmpty()) continue;

            ms.pushPose();

            //bobbing
            if (fluidLevel != minY) {
                ms.translate(0, (Mth.sin(AnimationTickHolder.getRenderTime(barrel.getLevel()) / 12f + anglePartition * i) + 1.5f) * 1 / 32f, 0);
            };

            //spacing the Items out if there are multiple
            Vec3 itemPosition = VecHelper.rotate(new Vec3(itemStacks.size() == 1 ? 0f : 0.1f, Mth.clamp(fluidLevel - 0.05f, 0.125f, 0.8f), 0f), anglePartition * i, Axis.Y);
			ms.translate(itemPosition.x, itemPosition.y, itemPosition.z);
            TransformStack.of(ms)
				.rotateYDegrees(anglePartition * i + 35)
				.rotateXDegrees(65);
            renderItem(barrel, partialTicks, ms, buffer, light, overlay, itemStacks.get(i - 1));
            ms.popPose(); 
        };

        ms.popPose();

        // Render Yeast
        ms.pushPose();
        if (renderYeast) {
            // I know it says FluidRenderer but I'm just using it to render a generic texture, sue me
            FluidRenderHelper.renderStillTiledFace(Direction.UP, 2 / 16f, 2 / 16f, 14 / 16f, 14 / 16f, fluidLevel + 0.01f, FluidRenderHelper.getFluidBuilder(buffer), ms, light, 0xFFFFFFFF,
                Minecraft.getInstance()
			    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(Destroy.asResource("block/yeast_overlay"))
            );
        };
        ms.popPose();
    };

    protected void renderItem(AgeingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        if (stack.isEmpty()) return;
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, barrel.getLevel(), 0);
    };

    protected float renderFluid(AgeingBarrelBlockEntity barrel, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        TankSegment tank = barrel.getTankToRender();
        float units = tank.getTotalUnits(partialTicks);
        float maxY = minY + (Mth.clamp(units / barrel.getTank().getCapacity(), 0, 1) * 8 / 12f);
        if (units < 1 || tank.getRenderedFluid().isEmpty()) return minY;
        ForgeCatnipServices.FLUID_RENDERER.renderFluidBox(tank.getRenderedFluid(), 2 / 16f, minY, 2 / 16f, 14 / 16f, maxY, 14 / 16f, buffer, ms, light, false, true);
        return maxY;
    };

    @Override
	public int getViewDistance() {
		return 16;
	};
    
}
