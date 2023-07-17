package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.minecraft.client.gui.GuiGraphics;

public class AnimatedDynamo extends AnimatedKinetics {

	private boolean basin;

	public AnimatedDynamo(boolean basin) {
		this.basin = basin;
	};

   	@Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 23;

		blockElement(DestroyPartials.DYNAMO_COG)
			.rotateBlock(0, getCurrentAngle() * 2, 0)
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(graphics);

		blockElement(DestroyBlocks.DYNAMO.getDefaultState())
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(graphics);

		if (basin) {
			blockElement(AllBlocks.BASIN.getDefaultState())
				.atLocal(0, 1.65, 0)
				.scale(scale)
				.render(graphics);
		};

		matrixStack.popPose();
    };
    
}
