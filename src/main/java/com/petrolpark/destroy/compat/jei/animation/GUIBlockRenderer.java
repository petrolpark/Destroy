package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;

public class GUIBlockRenderer extends AnimatedKinetics {

    /**
     * Render the given Block State.
     * @param blockState
     * @param scale The relative size of the Block
     */
    public void renderBlock(BlockState blockState, GuiGraphics graphics, double scale) {
        PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		stack.mulPose(Axis.YP.rotationDegrees(22.5f));
        blockElement(blockState)
            .atLocal(0, 0, 0)
            .scale(scale)
            .render(graphics);
        stack.popPose();
    };

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {}
    
};
