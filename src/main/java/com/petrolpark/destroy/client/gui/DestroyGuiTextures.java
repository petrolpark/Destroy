package com.petrolpark.destroy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum DestroyGuiTextures implements ScreenElement {

    //JEI
    JEI_SHORT_DOWN_ARROW("jei/widgets", 0, 64, 18, 18),
    JEI_SHORT_RIGHT_ARROW("jei/widgets", 0, 82, 18, 16),
	JEI_EQUILIBRIUM_ARROW("jei/widgets", 0, 96, 42, 11),
	JEI_LINE("jei/widgets", 40, 38, 177, 2),
	JEI_TEXT_BOX_LONG("jei/widgets", 0, 0, 169, 19),
	JEI_TEXT_BOX_SHORT("jei/widgets", 0, 19, 115, 19),
	JEI_DISTILLATION_TOWER_BOTTOM("jei/widgets", 0, 52, 12, 12),
	JEI_DISTILLATION_TOWER_MIDDLE("jei/widgets", 0, 40, 20, 12),
	JEI_DISTILLATION_TOWER_TOP("jei/widgets", 0, 38, 12, 2),
	JEI_DISTILLATION_TOWER_BRANCH("jei/widgets", 20, 45, 20, 2),

	//MISC
	NERD_EMOJI("jei/widgets", 115, 19, 16, 14);

    public final ResourceLocation location;
	public int width, height, startX, startY;

    private DestroyGuiTextures(String location, int startX, int startY, int width, int height) {
		this.location = Destroy.asResource("textures/gui/" + location + ".png");
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	};

    @OnlyIn(Dist.CLIENT)
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
	};
    
};
