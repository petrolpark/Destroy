package com.petrolpark.destroy.content.tool.syringe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SyringeItemRenderer extends CustomRenderedItemModelRenderer {

    static float MOVE_TO_CENTRE_TIME = 0.25f;
    static float PAUSE_TIME = 0.6f;
    static float STAB_TIME = 0.75f;

    @Override
    @SuppressWarnings("resource")
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        LocalPlayer player = Minecraft.getInstance().player; // It thinks this is unclosed; IDK how to fix that
        if (player == null) return;
        float partialTicks = AnimationTickHolder.getPartialTicks();

        boolean isLeftHandAnimation = (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        boolean isFirstPersonAnimation = (isLeftHandAnimation || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);

        int modifier = isLeftHandAnimation ? 1 : -1; //used to reverse direction of animation

        float X = 0.1f;
        float Y = 0.4f;
        float Z = 0.9f;

        ms.pushPose();

        if (isFirstPersonAnimation && stack.getOrCreateTag().contains("Injecting")) {

            float time = (float) player.getUseItemRemainingTicks() - partialTicks + 1.0F;
            float progress = (stack.getUseDuration() - time) / stack.getUseDuration();
            
            if (progress < MOVE_TO_CENTRE_TIME) { //gradually move to center of screen

                ms.translate(X * modifier * progress / MOVE_TO_CENTRE_TIME, Y * progress / MOVE_TO_CENTRE_TIME, Z * progress / MOVE_TO_CENTRE_TIME);
                ms.mulPose(Axis.YP.rotationDegrees(120 * modifier * progress / MOVE_TO_CENTRE_TIME));
                ms.mulPose(Axis.ZP.rotationDegrees(-70 * modifier * progress / MOVE_TO_CENTRE_TIME));

            } else { //hold at center of screen

                ms.translate(X * modifier, Y, Z);
                ms.mulPose(Axis.YP.rotationDegrees(120 * modifier));
                ms.mulPose(Axis.ZP.rotationDegrees(-70 * modifier));

            };

            if (progress > PAUSE_TIME && progress <= STAB_TIME) { //rapidly move towards arm

                float stabbingProgress = (progress - PAUSE_TIME) / (STAB_TIME - PAUSE_TIME);
                ms.translate(0f, -0.8f * stabbingProgress, 0f);

            } else if (progress > STAB_TIME) { //hold in arm

                ms.translate(0f, -0.8f, 0f);

            };
        };

        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());

        ms.popPose();
    };
};
