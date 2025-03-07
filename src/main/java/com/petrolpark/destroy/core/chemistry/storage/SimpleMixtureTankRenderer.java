package com.petrolpark.destroy.core.chemistry.storage;

import net.createmod.catnip.data.Couple;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.RenderTypes;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.fluids.FluidStack;

public class SimpleMixtureTankRenderer<T extends SimpleMixtureTankBlockEntity> extends SafeBlockEntityRenderer<T> {

    public SimpleMixtureTankRenderer(BlockEntityRendererProvider.Context context) {};

    @Override
    protected void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        render(be, null, partialTicks, ms, bufferSource, light, overlay);
    };

    public static <C> void render(ISimpleMixtureTankRenderInformation<C> renderInfo, C container, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        FluidStack fs = renderInfo.getRenderedFluid(container);
        if (fs.isEmpty()) return;
        Vector3f l = renderInfo.getFluidBoxDimensions().getFirst();
        Vector3f u = renderInfo.getFluidBoxDimensions().getSecond();
        FluidRenderer.renderFluidBox(fs.getFluid(), fs.getAmount(), l.x / 16f, l.y / 16f, l.z / 16f, u.x / 16f, (l.y + (u.y - l.y) * renderInfo.getFluidLevel(container, partialTicks)) / 16f, u.z / 16f, bufferSource.getBuffer(RenderTypes.entityTranslucentBlockMipped()), ms, light, true, true);
    };

    public static interface ISimpleMixtureTankRenderInformation<C> {

        public Couple<Vector3f> getFluidBoxDimensions();

        public FluidStack getRenderedFluid(C container);

        public float getFluidLevel(C container, float partialTicks);

    };
    
};
