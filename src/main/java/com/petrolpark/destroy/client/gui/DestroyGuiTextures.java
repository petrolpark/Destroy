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
    JEI_SHORT_DOWN_ARROW("jei/short_down_arrow", 18, 18),
    JEI_SHORT_RIGHT_ARROW("jei/short_right_arrow", 18, 18),
	JEI_TEXT_BOX_LONG("jei/text_box_long", 169, 19),
	JEI_TEXT_BOX_SHORT("jei/text_box_short", 115, 19),
	JEI_DISTILLATION_TOWER_BOTTOM("jei/distillation_tower_bottom", 12, 12),
	JEI_DISTILLATION_TOWER_MIDDLE("jei/distillation_tower_middle", 20, 12),
	JEI_DISTILLATION_TOWER_TOP("jei/distillation_tower_top", 12, 2),
	JEI_DISTILLATION_TOWER_BRANCH("jei/distillation_tower_branch", 20, 2);

    public final ResourceLocation location;
	public int width;
    public int height;

    private DestroyGuiTextures(String location, int width, int height) {
		this.location = Destroy.asResource("textures/gui/" + location + ".png");
		this.width = width;
		this.height = height;
	};

    @OnlyIn(Dist.CLIENT)
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, 0, 0, width, height);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, 0, 0, width, height);
	};
    
};
