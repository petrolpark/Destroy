package com.petrolpark.destroy.item.renderer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class SeismometerItemRenderer extends CustomRenderedItemModelRenderer<SeismometerItemRenderer.SeismometerModel> {

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
    protected void render(ItemStack stack, SeismometerModel model, PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        int ticksThroughAnimation = AnimationTickHolder.getTicks(true) % 32;
        TransformStack msr = TransformStack.cast(ms);

        Minecraft mc = Minecraft.getInstance();
        boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;
        TransformType mainHand = rightHanded ? TransformType.FIRST_PERSON_RIGHT_HAND : TransformType.FIRST_PERSON_LEFT_HAND;
        TransformType offHand = rightHanded ? TransformType.FIRST_PERSON_LEFT_HAND : TransformType.FIRST_PERSON_RIGHT_HAND;
        boolean animate = false;

        int handModifier = transformType == TransformType.FIRST_PERSON_LEFT_HAND ? -1 : 1;

        boolean noControllerInMain = !DestroyItems.SEISMOMETER.isIn(mc.player.getMainHandItem()); // It thinks mc.player might be null

        ms.pushPose();

        if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
            msr.translate(0d, 1d / 4d, 1d / 4d * handModifier);
            msr.rotateY(-30 * handModifier);
            msr.rotateZ(-30);
            animate = true;
        };
        
        renderer.render(animate ? model.getPartial("base") : model.getOriginalModel(), light);

        if (!animate) {
            ms.popPose();
            return;
        };

        // Determine whether the next animation cycle should be level or have a spike
        if (spike == null || ticksThroughAnimation == 0) {
            spike = spikeNextPage > 0;
        };

        BakedModel pageModel = model.getPartial(spike ? "page_spike" : "page_level");

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
        renderer.render(model.getPartial("needle"), light);
        ms.popPose();
        ms.popPose();

        ms.popPose();
    };

    @Override
    public SeismometerModel createModel(BakedModel originalModel) {
        return new SeismometerModel(originalModel);
    };
    
    public static class SeismometerModel extends DestroyCustomRenderedItemModel {
        
        public SeismometerModel(BakedModel template){
            super(template, "seismometer");
            addPartials("base", "needle", "page_blank", "page_level", "page_spike");
        };
    }
};
