package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class AnimatedTreeTap extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(112.5f));
		int scale = 23;

		blockElement(DestroyPartials.TREE_TAP_ARM)
			.rotateBlock(9f * Mth.sin(AnimationTickHolder.getRenderTime() / 4f), 0, 0)
            .withRotationOffset(new Vec3(0, -4 / 16f, -1 / 16f))
            .atLocal(0f, - 12 / 16f, 7 / 16f)
			.scale(scale)
			.render(graphics);

        blockElement(shaft(Direction.Axis.X))
            .rotateBlock(getCurrentAngle(), 0, 0)
            .scale(scale)
            .render(graphics);

		blockElement(DestroyBlocks.TREE_TAP.getDefaultState())
			.atLocal(0, 0, 0)
            .rotate(0, 180, 0)
            .withRotationOffset(new Vec3(0.5, 0.5, 0.5))
			.scale(scale)
			.render(graphics);

		matrixStack.popPose();
        
    };
    
};
