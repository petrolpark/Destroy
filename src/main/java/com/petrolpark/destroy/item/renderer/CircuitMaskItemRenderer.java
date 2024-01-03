package com.petrolpark.destroy.item.renderer;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.CircuitMaskItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CircuitMaskItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        Minecraft mc = Minecraft.getInstance();

        if (transformType == ItemDisplayContext.FIXED) {
            if (stack.getOrCreateTag().contains("Flipped")) TransformStack.cast(ms).rotateY(180);
            ms.scale(1.98f, 1.98f, 1.98f);
        };

        ItemRenderer itemRenderer = mc.getItemRenderer();
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
        int pattern = (stack.getItem() instanceof CircuitMaskItem item ? item.getPattern(stack): 0);
        for (int i = 0; i < 16; i++) {
            if (CircuitMaskItem.isPunched(pattern, i)) continue;
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, CircuitMaskItem.models[i]);
        };

        ms.popPose();
    };
    
};
