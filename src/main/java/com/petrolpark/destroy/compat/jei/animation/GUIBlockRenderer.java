package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.minecraft.world.level.block.state.BlockState;

public class GUIBlockRenderer extends AnimatedKinetics {

    /**
     * Render the given Block State.
     * @param blockState
     * @param scale The relative size of the Block
     */
    public void renderBlock(BlockState blockState, PoseStack stack, double scale) {
        stack.pushPose();
        stack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		stack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
        blockElement(blockState)
            .atLocal(0, 0, 0)
            .scale(scale)
            .render(stack);
        stack.popPose();
    };

    @Override
    public void draw(PoseStack matrixStack, int xOffset, int yOffset) {};
    
};
