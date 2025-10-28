package com.petrolpark.destroy.compat.jei.animation;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.drawable.IDrawable;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class ArcFurnaceIcon implements IDrawable {

    private final AnimatedDynamo dynamo = new AnimatedDynamo(false, true);
    private final Supplier<ItemStack> itemSupplier;
    private ItemStack stack = null;

    public ArcFurnaceIcon(Supplier<ItemStack> itemSupplier) {
        this.itemSupplier = itemSupplier;
    };

    @Override
    public int getWidth() {
        return 18;
    };

    @Override
    public int getHeight() {
        return 18;
    };

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        if (stack == null) stack = itemSupplier.get();

        PoseStack ms = graphics.pose();
		ms.pushPose();
        ms.translate(xOffset, yOffset, 200f);

        ms.pushPose();
        ms.translate(4.5f, 9f, 0f);
        ms.scale(0.3f, 0.3f, 0.3f);
        dynamo.draw(graphics);
        ms.popPose();

        ms.pushPose();
		ms.translate(10, 10, 100f);
		ms.scale(0.5f, 0.5f, 0.5f);
		GuiGameElement.of(stack)
			.render(graphics);
        ms.popPose();

		ms.popPose();
    };
    
};
