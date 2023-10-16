package com.petrolpark.destroy.client.gui.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.ibm.icu.text.DecimalFormat;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.client.gui.DestroyIcons;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.item.MoleculeDisplayItem;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class VatScreen extends AbstractSimiScreen {

    private static int CARD_HEIGHT = 32;
    private Rect2i moleculeScrollArea;
    private Rect2i textArea;

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(6);
        df.setMaximumFractionDigits(6);
    };

    private VatControllerBlockEntity blockEntity;

    private DestroyGuiTextures background;

    private Molecule selectedMolecule;
    private List<Pair<Molecule, Float>> orderedMolecules;

    private LerpedFloat moleculeScroll = LerpedFloat.linear().startWithValue(0);
    private LerpedFloat textScroll = LerpedFloat.linear().startWithValue(0);
    private LerpedFloat horizontalTextScroll = LerpedFloat.linear().startWithValue(0);

    private float maxMoleculeScroll = 1f;
    private float textWidth = 0f;
    private float textHeight = 0f;

    private IconButton confirmButton;
    private IconButton controlsIcon;

    public VatScreen(VatControllerBlockEntity vatController) {
        super(DestroyLang.translate("tooltip.vat.menu.title").component());
        background = DestroyGuiTextures.VAT;
        blockEntity = vatController;

        selectedMolecule = null;
        orderedMolecules = new ArrayList<>(blockEntity.getCombinedReadOnlyMixture().getContents(false).size());
    };

    @Override
	protected void init() {
		setWindowSize(background.width, background.height);
		super.init();
		clearWidgets();

        moleculeScrollArea = new Rect2i(guiLeft + 11, guiTop + 16, 119, 173);
        textArea = new Rect2i(guiLeft + 131, guiTop + 102, 114, 87);

        confirmButton = new IconButton(guiLeft + background.width - 33, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> minecraft.player.closeContainer());
		addRenderableWidget(confirmButton);

        controlsIcon = new IconButton(guiLeft + 16, guiTop + 202, DestroyIcons.QUESTION_MARK);
        controlsIcon.setToolTip(DestroyLang.translate("tooltip.vat.menu.controls").component());
        addRenderableWidget(controlsIcon);
    };

    @Override
	public void tick() {
		super.tick();

        moleculeScroll.tickChaser();
        textScroll.tickChaser();
        horizontalTextScroll.tickChaser();

        ReadOnlyMixture mixture = blockEntity.getCombinedReadOnlyMixture();

        orderedMolecules = new ArrayList<>(mixture.getContents(false).size());
        orderedMolecules.addAll(mixture.getContents(false).stream().map(molecule -> Pair.of(molecule, mixture.getConcentrationOf(molecule))).toList());
        Collections.sort(orderedMolecules, (p1, p2) -> Float.compare(p2.getSecond(), p1.getSecond()));
	};

    @Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (moleculeScrollArea.contains((int)mouseX, (int)mouseY)) {
            float chaseTarget = moleculeScroll.getChaseTarget();
            float max = 37 - 173;
            max += (orderedMolecules.size() - 1) * CARD_HEIGHT;

            if (max >= 0) {
                chaseTarget -= delta * 12;
                chaseTarget = Mth.clamp(chaseTarget, 0, max);
                moleculeScroll.chase((int) chaseTarget, 0.7f, Chaser.EXP);
            } else {
                moleculeScroll.chase(0, 0.7f, Chaser.EXP);
            };
            
            maxMoleculeScroll = max;
            return true;
        } else if (textArea.contains((int)mouseX, (int)mouseY)) {
            if (hasShiftDown()) {
                float chaseTarget = horizontalTextScroll.getChaseTarget();
                float max = textWidth - 106;

                if (max >= 0) {
                    chaseTarget -= delta * 6;
                    chaseTarget = Mth.clamp(chaseTarget, 0, max);
                    horizontalTextScroll.chase((int) chaseTarget, 0.7f, Chaser.EXP);
                } else {
                    horizontalTextScroll.chase(0, 0.7f, Chaser.EXP);
                };
            } else {
                float chaseTarget = textScroll.getChaseTarget();
                float max = textHeight - 80;

                if (max >= 0) {
                    chaseTarget -= delta * 6;
                    chaseTarget = Mth.clamp(chaseTarget, 0, max);
                    textScroll.chase((int) chaseTarget, 0.7f, Chaser.EXP);
                } else {
                    textScroll.chase(0, 0.7f, Chaser.EXP);
                };
            };
        };
        return false;
    };

    @Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (moleculeScrollArea.contains((int)mouseX, (int)mouseY)) {
            for (int i = 0; i < orderedMolecules.size(); i++) {
                int yPos = guiTop + ((i + 1) * CARD_HEIGHT) - (int)moleculeScroll.getChaseTarget() - 14;
                Rect2i clickArea = new Rect2i(moleculeScrollArea.getX() + 15, yPos, 97, 28);
                if (clickArea.contains((int)mouseX, (int)mouseY)) {
                    Molecule molecule = orderedMolecules.get(i).getFirst();
                    if (selectedMolecule != null && selectedMolecule.getFullID().equals(molecule.getFullID())) {
                        selectedMolecule = null;
                    } else {
                        selectedMolecule = molecule;
                    };
                    textScroll.chase(0, 0.7, Chaser.EXP);
                    horizontalTextScroll.chase(0, 0.7, Chaser.EXP);
                };
            };
        };
        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
    @SuppressWarnings("null") // 'minecraft' is not null
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null) return;
        PoseStack ms = graphics.pose();
        boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

        float scrollOffset = -moleculeScroll.getValue(partialTicks);
        
        // Background
        background.render(graphics, guiLeft, guiTop);

        // Title
        graphics.drawString(font, title, guiLeft + 21, guiTop + 4, 0x54214F, false);

        UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);
        
        // Molecule entries
        startStencil(graphics, moleculeScrollArea.getX(), moleculeScrollArea.getY(), moleculeScrollArea.getWidth(), moleculeScrollArea.getHeight());
        ms.pushPose();
        ms.translate(guiLeft + 25, guiTop + 20 + scrollOffset, 0);
        for (Pair<Molecule, Float> pair : orderedMolecules) {
            ms.pushPose();
            Molecule molecule = pair.getFirst();
            boolean selected = selectedMolecule != null && molecule.getFullID().equals(selectedMolecule.getFullID());
            (selected ? DestroyGuiTextures.VAT_CARD_SELECTED : DestroyGuiTextures.VAT_CARD_UNSELECTED).render(graphics, selected ? -1 : 0, selected ? -1 : 0);
            graphics.drawString(font, DestroyLang.shorten(molecule.getName(iupac).getString(), font, 92), 4, 4, 0xFFFFFF);
            graphics.drawString(font, Component.literal(df.format(pair.getSecond())+"M"), 4, 17, 0xFFFFFF);
            ms.popPose();
            ms.translate(0, CARD_HEIGHT, 0);
        };
        ms.popPose();
        endStencil();

        // Scroll dot
        DestroyGuiTextures.VAT_SCROLL_DOT.render(graphics, guiLeft + 15, guiTop + 20 + (int)(moleculeScroll.getValue(partialTicks) * 158 / maxMoleculeScroll));

        // Shadow over scroll area
        graphics.fillGradient(moleculeScrollArea.getX(), moleculeScrollArea.getY(),                               moleculeScrollArea.getX() + moleculeScrollArea.getWidth(), moleculeScrollArea.getY() + 10, 200, 0x77000000, 0x00000000);
		graphics.fillGradient(moleculeScrollArea.getX(), moleculeScrollArea.getY() + moleculeScrollArea.getHeight() - 10, moleculeScrollArea.getX() + moleculeScrollArea.getWidth(), moleculeScrollArea.getY() + moleculeScrollArea.getHeight(), 200, 0x00000000, 0x77000000);

        // Molecule structure
        if (selectedMolecule != null) {
            ms.pushPose();
            ms.translate(guiLeft + 131, guiTop + 16, 100);
            startStencil(graphics, 0, 0, 114, 85);
            MoleculeRenderer renderer = selectedMolecule.getRenderer();
            ms.translate((double)-renderer.getWidth() / 2, (double)-renderer.getHeight() / 2, 0);
            renderer.render(56, 42, graphics);
            endStencil();
            ms.popPose();
        };

        // Molecule name and info
        textHeight = font.lineHeight;
        textWidth = 0f;
        startStencil(graphics, textArea.getX(), textArea.getY(), textArea.getWidth(), textArea.getHeight());
        ms.pushPose();
        ms.translate(guiLeft + 135 - (int)horizontalTextScroll.getValue(partialTicks), guiTop + 106 - (int)textScroll.getValue(partialTicks), 0);
        if (selectedMolecule != null) {
            graphics.drawString(font, selectedMolecule.getName(iupac), 0, 0, 0xFFFFFF);
            textWidth = font.width(selectedMolecule.getName(iupac));
            int i = 0;
            for (Component line : MoleculeDisplayItem.getLore(selectedMolecule)) {
                textHeight += font.lineHeight;
                textWidth = Math.max(textWidth, font.width(line));
                ms.translate(0, font.lineHeight, 0);
                graphics.drawString(font, line, 0, 0, 0xFFFFFF);
                i++;
            };
        } else {
            graphics.drawString(font, DestroyLang.translate(orderedMolecules.isEmpty() ? "tooltip.vat.menu.empty" : "tooltip.vat.menu.select").component(), 0, 0, 0xFFFFFF);            
        };
        ms.popPose();
        endStencil();

        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());
        
        // Show 3D Vat controller
        if (!blockEntity.hasLevel()) return;
        ms.pushPose();
        TransformStack msr = TransformStack.cast(ms);
        msr.pushPose()
			.translate(guiLeft + background.width + 4, guiTop + background.height + 4, 100)
			.scale(40)
			.rotateX(-22)
			.rotateY(63);
        GuiGameElement.of(DestroyBlocks.VAT_CONTROLLER.getDefaultState().setValue(VatControllerBlock.FACING, Direction.WEST))
            .render(graphics);
        ms.popPose();
    };

    protected void startStencil(GuiGraphics graphics, float x, float y, float w, float h) {
		RenderSystem.clear(GL30.GL_STENCIL_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

		GL11.glDisable(GL11.GL_STENCIL_TEST);
		RenderSystem.stencilMask(~0);
		RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		RenderSystem.stencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		RenderSystem.stencilMask(0xFF);
		RenderSystem.stencilFunc(GL11.GL_NEVER, 1, 0xFF);

		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(x, y, 0);
		matrixStack.scale(w, h, 1);
		graphics.fillGradient(0, 0, 1, 1, -100, 0xff000000, 0xff000000);
		matrixStack.popPose();

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
	}

	protected void endStencil() {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
    
};
