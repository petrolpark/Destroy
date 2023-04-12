package com.petrolpark.destroy.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.item.WithSecondaryItem;
import com.simibubi.create.foundation.item.render.CreateCustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class WithSecondaryItemRenderer extends CustomRenderedItemModelRenderer<WithSecondaryItemRenderer.WithSecondaryItemModel> {

    @Override
    protected void render(ItemStack stack, WithSecondaryItemRenderer.WithSecondaryItemModel model, PartialItemModelRenderer renderer, TransformType transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (transformType == TransformType.GUI && Screen.hasShiftDown()) {
            ItemStack secondaryStack = WithSecondaryItem.getSecondaryItem(stack);
            if (!stack.isEmpty()) {
                PoseStack localMs = new PoseStack();
                localMs.translate(1/ 4f, -1 / 4f, 1);
                localMs.scale(.5f, .5f, .5f);
                itemRenderer.renderStatic(secondaryStack, TransformType.GUI, light, OverlayTexture.NO_OVERLAY, localMs, buffer, 0);
			};
		}

        itemRenderer.render(stack, TransformType.NONE, false, ms, buffer, light, overlay, model.getOriginalModel()); // Render the Item normally
    };

    @Override
    public WithSecondaryItemModel createModel(BakedModel originalModel) {
        return new WithSecondaryItemModel(originalModel);
    };

    public static class WithSecondaryItemModel extends CreateCustomRenderedItemModel {
        public WithSecondaryItemModel(BakedModel template){
            super(template, "");
        };
    };


};
