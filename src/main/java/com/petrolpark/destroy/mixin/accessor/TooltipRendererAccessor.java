package com.petrolpark.destroy.mixin.accessor;

import mezz.jei.common.gui.TooltipRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TooltipRenderer.class)
public interface TooltipRendererAccessor {

    @Invoker(
        value = "drawHoveringText",
        remap = false
    )
    public static void invokeDrawHoveringText(GuiGraphics graphics, List<Component> textLines, int x, int y, ItemStack itemStack, Font font) {};
};
