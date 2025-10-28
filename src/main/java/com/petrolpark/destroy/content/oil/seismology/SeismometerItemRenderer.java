package com.petrolpark.destroy.content.oil.seismology;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SeismometerItemRenderer extends CustomRenderedItemModelRenderer {

    protected static final PartialModel UNANIMATED = PartialModel.of(Destroy.asResource("item/seismometer/item"));
    protected static final PartialModel BASE = PartialModel.of(Destroy.asResource("item/seismometer/base"));
    protected static final PartialModel NEEDLE = PartialModel.of(Destroy.asResource("item/seismometer/needle"));
    protected static final PartialModel PAGE_BLANK = PartialModel.of(Destroy.asResource("item/seismometer/page_blank"));
    protected static final PartialModel PAGE_LEVEL = PartialModel.of(Destroy.asResource("item/seismometer/page_level"));
    protected static final PartialModel PAGE_SPIKE = PartialModel.of(Destroy.asResource("item/seismometer/page_spike"));

    private Boolean spike;
    private static int spikeNextPage; // Whether the next page to be shown should have a spike on it
    private static LerpedFloat angle;

    static {
        angle = LerpedFloat.angular().startWithValue(0d);
        angle.chase(0f, 0.2f, LerpedFloat.Chaser.EXP);
    };

    public static void tick() {
        angle.tickChaser();
        if (spikeNextPage > 0) spikeNextPage--;
    };

    public static void spike() {
        spikeNextPage = 32;
    };

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        int ticksThroughAnimation = AnimationTickHolder.getTicks(true) % 32;
        PoseTransformStack msr = TransformStack.of(ms);

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
        ItemDisplayContext mainHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        ItemDisplayContext offHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        boolean animate = false;

        int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;

        boolean noControllerInMain = !DestroyItems.SEISMOMETER.isIn(player.getMainHandItem()); // It thinks mc.player might be null

        ms.pushPose();

        if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
            msr.translate(0d, 1d / 4d, 1d / 4d * handModifier);
            msr.rotateYDegrees(-30 * handModifier);
            msr.rotateZDegrees(-30);
            animate = true;
        };
        
        renderer.render(animate ? BASE.get() : model.getOriginalModel(), light);

        if (!animate) {
            ms.popPose();
            return;
        };

        // Determine whether the next animation cycle should be level or have a spike
        if (spike == null || ticksThroughAnimation == 0) {
            spike = spikeNextPage > 0;
        };

        BakedModel pageModel = spike ? PAGE_SPIKE.get() : PAGE_LEVEL.get();

        ms.pushPose();
        renderer.render(pageModel, light);
        ms.popPose();

        float angleToChase = 0f;
        if (spike) {
            if (ticksThroughAnimation < 8) {
                angleToChase = -30;
            } else if (ticksThroughAnimation < 16) {
                angleToChase = 30;
            } else if (ticksThroughAnimation < 24) {
                angleToChase = 10;
            } else {
                angleToChase = -10;
            };
        } else {
            angleToChase = ticksThroughAnimation % 16 < 8 ? 10 : -10;
        };
        angle.updateChaseTarget(angleToChase);

        ms.pushPose();
        ms.translate(0f, 0f, -5/16f);
        ms.pushPose();
        msr.rotateYDegrees(angle.getValue(partialTicks));
        renderer.render(NEEDLE.get(), light);
        ms.popPose();
        ms.popPose();
        ms.popPose();
    };
};
