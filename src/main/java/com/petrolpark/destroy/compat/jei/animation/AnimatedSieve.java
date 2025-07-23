package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.client.DestroyPartials;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AnimatedSieve extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
		ms.translate(xOffset, yOffset, 200);
		ms.mulPose(Axis.XP.rotationDegrees(-25.5f));
		ms.mulPose(Axis.YP.rotationDegrees(112.5f));
		ms.scale(23, 23, 23);

		blockElement(DestroyPartials.MECHANICAL_SIEVE_SHAFT)
            .rotateBlock(0, getCurrentAngle(), 90)
			.render(graphics);

        float offset = Mth.sin(getCurrentAngle() * Mth.PI / 180f) * 2 / 16f;
        blockElement(DestroyPartials.MECHANICAL_SIEVE)
            .rotateBlock(0, 90, 0)
            .atLocal(0f, 0f, offset)
            .render(graphics);

        blockElement(DestroyPartials.MECHANICAL_SIEVE_LINKAGES)
            .atLocal(0f, 0f, offset)
            .rotateBlock(getCurrentAngle(), 90, 0)
            .render(graphics);

		blockElement(DestroyBlocks.MECHANICAL_SIEVE.getDefaultState())
			.atLocal(0, 0, 0)
			.render(graphics);

		ms.popPose();
    };
    
};
