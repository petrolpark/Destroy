package com.petrolpark.destroy.compat.jei.animation;

import static com.mojang.math.Constants.PI;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlock;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlockEntityRenderer;
import com.petrolpark.destroy.content.processing.glassblowing.GlassblowingRecipe;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

public class AnimatedBlowpipe extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        //NOOP
    };

    public void draw(GlassblowingRecipe recipe, FluidStack fluid, GuiGraphics graphics) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0, 0, 200);
		ms.mulPose(Axis.XP.rotationDegrees(-37.5f));
		ms.mulPose(Axis.YP.rotationDegrees(247.5f));
        ms.scale(23f, 23f, 23f);
        ms.pushPose();

        blockElement(DestroyBlocks.BLOWPIPE.getDefaultState().setValue(BlowpipeBlock.FACING, Direction.NORTH))
            .render(graphics);
        TransformStack.of(ms)
            .rotateY(PI)
            .translate(-0.5d, -0.5d, 0d);
        ms.pushPose();
        UIRenderHelper.flipForGuiRender(ms);
        RenderSystem.disableDepthTest();
        BlowpipeBlockEntityRenderer.render(recipe, fluid, Math.min((AnimationTickHolder.getRenderTime() % 120f) / 100f, 1f), ms, graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        RenderSystem.enableDepthTest();
        ms.popPose();

        ms.popPose();
        ms.popPose();
    };
    
};
