package com.petrolpark.destroy.content.processing.glassblowing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BlowpipeItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        CompoundTag tag = stack.getOrCreateTag();

        ms.pushPose();
        if (tag.getBoolean("Blowing")) {
            if (transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
                ms.popPose();
                return; // This is handled with a Render Layer
            } else if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                float movementProgress;
                int ticks = mc.player.getTicksUsingItem();
                if (ticks < BlowpipeItem.TIME_TO_MOVE_TO_MOUTH) {
                    movementProgress = ((float)ticks + partialTicks) / BlowpipeItem.TIME_TO_MOVE_TO_MOUTH;
                    movementProgress = 3 * movementProgress * movementProgress - 2 * movementProgress * movementProgress * movementProgress;
                } else {
                    movementProgress = 1f;
                };
                ms.translate(-9 / 16f * movementProgress, 3 / 16f * movementProgress, 0f);
            };
        };
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());

        GlassblowingRecipe recipe = BlowpipeBlockEntity.readRecipe(mc.level, tag);
        FluidTank tank = new FluidTank(BlowpipeBlockEntity.TANK_CAPACITY);
        tank.readFromNBT(tag.getCompound("Tank"));
        if (recipe == null) {
            ms.popPose();
            return;
        };

        int lastProgress = tag.getInt("LastProgress");
        float progressProportion = (float)lastProgress / BlowpipeBlockEntity.BLOWING_DURATION;
        if (tag.getInt("Progress") != lastProgress) progressProportion += partialTicks / BlowpipeBlockEntity.BLOWING_DURATION;

        if (transformType == ItemDisplayContext.GUI) {
            if (Screen.hasShiftDown()) {
                ms.popPose();
                ms.popPose();
                ms.popPose();
                ms.translate(-1 / 4f, -1 / 4f, 1);
                ms.scale(0.5f, 0.5f, 0.5f);
                itemRenderer.renderStatic(recipe.getRollableResults().get(0).getStack(), ItemDisplayContext.GUI, light, OverlayTexture.NO_OVERLAY, ms, buffer, mc.level, 0);
                ms.pushPose();
                ms.pushPose();
                ms.pushPose();
            };
        } else if (!tank.isEmpty()) {
            ms.pushPose();
            ms.translate(0f, 0f, -8 / 16f);
            TransformStack.of(ms)
                .rotateYDegrees(180);
            BlowpipeBlockEntityRenderer.render(recipe, tank.getFluid(), progressProportion, ms, buffer, light, overlay);
            ms.popPose();
        };


        ms.popPose();
    };
    
};
