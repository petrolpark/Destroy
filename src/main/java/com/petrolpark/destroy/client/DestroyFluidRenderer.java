package com.petrolpark.destroy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

public class DestroyFluidRenderer {
    

    public static void renderFluidBoxWithAlpha(FluidStack fluidStack, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, MultiBufferSource buffer, PoseStack ms, int light, float alpha) {
        renderFluidBoxWithAlpha(fluidStack, xMin, yMin, zMin, xMax, yMax, zMax, FluidRenderer.getFluidBuilder(buffer), ms, light, alpha);
    };

    /**
     * Copied from {@link FluidRenderer#renderFluidBox(FluidStack, float, float, float, float, float, float, VertexConsumer, PoseStack, int, boolean) Create 0.5.1 source code}.
     * @param fluidStack
     * @param xMin
     * @param yMin
     * @param zMin
     * @param xMax
     * @param yMax
     * @param zMax
     * @param builder
     * @param ms
     * @param light
     * @param alpha
     */
    public static void renderFluidBoxWithAlpha(FluidStack fluidStack, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, VertexConsumer builder, PoseStack ms, int light, float alpha) {
		Fluid fluid = fluidStack.getFluid();
		IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
		FluidType fluidAttributes = fluid.getFluidType();
		TextureAtlasSprite fluidTexture = Minecraft.getInstance()
			.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
			.apply(clientFluid.getStillTexture(fluidStack));

		Color preAlphaColor = new Color(clientFluid.getTintColor(fluidStack));
        preAlphaColor = preAlphaColor.scaleAlpha(alpha);
        int color = preAlphaColor.getRGB();
		int blockLightIn = (light >> 4) & 0xF;
		int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
		light = (light & 0xF00000) | luminosity << 4;

		ms.pushPose();

		for (Direction side : Iterate.directions) {
			boolean positive = side.getAxisDirection() == AxisDirection.POSITIVE;
			if (side.getAxis().isHorizontal()) {
				if (side.getAxis() == Axis.X) {
					FluidRenderer.renderStillTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, ms, light, color, fluidTexture);
				} else {
					FluidRenderer.renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, ms, light, color, fluidTexture);
				};
			} else {
				FluidRenderer.renderStillTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, ms, light, color, fluidTexture);
			};
		};

		ms.popPose();
	};

	public static void renderFluidSquare(GuiGraphics graphics, int x, int y, FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
		TextureAtlasSprite fluidTexture = Minecraft.getInstance()
			.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
			.apply(clientFluid.getStillTexture(fluidStack));

		graphics.blit(x, y, 0, 16, 16, fluidTexture);
	};

	
};
