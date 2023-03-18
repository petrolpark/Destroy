package com.petrolpark.destroy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum DestroyGuiTextures implements ScreenElement {

    //JEI
    JEI_SHORT_DOWN_ARROW("jei/short_down_arrow", 18, 18),
    JEI_SHORT_RIGHT_ARROW("jei/short_right_arrow", 18, 18);

    public final ResourceLocation location;
	public int width;
    public int height;

    private DestroyGuiTextures(String location, int width, int height) {
		this.location = Destroy.asResource("textures/gui/" + location + ".png");
		this.width = width;
		this.height = height;
	}

    @OnlyIn(Dist.CLIENT)
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(PoseStack ms, int x, int y) {
		bind();
		GuiComponent.blit(ms, x, y, 0, 0, 0, width, height, width, height);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack ms, int x, int y, GuiComponent component) {
		bind();
		component.blit(ms, x, y, 0, 0, width, height);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack ms, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(ms, c, x, y, 0, 0, width, height);
	}
    
}
