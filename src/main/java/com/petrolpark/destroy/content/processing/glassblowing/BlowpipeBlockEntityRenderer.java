package com.petrolpark.destroy.content.processing.glassblowing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.client.DestroyFluidRenderer;
import com.petrolpark.destroy.client.DestroyItemDisplayContexts;
import com.petrolpark.destroy.content.processing.glassblowing.GlassblowingRecipe.BlowingShape;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraftforge.fluids.FluidStack;

public class BlowpipeBlockEntityRenderer extends SafeBlockEntityRenderer<BlowpipeBlockEntity> {

    GlassblowingRecipe testRecipe;

    public BlowpipeBlockEntityRenderer(Context context) {

    };

    @Override
    protected void renderSafe(BlowpipeBlockEntity blowpipe, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if (blowpipe.getRecipe() == null) return;
        
        float progress = blowpipe.progressLastTick;
        if (blowpipe.progress != blowpipe.progressLastTick) progress += partialTicks;
        progress /= (float)BlowpipeBlockEntity.BLOWING_DURATION;

        ms.pushPose();
        TransformStack.of(ms)
        .center()
            .rotateToFace(blowpipe.getBlockState().getValue(BlowpipeBlock.FACING).getOpposite())
            .translate(0f, - 2 / 16f, 0.5f);
        ms.translate(0f, INITIAL_BLOB_LENGTH / 2f, 0f);
        render(blowpipe.getRecipe(), blowpipe.getFluid(), progress, ms, bufferSource, light, overlay);
        ms.popPose();
    };

    private static final float INITIAL_BLOB_LENGTH = 4 / 16f;
    private static final float INITIAL_BLOB_RADIUS = 2 / 16f;

    public static void render(GlassblowingRecipe recipe, FluidStack fluid, float progress, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (fluid.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        int shapes = recipe.blowingShapes.size();
        float startingShapeLength = INITIAL_BLOB_LENGTH / shapes;

        float spline;
        float fluidOpacity;
        if (progress > BlowpipeBlockEntity.BLOWING_TIME_PROPORTION) {
            spline = 1f;
            fluidOpacity = (1f - progress) / (1f - BlowpipeBlockEntity.BLOWING_TIME_PROPORTION);
            mc.getItemRenderer().renderStatic(recipe.getResultItem(mc.level.registryAccess()), DestroyItemDisplayContexts.BLOWPIPE, light, overlay, ms, buffer, mc.level, 0);
        } else {
            float blowingProgress = progress / BlowpipeBlockEntity.BLOWING_TIME_PROPORTION;
            spline = 3 * blowingProgress * blowingProgress - 2 * blowingProgress * blowingProgress * blowingProgress; // Smooth it out brother
            fluidOpacity = 1f;
        };
        
        float[] ends = new float[shapes + 1];
        float[] radii = new float[shapes];

        ends[0] = 0f;
        int i = 0;
        float totalLength = 0f;
        for (BlowingShape shape : recipe.blowingShapes) {
            radii[i] = INITIAL_BLOB_RADIUS + (shape.radius() - INITIAL_BLOB_RADIUS) * spline;
            ends[i + 1] = startingShapeLength * (i + 1) + (totalLength + shape.length() - startingShapeLength * (i + 1)) * spline;
            totalLength += shape.length();
            i++;
        };

        for (int j = 0; j < shapes; j++) {
            DestroyFluidRenderer.renderFluidBoxWithAlpha(fluid, - radii[j] - 1/ 512f, -radii[j] - 1/ 128f, ends[j] - 1/ 512f, radii[j] + 1 / 512f, radii[j] + 1 / 128f, ends[j+1] + 1 / 512f, buffer, ms, light, fluidOpacity);
        };
    };
    
};
