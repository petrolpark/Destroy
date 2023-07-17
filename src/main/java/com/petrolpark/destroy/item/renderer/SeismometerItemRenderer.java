package com.petrolpark.destroy.item.renderer;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SeismometerItemRenderer extends CustomRenderedItemModelRenderer {

    protected static final PartialModel UNANIMATED = new PartialModel(Destroy.asResource("item/seismometer/item"));
    protected static final PartialModel BASE = new PartialModel(Destroy.asResource("item/seismometer/base"));
    protected static final PartialModel NEEDLE = new PartialModel(Destroy.asResource("item/seismometer/needle"));
    protected static final PartialModel PAGE_BLANK = new PartialModel(Destroy.asResource("item/seismometer/page_blank"));
    protected static final PartialModel PAGE_LEVEL = new PartialModel(Destroy.asResource("item/seismometer/page_level"));
    protected static final PartialModel PAGE_SPIKE = new PartialModel(Destroy.asResource("item/seismometer/page_spike"));

    private Boolean spike;
    private static int spikeNextPage; // Whether the next page to be shown should have a spike on it
    private static LerpedFloat angle;

    static {
        angle = LerpedFloat.angular().startWithValue(0d);
        angle.chase(0f, 0.2f, Chaser.EXP);
    };

    public static void tick() {
        angle.tickChaser();
        if (spikeNextPage > 0) spikeNextPage--;
    };

    public static void spike() {
        spikeNextPage = 32;
    };

    @Override
    @SuppressWarnings("null")
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        int ticksThroughAnimation = AnimationTickHolder.getTicks(true) % 32;
        TransformStack msr = TransformStack.cast(ms);

        Minecraft mc = Minecraft.getInstance();
        boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
        ItemDisplayContext mainHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        ItemDisplayContext offHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        boolean animate = false;

        int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;

        boolean noControllerInMain = !DestroyItems.SEISMOMETER.isIn(mc.player.getMainHandItem()); // It thinks mc.player might be null

        ms.pushPose();

        if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
            msr.translate(0d, 1d / 4d, 1d / 4d * handModifier);
            msr.rotateY(-30 * handModifier);
            msr.rotateZ(-30);
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
        msr.rotateY(angle.getValue(partialTicks));
        renderer.render(NEEDLE.get(), light);
        ms.popPose();
        ms.popPose();

        ms.popPose();
    };
};
