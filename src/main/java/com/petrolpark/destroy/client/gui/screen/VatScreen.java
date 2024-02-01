package com.petrolpark.destroy.client.gui.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.icu.text.DecimalFormat;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.VatControllerBlock;
import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour.VatTankSegment.VatFluidTank;
import com.petrolpark.destroy.chemistry.ClientMixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.client.gui.DestroyGuiTextures;
import com.petrolpark.destroy.client.gui.DestroyIcons;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.MoleculeDisplayItem;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.GuiHelper;
import com.petrolpark.destroy.util.vat.Vat;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;

public class VatScreen extends AbstractSimiScreen {

    private int ticksUntilRefresh;

    private static int CARD_HEIGHT = 32;
    private Rect2i moleculeScrollArea;
    private Rect2i textArea;
    private Rect2i filterArea;

    private static final DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(5);
        df.setMaximumFractionDigits(5);
    };

    private VatControllerBlockEntity blockEntity;

    private DestroyGuiTextures background;

    private Molecule selectedMolecule;
    private List<Pair<Molecule, Float>> orderedMolecules;

    private View selectedView;

    private LerpedFloat moleculeScroll = LerpedFloat.linear().startWithValue(0);
    private LerpedFloat textScroll = LerpedFloat.linear().startWithValue(0);
    private LerpedFloat horizontalTextScroll = LerpedFloat.linear().startWithValue(0);

    private float maxMoleculeScroll = 1f;
    private float textWidth = 0f;
    private float textHeight = 0f;

    private IconButton confirmButton;
    private IconButton controlsIcon;
    private List<IconButton> contentsShownButtons;
    private EditBox filter;

    public VatScreen(VatControllerBlockEntity vatController) {
        super(DestroyLang.translate("tooltip.vat.menu.title").component());
        background = DestroyGuiTextures.VAT;
        blockEntity = vatController;

        selectedMolecule = null;
        orderedMolecules = new ArrayList<>();

        selectedView = View.BOTH;

        ticksUntilRefresh = 0;
    };

    @Override
    @SuppressWarnings("null")
	protected void init() {
		setWindowSize(background.width, background.height);
		super.init();
		clearWidgets();

        moleculeScrollArea = new Rect2i(guiLeft + 11, guiTop + 16, 119, 169);
        textArea = new Rect2i(guiLeft + 131, guiTop + 102, 114, 83);

        confirmButton = new IconButton(guiLeft + background.width - 33, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		confirmButton.withCallback(() -> {if (minecraft != null && minecraft.player != null) minecraft.player.closeContainer();}); // It thinks minecraft and player might be null
		addRenderableWidget(confirmButton);

        controlsIcon = new IconButton(guiLeft + 16, guiTop + 202, DestroyIcons.QUESTION_MARK);
        controlsIcon.setToolTip(DestroyLang.translate("tooltip.vat.menu.controls").component());
        addRenderableWidget(controlsIcon);

        contentsShownButtons = new ArrayList<>(View.values().length);
        int i = 0;
        for (View view : View.values()) {
            IconButton button = new IconButton(guiLeft + 52 + i * 18, guiTop + background.height - 24, view.icon);
            button.withCallback(() -> {
                contentsShownButtons.forEach(b -> b.active = true);
                button.active = false;
                selectedView = view;
                updateMoleculeList();
                moleculeScroll.updateChaseTarget(0f);
            });
            button.active = selectedView != view;
            button.setToolTip(view.tooltip);
            contentsShownButtons.add(button);
            addRenderableWidget(button);
            i++;
        };

        filter = new EditBox(font, guiLeft + 114, guiTop + background.height - 19, 95, 10, Components.immutableEmpty());
        filter.setBordered(false);
        filter.setMaxLength(35);
		filter.setFocused(false);
		filter.mouseClicked(0, 0, 0);
		filter.setResponder(s -> {
            updateMoleculeList();
            moleculeScroll.updateChaseTarget(0f);
        });
		filter.active = false;
        filter.setTooltip(Tooltip.create(DestroyLang.translate("tooltip.vat.menu.search_filter").component()));
        addRenderableWidget(filter);

        filterArea = new Rect2i(guiLeft + 110, guiTop + background.height - 24, guiLeft + 114 + 95, guiTop + background.height - 24 + 18);

        updateMoleculeList();
    };

    @Override
	public void tick() {
		super.tick();

        moleculeScroll.tickChaser();
        textScroll.tickChaser();
        horizontalTextScroll.tickChaser();

        if (getFocused() != filter) {
			filter.setCursorPosition(filter.getValue().length());
			filter.setHighlightPos(filter.getCursorPosition());
		};

        ticksUntilRefresh--;
        if (ticksUntilRefresh < 0) {
            ticksUntilRefresh = 20;
            updateMoleculeList();  
        };
	};

    protected void updateMoleculeList() {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        int amount = 0;
        VatFluidTank tank = null;
        switch (selectedView) {
            case BOTH: {
                mixture = blockEntity.getCombinedReadOnlyMixture();
                amount = blockEntity.getVatOptional().map(Vat::getCapacity).orElse(0);
                break;
            } case GAS: {
                tank = blockEntity.getGasTank();
            } case LIQUID: {
                if (selectedView == View.LIQUID) tank = blockEntity.getLiquidTank();
            } default: {
                if (tank != null) {
                    amount = tank.getFluidAmount();
                    FluidStack stack = tank.getFluid();
                    if (DestroyFluids.isMixture(stack)) mixture = ReadOnlyMixture.readNBT(ClientMixture::new, stack.getOrCreateChildTag("Mixture"));
                };
            };
        };

        orderedMolecules = new ArrayList<>(mixture.getContents(false).size());
        for (Molecule molecule : mixture.getContents(false)) {
            String search = filter.getValue().toUpperCase();
            if (
                filter == null || filter.getValue().isEmpty()
                || molecule.getName(false).getString().toUpperCase().indexOf(search) > -1 // Check common name against filter
                || molecule.getName(true).getString().toUpperCase().indexOf(search) > -1 // Check IUPAC name against filter
                || molecule.getSerlializedMolecularFormula(false).toUpperCase().indexOf(search) > -1 // Check formula against filter
            ) {
                orderedMolecules.add(Pair.of(molecule, mixture.getConcentrationOf(molecule) * amount / 1000f));
            };
        };
        Collections.sort(orderedMolecules, (p1, p2) -> Float.compare(p2.getSecond(), p1.getSecond()));
    };

    @Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (moleculeScrollArea.contains((int)mouseX, (int)mouseY)) {
            float chaseTarget = moleculeScroll.getChaseTarget();
            float max = 37 - 169;
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
                float max = textHeight - 76;

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
        if (filterArea.contains((int)mouseX, (int)mouseY) && !filter.isFocused()) {
            filter.setFocused(true);
			filter.setHighlightPos(0);
			setFocused(filter);
			return true;
        } else {
            filter.setFocused(false);
        };
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
                    return true;
                };
            };
        };
        return super.mouseClicked(mouseX, mouseY, button);
    };

    @Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (getFocused() instanceof EditBox && (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) && filter.isFocused()) {
			filter.setFocused(false);
			return true;
		};

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

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
        GuiHelper.startStencil(graphics, moleculeScrollArea.getX(), moleculeScrollArea.getY(), moleculeScrollArea.getWidth(), moleculeScrollArea.getHeight());
        ms.pushPose();
        ms.translate(guiLeft + 25, guiTop + 20 + scrollOffset, 0);
        for (Pair<Molecule, Float> pair : orderedMolecules) {
            ms.pushPose();
            Molecule molecule = pair.getFirst();
            boolean selected = selectedMolecule != null && molecule.getFullID().equals(selectedMolecule.getFullID());
            (selected ? DestroyGuiTextures.VAT_CARD_SELECTED : DestroyGuiTextures.VAT_CARD_UNSELECTED).render(graphics, selected ? -1 : 0, selected ? -1 : 0);
            graphics.drawString(font, DestroyLang.shorten(molecule.getName(iupac).getString(), font, 92), 4, 4, 0xFFFFFF);
            graphics.drawString(font, DestroyLang.translate("tooltip.vat.menu.moles", df.format(pair.getSecond())).component(), 4, 17, 0xFFFFFF);
            ms.popPose();
            ms.translate(0, CARD_HEIGHT, 0);
        };
        ms.popPose();
        GuiHelper.endStencil();

        // Scroll dot
        DestroyGuiTextures.VAT_SCROLL_DOT.render(graphics, guiLeft + 15, guiTop + 20 + (int)(moleculeScroll.getValue(partialTicks) * 154 / maxMoleculeScroll));

        // Shadow over scroll area
        graphics.fillGradient(moleculeScrollArea.getX(), moleculeScrollArea.getY(),                                       moleculeScrollArea.getX() + moleculeScrollArea.getWidth(), moleculeScrollArea.getY() + 10,                             200, 0x77000000, 0x00000000);
		graphics.fillGradient(moleculeScrollArea.getX(), moleculeScrollArea.getY() + moleculeScrollArea.getHeight() - 10, moleculeScrollArea.getX() + moleculeScrollArea.getWidth(), moleculeScrollArea.getY() + moleculeScrollArea.getHeight(), 200, 0x00000000, 0x77000000);

        // Molecule structure
        if (selectedMolecule != null) {
            ms.pushPose();
            ms.translate(guiLeft + 131, guiTop + 16, 100);
            GuiHelper.startStencil(graphics, 0, 0, 114, 85);
            MoleculeRenderer renderer = selectedMolecule.getRenderer();
            ms.translate((double)-renderer.getWidth() / 2, (double)-renderer.getHeight() / 2, 0);
            renderer.render(56, 42, graphics);
            GuiHelper.endStencil();
            ms.popPose();
        };

        // Molecule name and info
        textHeight = font.lineHeight;
        textWidth = 0f;
        GuiHelper.startStencil(graphics, textArea.getX(), textArea.getY(), textArea.getWidth(), textArea.getHeight());
        ms.pushPose();
        ms.translate(guiLeft + 135 - (int)horizontalTextScroll.getValue(partialTicks), guiTop + 106 - (int)textScroll.getValue(partialTicks), 0);
        if (selectedMolecule != null) {
            graphics.drawString(font, selectedMolecule.getName(iupac), 0, 0, 0xFFFFFF);
            textWidth = font.width(selectedMolecule.getName(iupac));
            for (Component line : MoleculeDisplayItem.getLore(selectedMolecule)) {
                textHeight += font.lineHeight;
                textWidth = Math.max(textWidth, font.width(line));
                ms.translate(0, font.lineHeight, 0);
                graphics.drawString(font, line, 0, 0, 0xFFFFFF);
            };
        } else {
            graphics.drawString(font, DestroyLang.translate(orderedMolecules.isEmpty() ? "tooltip.vat.menu.empty" : "tooltip.vat.menu.select").component(), 0, 0, 0xFFFFFF);            
        };
        ms.popPose();
        GuiHelper.endStencil();

        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());

        // Filter label
        graphics.drawString(font, DestroyLang.translate("tooltip.vat.menu.filters").component(), guiLeft + 52, guiTop + background.height - 34, AllGuiTextures.FONT_COLOR, false);
        
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

    protected enum View {
        BOTH(DestroyIcons.VAT_ALL, DestroyLang.translate("tooltip.vat.menu.view.both").component()),
        LIQUID(DestroyIcons.VAT_SOLUTION, DestroyLang.translate("tooltip.vat.menu.view.liquid").component()),
        GAS(DestroyIcons.VAT_GAS, DestroyLang.translate("tooltip.vat.menu.view.gas").component());

        public final DestroyIcons icon;
        public final Component tooltip;

        View(DestroyIcons icon, Component tooltip) {
            this.icon = icon;
            this.tooltip = tooltip;
        };
    };

};
