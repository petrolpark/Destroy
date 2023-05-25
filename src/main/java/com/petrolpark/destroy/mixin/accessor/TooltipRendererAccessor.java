package com.petrolpark.destroy.mixin.accessor;

import mezz.jei.common.gui.TooltipRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(TooltipRenderer.class)
public interface TooltipRendererAccessor {

    @Invoker("drawHoveringText")
    public static void invokeDrawHoveringText(PoseStack poseStack, List<Component> textLines, int x, int y, ItemStack itemStack, Font font) {};
};
