package com.petrolpark.destroy.item.renderer;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.SwissArmyKnifeItem;
import com.petrolpark.destroy.item.SwissArmyKnifeItem.Tool;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SwissArmyKnifeRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {

            Map<Tool, LerpedFloat> chasers = SwissArmyKnifeItem.getChasers(stack);
            float partialTicks = AnimationTickHolder.getPartialTicks();

            ms.pushPose();
            ms.translate(0f,  - 4 / 16f, -0.27f);
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render first casing

            // Render all tools
            ms.pushPose();
            ms.translate(-10 / 16f, 10 / 16f, 0f);

            int inversion = 1;
            for (RenderedTool tool : RenderedTool.TOOLS) {
                ms.translate(0f, 0f, 0.06f);
                ItemStack renderedTool = tool.getRenderedItemStack((SwissArmyKnifeItem)stack.getItem());
                LerpedFloat toolAngle = chasers.get(tool.tool);

                ms.pushPose();
                if (tool != RenderedTool.CASING) {
                    TransformStack.cast(ms)
                        .translate(5 / 16f, -5 / 16f, 0f)
                        .rotateZ(180 * (1 - toolAngle.getValue(partialTicks)) * inversion)
                        .translateBack(5 / 16f, -5 / 16f, 0f);
                };
                itemRenderer.renderStatic(renderedTool, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, ms, buffer, mc.level, 0);
                ms.popPose();
                inversion *= -1; // Alternately flip each tool
            };
            ms.popPose();

            ms.translate(0f, 0f, 0.42f);
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render other casing

            ms.popPose();
        } else {
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
        };

    };

    public static enum RenderedTool {
        CASING(null),
        PICKAXE(Tool.PICKAXE),
        SHOVEL(Tool.SHOVEL),
        AXE(Tool.AXE),
        HOE(Tool.HOE),
        UPPER_SHEARS(Tool.SHEARS),
        LOWER_SHEARS(Tool.SHEARS);

        private static final List<RenderedTool> TOOLS = List.of(PICKAXE, AXE, LOWER_SHEARS, UPPER_SHEARS, SHOVEL, HOE);

        @Nullable
        public final Tool tool;

        RenderedTool(Tool tool) {
            this.tool = tool;
        };

        public ItemStack getRenderedItemStack(SwissArmyKnifeItem item) {
            ItemStack stack = new ItemStack(item, 1);
            stack.getOrCreateTag().putFloat("RenderedTool", ((float)ordinal() / 8f));
            return stack;
        };

        public static float getItemProperty(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            return stack.getOrCreateTag().getFloat("RenderedTool");
        };
    };


    
};
