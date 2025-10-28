package com.petrolpark.destroy.client;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.petrolpark.client.rendering.IGuiTexture;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;

import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum DestroyGuiTextures implements IGuiTexture {

	// Circuits
	CIRCUIT_BOARD_BORDER("circuit", 48, 48),
	CIRCUIT_BOARD_CELL_SHADING("circuit", 48, 0, 10, 10),
	CIRCUIT_BOARD_CELL("circuit", 48, 16, 8, 8),
	CIRCUIT_MASK_BORDER("circuit", 0, 48, 48, 48),
	CIRCUIT_MASK_CELL_SHADING("circuit", 48, 48, 10, 10),
	CIRCUIT_MASK_CELL("circuit", 48, 64, 8, 8),
	
	// Keypunch
	KEYPUNCH("keypunch", 187, 169),

	// Vat
	VAT("vat", 256, 226),
	VAT_CARD_UNSELECTED("vat", 0, 227, 100, 28),
	VAT_CARD_SELECTED("vat", 116, 226, 102, 30),
	VAT_CARD_ARROW("vat", 218, 226, 25, 30),
	VAT_SCROLL_DOT("vat", 100, 226, 7, 8),
	VAT_QUANTITY_OBSERVER("logistics", 0, 0, 256, 95),
	COLORIMETER("colorimeter", 0, 0, 256, 195),

	// Redstone Programmer
	REDSTONE_PROGRAMMER("redstone_programmer_1", 256, 226),
	REDSTONE_PROGRAMMER_NOTE_BORDER_MIDDLE("redstone_programmer_2", 192, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_LEFT("redstone_programmer_2", 196, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_LONE("redstone_programmer_2", 200, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_BORDER_RIGHT("redstone_programmer_2", 204, 0, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_0("redstone_programmer_2", 192, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_1("redstone_programmer_2", 196, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_2("redstone_programmer_2", 200, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_3("redstone_programmer_2", 204, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_4("redstone_programmer_2", 208, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_5("redstone_programmer_2", 212, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_6("redstone_programmer_2", 216, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_7("redstone_programmer_2", 220, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_8("redstone_programmer_2", 224, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_9("redstone_programmer_2", 228, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_10("redstone_programmer_2", 232, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_11("redstone_programmer_2", 236, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_12("redstone_programmer_2", 240, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_13("redstone_programmer_2", 244, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_14("redstone_programmer_2", 248, 18, 4, 18),
	REDSTONE_PROGRAMMER_NOTE_15("redstone_programmer_2", 252, 18, 4, 18),
	REDSTONE_PROGRAMMER_LINE("redstone_programmer_2", 179, 16, 2, 199),
	REDSTONE_PROGRAMMER_BARLINE("redstone_programmer_2", 181, 16, 2, 199),
	REDSTONE_PROGRAMMER_ITEM_SLOTS("redstone_programmer_2", 192, 36, 36, 18),
	REDSTONE_PROGRAMMER_DELETE_CHANNEL("redstone_programmer_2", 244, 36, 12, 18),
	REDSTONE_PROGRAMMER_MOVE_CHANNEL_UP("redstone_programmer_2", 228, 36, 12, 9),
	REDSTONE_PROGRAMMER_MOVE_CHANNEL_DOWN("redstone_programmer_2", 228, 45, 12, 9),
	REDSTONE_PROGRAMMER_PLAYHEAD("redstone_programmer_2", 185,16, 7, 199),
	REDSTONE_PROGRAMMER_REMOVE_BAR("redstone_programmer_2", 192, 54, 12, 13),
	REDSTONE_PROGRAMMER_ADD_BAR("redstone_programmer_2", 204, 54, 12, 13),

	// Seismograph
	SEISMOGRAPH_BACKGROUND("seismograph", 0, 0, 64, 64, 64, 64),
	SEISMOGRAPH_OVERLAY("seismograph_overlay", 0, 0, 64, 64, 64, 64),
	SEISMOGRAPH_TICK("seismograph_symbols", 0, 0, 5, 5, 64, 64),
	SEISMOGRAPH_CROSS("seismograph_symbols", 5, 0, 5, 5, 64, 64),
	SEISMOGRAPH_GUESSED_TICK("seismograph_symbols", 10, 0, 5, 5, 64, 64),
	SEISMOGRAPH_GUESSED_CROSS("seismograph_symbols", 15, 0, 5, 5, 64, 64),
	SEISMOGRAPH_1("seismograph_symbols", 0, 5, 3, 5, 64, 64),
	SEISMOGRAPH_2("seismograph_symbols", 3, 5, 3, 5, 64, 64),
	SEISMOGRAPH_3("seismograph_symbols", 6, 5, 3, 5, 64, 64),
	SEISMOGRAPH_4("seismograph_symbols", 9, 5, 3, 5, 64, 64),
	SEISMOGRAPH_5("seismograph_symbols", 12, 5, 3, 5, 64, 64),
	SEISMOGRAPH_6("seismograph_symbols", 15, 5, 3, 5, 64, 64),
	SEISMOGRAPH_7("seismograph_symbols", 18, 5, 3, 5, 64, 64),
	SEISMOGRAPH_8("seismograph_symbols", 21, 5, 3, 5, 64, 64),
	SEISMOGRAPH_UNKNOWN("seismograph_symbols", 0, 10, 3, 5, 64, 64),
	SEISMOGRAPH_HIGHLIGHT_ROW("seismograph_symbols", 0, 57, 57, 7, 64, 64),
	SEISMOGRAPH_HIGHLIGHT_COLUMMN("seismograph_symbols", 57, 0, 7, 57, 64, 64),
	SEISMOGRAPH_HIGHLIGHT_CROSS("seismograph_symbols", 0, 38, 19, 19, 64, 64),
	SEISMOGRAPH_HIGHLIGHT_CELL("seismograph_symbols", 0, 31, 7, 7, 64, 64),

	// Custom Explosive Mixtures
	CUSTOM_EXPLOSIVE_BACKGROUND("custom_explosive", 0, 0, 187, 135),
	CUSTOM_EXPLOSIVE_CHART("custom_explosive", 0, 135, 76, 76),
	CUSTOM_EXPLOSIVE_FULFILLED_LESS("custom_explosive", 187, 0, 7, 12),
	CUSTOM_EXPLOSIVE_FULFILLED_ZERO("custom_explosive", 194, 0, 12, 12),
	CUSTOM_EXPLOSIVE_FULFILLED_GREATER("custom_explosive", 206, 0, 7, 12),
	CUSTOM_EXPLOSIVE_UNFULFILLED_LESS("custom_explosive", 187, 12, 7, 12),
	CUSTOM_EXPLOSIVE_UNFULFILLED_ZERO("custom_explosive", 194, 12, 12, 12),
	CUSTOM_EXPLOSIVE_UNFULFILLED_GREATER("custom_explosive", 206, 12, 7, 12),
	CUSTOM_EXPLOSIVE_BAR("custom_explosive", 213, 0, 2, 4),
	CUSTOM_EXPLOSIVE_SLOT("custom_explosive", 187, 24, 18, 18),
	CUSTOM_EXPLOSIVE_JEI_BACKGROUND("custom_explosive", 76, 135, 90, 94),

	// Blowpipe Recipe selection
	BLOWPIPE_BACKGROUND("blowpipe", 0, 0, 95, 77),
	BLOWPIPE_RECIPE("blowpipe", 0, 77, 64, 18),
	BLOWPIPE_RECIPE_SELECTED("blowpipe", 0, 77 + 18, 64, 18),
	BLOWPIPE_SCROLL("blowpipe", 95, 0, 12, 15),
	BLOWPIPE_SCROLL_LOCKED("blowpipe", 95 + 12, 0, 12, 15),

	// Ponder
	THERMOMETER("ponder", 0, 0, 16, 32),

	// Misc - all moved to p library in 1.21.1
	@MoveToPetrolparkLibrary
	INVENTORY_BACKGROUND("inventory", 0, 0, 9, 9, 64, 64),
	INVENTORY_SLOT("inventory", 0, 22, 18, 18, 64, 64),
	HOTBAR_BACKGROUND("inventory", 9, 0, 22, 22, 64, 64),
	HOTBAR_SLOT("inventory", 31, 0, 20, 20, 64, 64),
	;

    public final ResourceLocation location;
	public final int width, height, startX, startY, textureWidth, textureHeight;

	private DestroyGuiTextures(String location, int width, int height) {
		this(location, 0, 0, width, height);	
	};

	private DestroyGuiTextures(String location, int startX, int startY, int width, int height) {
		this(location, startX, startY, width, height, 256, 256);
	};

    private DestroyGuiTextures(String location, int startX, int startY, int width, int height, int textureWidth, int textureHeight) {
		this.location = Destroy.asResource("textures/gui/" + location + ".png");
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	};

	private static final DestroyGuiTextures[] notes = new DestroyGuiTextures[]{REDSTONE_PROGRAMMER_NOTE_0, REDSTONE_PROGRAMMER_NOTE_1, REDSTONE_PROGRAMMER_NOTE_2, REDSTONE_PROGRAMMER_NOTE_3, REDSTONE_PROGRAMMER_NOTE_4, REDSTONE_PROGRAMMER_NOTE_5, REDSTONE_PROGRAMMER_NOTE_6, REDSTONE_PROGRAMMER_NOTE_7, REDSTONE_PROGRAMMER_NOTE_8, REDSTONE_PROGRAMMER_NOTE_9, REDSTONE_PROGRAMMER_NOTE_10, REDSTONE_PROGRAMMER_NOTE_11, REDSTONE_PROGRAMMER_NOTE_12, REDSTONE_PROGRAMMER_NOTE_13, REDSTONE_PROGRAMMER_NOTE_14, REDSTONE_PROGRAMMER_NOTE_15};

	public static DestroyGuiTextures getRedstoneProgrammerNote(int strength) {
		if (strength > 0 && strength <= 15) return notes[strength];
		return REDSTONE_PROGRAMMER_NOTE_0;
	};

	public RenderType asTextRenderType() {
		return RenderType.text(location);
	};

	@Override
	public ResourceLocation getLocation() {
		return location;
	};

	@Override
	public int getStartX() {
		return startX;
	};

	@Override
	public int getStartY() {
		return startY;
	};

	@Override
	public int getWidth() {
		return width;
	};

	@Override
	public int getHeight() {
		return height;
	};

	@Override
	public int getTextureWidth() {
		return textureWidth;
	};

	@Override
	public int getTextureHeight() {
		return textureHeight;
	};

    @OnlyIn(Dist.CLIENT)
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height, textureWidth, textureHeight);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(GuiGraphics graphics, int x, int y, Color c) {
		bind();
		UIRenderHelper.drawColoredTexture(graphics, c, x, y, 1, startX, startY, width, height, textureWidth, textureHeight);
	};

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack ms, float x, float y) {
		Matrix4f matrix = ms.last().pose();
		float uvx1 = startX / (float)textureWidth;
		float uvx2 = (startX + width) / (float)textureWidth;
		float uvy1 = startY / (float)textureHeight;
		float uvy2 = (startY + height) / (float)textureHeight;
		RenderSystem.setShaderTexture(0, location);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix, x, y, 0f).uv(uvx1, uvy1).endVertex();
		bufferbuilder.vertex(matrix, x, y + height, 0f).uv(uvx1, uvy2).endVertex();
		bufferbuilder.vertex(matrix, x + width, y + height, 0f).uv(uvx2, uvy2).endVertex();
		bufferbuilder.vertex(matrix, x + width, y, 0f).uv(uvx2, uvy1).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	};
    
};
