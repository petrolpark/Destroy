package com.petrolpark.destroy.content.tool.swissarmyknife;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.content.tool.swissarmyknife.SwissArmyKnifeItem.Tool;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SwissArmyKnifeItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {

            boolean firstPerson = (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND);

            Map<Tool, LerpedFloat> chasers = SwissArmyKnifeItem.getChasers(stack);

            ms.pushPose();
            if (!firstPerson) {
                ms.scale(0.5f, 0.5f, 0.5f);
                ms.translate(-3 / 10f, - 5 / 10f, 0f);
                TransformStack.of(ms).rotateZDegrees(-90f);
            } else {
                ms.scale(0.6f, 0.6f, 0.6f);
                ms.translate(0f, -6 / 16f, 3 / 16f);
                TransformStack.of(ms).rotateZDegrees(-45f);
            };
            ms.translate(0f, 0f, -0.175f);
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render first casing

            // Render all tools
            ms.pushPose();
            ms.translate(-10 / 16f, 10 / 16f, 0f);

            for (RenderedTool tool : RenderedTool.ORDERED_TOOLS) {
                ms.translate(0f, 0f, 0.05f);
                ItemStack renderedTool = tool.getRenderedItemStack((SwissArmyKnifeItem)stack.getItem());
                LerpedFloat toolAngle = chasers.get(tool.tool);

                ms.pushPose();
                if (toolAngle != null) {
                    TransformStack.of(ms)
                        .translate(5 / 16f, -5 / 16f, 0f)
                        .rotateZDegrees(179 * (1 - toolAngle.getValue()) * (tool == RenderedTool.LOWER_SHEARS ? 1f : -1f))
                        .translateBack(5 / 16f, -5 / 16f, 0f);
                };
                itemRenderer.renderStatic(renderedTool, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, ms, buffer, mc.level, 0);
                ms.popPose();
            };
            ms.popPose();

            ms.translate(0f, 0f, 0.35f);
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render other casing

            ms.popPose();
        } else {
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
        };

    };

    public static enum RenderedTool {
        CASING(Tool.PICKAXE), // The 'Tool.PICKAXE' is not used here
        PICKAXE(Tool.PICKAXE),
        SHOVEL(Tool.SHOVEL),
        AXE(Tool.AXE),
        HOE(Tool.HOE),
        UPPER_SHEARS(Tool.SHEARS),
        LOWER_SHEARS(Tool.SHEARS);

        private static final List<RenderedTool> ORDERED_TOOLS = List.of(PICKAXE, AXE, LOWER_SHEARS, UPPER_SHEARS, SHOVEL, HOE);

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
