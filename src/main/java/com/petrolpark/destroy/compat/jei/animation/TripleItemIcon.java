package com.petrolpark.destroy.compat.jei.animation;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class TripleItemIcon extends DoubleItemIcon {

    private final Supplier<ItemStack> tertiarySupplier;
    private ItemStack tertiaryStack;

	public TripleItemIcon(Supplier<ItemStack> primary, Supplier<ItemStack> secondary, Supplier<ItemStack> tertiary) {
		super(primary, secondary);
        this.tertiarySupplier = tertiary;
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
		if (tertiaryStack == null) {
            tertiaryStack = tertiarySupplier.get();
		};
        super.draw(graphics, xOffset, yOffset);

        PoseStack ms = graphics.pose();
		ms.pushPose();
		ms.translate(xOffset - 4, yOffset + 10, 100);
		ms.scale(0.5f, 0.5f, 0.5f);
		GuiGameElement.of(tertiaryStack)
			.render(graphics);
		ms.popPose();
	}
};
