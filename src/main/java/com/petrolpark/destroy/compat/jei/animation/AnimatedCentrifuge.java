package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

public class AnimatedCentrifuge extends AnimatedKinetics {

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
        matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(112.5f));
		int scale = 23;

		blockElement(DestroyPartials.CENTRIFUGE_COG)
			.rotateBlock(0, getCurrentAngle() * 2, 0)
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(matrixStack);

		blockElement(DestroyBlocks.CENTRIFUGE.getDefaultState())
			.atLocal(0, 0, 0)
			.scale(scale)
			.render(matrixStack);

		matrixStack.popPose();
        
    };
    
}
