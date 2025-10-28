package com.petrolpark.destroy.content.oil.seismology;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph.Mark;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class SeismographItemRenderer extends CustomRenderedItemModelRenderer {

    public static final RenderType BACKGROUND = DestroyGuiTextures.SEISMOGRAPH_BACKGROUND.asTextRenderType();
    public static final RenderType OVERLAY = DestroyGuiTextures.SEISMOGRAPH_OVERLAY.asTextRenderType();
    public static final RenderType TICK = DestroyGuiTextures.SEISMOGRAPH_TICK.asTextRenderType();
    public static final RenderType CROSS = DestroyGuiTextures.SEISMOGRAPH_CROSS.asTextRenderType();
    public static final RenderType GUESSED_TICK = DestroyGuiTextures.SEISMOGRAPH_GUESSED_TICK.asTextRenderType();
    public static final RenderType GUESSED_CROSS = DestroyGuiTextures.SEISMOGRAPH_GUESSED_CROSS.asTextRenderType();

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemRenderer itemRenderer = mc.getItemRenderer();
        ItemInHandRenderer handItemRenderer = mc.getEntityRenderDispatcher().getItemInHandRenderer();
        float partialTicks = AnimationTickHolder.getPartialTicks();

        if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            if (mc.screen instanceof SeismographScreen) return; // Don't render if it's already open in GUI form
            
            // Logic replicated from ItemInHandRenderer
            InteractionHand swingingHand = player.swingingArm;
            HumanoidArm arm = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            InteractionHand hand = arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (swingingHand == null) swingingHand = InteractionHand.MAIN_HAND;
            float equippedProgress = 1f - (hand == InteractionHand.MAIN_HAND ? Mth.lerp(partialTicks, handItemRenderer.oMainHandHeight, handItemRenderer.mainHandHeight) : Mth.lerp(partialTicks, handItemRenderer.oOffHandHeight, handItemRenderer.offHandHeight));
            float swingProgress = swingingHand == hand ? player.getAttackAnim(partialTicks) : 0f;

            // Undo the Item Stack model transforms
            ms.popPose();
            ms.popPose();
            ms.popPose();
            renderOneHandedSeismograph(ms, buffer, light, equippedProgress, arm, swingProgress, stack, mc, handItemRenderer);
            ms.pushPose();
            ms.pushPose();
            ms.pushPose();
        } else {
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel());
        };
    };

    /**
     * Largely copied from the {@link net.minecraft.client.renderer.ItemInHandRenderer#renderOneHandedMap Minecraft source code}.
     */
    public static void renderOneHandedSeismograph(PoseStack ms, MultiBufferSource buffer, int light, float equippedProgress, HumanoidArm hand, float swingProgress, ItemStack stack, Minecraft mc, ItemInHandRenderer itemRenderer) {
        
        // Copied from vanilla - hand
        ms.pushPose();
        float handRotation = hand == HumanoidArm.RIGHT ? 1f : -1f;
        ms.translate(handRotation * 0.125f, -0.125f, 0f);
        if (!mc.player.isInvisible()) {
            ms.pushPose();
            ms.mulPose(Axis.ZP.rotationDegrees(10f));
            itemRenderer.renderPlayerArm(ms, buffer, light, equippedProgress, swingProgress, hand);
            ms.popPose();
        };

        // Copied from vanilla - transformation for map
        ms.translate(handRotation * 0.51f, -0.08f + equippedProgress * -1.2f, -0.75f);
        float sqrtSwing = Mth.sqrt(swingProgress);
        float swingAngle = Mth.sin(sqrtSwing * (float)Math.PI);
        ms.translate(handRotation * -0.5f * swingAngle, 0.4f * Mth.sin(sqrtSwing * ((float)Math.PI * 2f)) - 0.3f * swingAngle, -0.3f * Mth.sin(swingProgress * (float)Math.PI));
        ms.mulPose(Axis.XP.rotationDegrees(swingAngle * -45f));
        ms.mulPose(Axis.YP.rotationDegrees(sqrtSwing * swingAngle * -30f));
        ms.mulPose(Axis.YP.rotationDegrees(180f));
        ms.mulPose(Axis.ZP.rotationDegrees(180f));
        ms.scale(0.38f, 0.38f, 0.38f);
        ms.translate(-0.5f, -0.5f, 0f);

        // Scale to the size of the vanilla map
        ms.scale(1 / 128f, 1 / 128f, 1 / 128f);
        ms.translate(-7f, -7f, 0f);
        ms.scale(142 / 64f, 142 / 64f, 1f);

        // Get relevant data
        Integer mapId = SeismographItem.getMapId(stack);
        MapItemSavedData mapData = SeismographItem.getSavedData(mapId, mc.level);
        Seismograph seismograph = SeismographItem.readSeismograph(stack);

        // Render as normal
        renderSeismograph(ms, buffer, light, mapId, mapData, seismograph, mc, (t, x, y) -> t.render(ms, x, y), true);

        ms.popPose();
    };

    public static final DestroyGuiTextures[] numberSymbols = new DestroyGuiTextures[]{DestroyGuiTextures.SEISMOGRAPH_1, DestroyGuiTextures.SEISMOGRAPH_1, DestroyGuiTextures.SEISMOGRAPH_2, DestroyGuiTextures.SEISMOGRAPH_3, DestroyGuiTextures.SEISMOGRAPH_4, DestroyGuiTextures.SEISMOGRAPH_5, DestroyGuiTextures.SEISMOGRAPH_6, DestroyGuiTextures.SEISMOGRAPH_7, DestroyGuiTextures.SEISMOGRAPH_8};

    @FunctionalInterface
    public static interface SeismographGuiTextureRenderer {
        public void render(DestroyGuiTextures texture, float x, float y);
    };
    
    public static void renderSeismograph(PoseStack ms, MultiBufferSource buffer, int light, Integer mapId, MapItemSavedData mapData, Seismograph seismograph, Minecraft mc, SeismographGuiTextureRenderer renderer, boolean invertZ) {
        ms.pushPose(); 

        float zm = invertZ ? -2f : 1f;

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();

        // Background
        renderer.render(DestroyGuiTextures.SEISMOGRAPH_BACKGROUND, 0f, 0f);
    
        // Marks
        ms.pushPose();
        ms.translate(13f, 13f, 0.02f * zm);
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                Mark mark = seismograph.getMark(x, z);
                if (mark != Mark.NONE) renderer.render(mark.icon, x * 6f, z * 6f);
            };
        };
        ms.popPose();

        // Rows
        ms.pushPose();;
        ms.translate(8f, 13f, 0.03f * zm);
        for (int z = 0; z < 8; z++) {
            ms.pushPose();
            ms.translate(0f, z * 6f, 0f);
            if (seismograph.isRowDiscovered(z)) {
                int[] numbers = seismograph.getRowDisplayed(z);
                for (int i = numbers.length - 1; i >= 0; i--) {
                    if (numbers[i] != 0) {
                        renderer.render(numberSymbols[numbers[i]], 0f, 0f);
                        ms.translate((numbers[i] <= 2) ? -2f : -3f, 0f, 0f);
                    };
                };
            } else {
                renderer.render(DestroyGuiTextures.SEISMOGRAPH_UNKNOWN, 0f, 0f);
            };
            ms.popPose();
        };
        ms.popPose();

        // Columns
        ms.pushPose();;
        ms.translate(18f, 8f, 0.03f * zm);
        TransformStack.of(ms)
            .rotateZDegrees(90);
        for (int x = 0; x < 8; x++) {
            ms.pushPose();
            ms.translate(0f, x * -6f, 0f);
            if (seismograph.isColumnDiscovered(x)) {
                int[] numbers = seismograph.getColumnDisplayed(x);
                for (int i = numbers.length - 1; i >= 0; i--) {
                    if (numbers[i] != 0) {
                        renderer.render(numberSymbols[numbers[i]], 0f, 0f);
                        ms.translate((numbers[i] <= 2) ? -2f : -3f, 0f, 0f);
                    };
                };
            } else {
                renderer.render(DestroyGuiTextures.SEISMOGRAPH_UNKNOWN, 0f, 0f);
            };
            ms.popPose();
        };
        ms.popPose();

        // Map colors
        ms.pushPose();
        ms.translate(13f, 13f,  0f);
        ms.scale(47 / 128f, 47 / 128f, 1f);
        ms.translate(0f, 0f, 0.01f * zm);
        if (mapId != null && mapData != null) mc.gameRenderer.getMapRenderer().render(ms, buffer, mapId, mapData, false, light);
        ms.popPose();

        // Overlay
        ms.pushPose();
        ms.translate(0f, 0f, 0.04f * zm);
        renderer.render(DestroyGuiTextures.SEISMOGRAPH_OVERLAY, 0f, 0f);
        ms.popPose();
        ms.popPose();
    };

};
