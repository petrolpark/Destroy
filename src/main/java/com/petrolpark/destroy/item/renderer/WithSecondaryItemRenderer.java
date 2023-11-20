package com.petrolpark.destroy.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.WithSecondaryItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class WithSecondaryItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();
        if (transformType == ItemDisplayContext.GUI && Screen.hasShiftDown()) {
            ItemStack secondaryStack = WithSecondaryItem.getSecondaryItem(stack);
            if (!stack.isEmpty()) {
                ms.pushPose();
                ms.translate(1/ 4f, -1 / 4f, 1);
                ms.scale(0.5f, 0.5f, 0.5f);
                itemRenderer.renderStatic(secondaryStack, ItemDisplayContext.GUI, light, OverlayTexture.NO_OVERLAY, ms, buffer, mc.level, 0);
                ms.popPose();
			};
		}

        itemRenderer.render(stack, ItemDisplayContext.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
    };
};
