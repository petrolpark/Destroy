package com.petrolpark.destroy.core.chemistry.vat.observation.colorimeter;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.client.DestroyIcons;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.chemistry.MoleculeRenderer;
import com.petrolpark.destroy.core.chemistry.vat.observation.AbstractQuantityObservingScreen;
import com.petrolpark.destroy.core.chemistry.vat.observation.RedstoneQuantityMonitorThresholdChangeC2SPacket;
import com.petrolpark.destroy.util.GuiHelper;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;

import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ColorimeterScreen extends AbstractQuantityObservingScreen {

    protected final ColorimeterBlockEntity colorimeter;

    protected List<LegacySpecies> availableSpecies;
    protected int speciesIndex;
    protected LegacySpecies species;

    protected boolean observingGas;

    protected IconButton gasButton;
    protected IconButton liquidButton;
    protected IconButton leftButton;
    protected IconButton rightButton;

    public ColorimeterScreen(ColorimeterBlockEntity colorimeter, List<LegacySpecies> availableSpecies) {
        super(colorimeter.redstoneMonitor, Component.translatable("block.destroy.colorimeter"), DestroyGuiTextures.COLORIMETER);
        this.colorimeter = colorimeter;

        species = colorimeter.getMolecule();
        speciesIndex = 0;
        this.availableSpecies = availableSpecies;
        availableSpecies.remove(species);
        availableSpecies.add(speciesIndex, species); // Put the selected species first

        observingGas = colorimeter.observingGas;
    };

    @Override
    protected void init() {
        super.init();

        liquidButton = new IconButton(guiLeft + 7, guiTop + 171, DestroyIcons.VAT_SOLUTION);
        liquidButton.setToolTip(DestroyLang.translate("tooltip.colorimeter.menu.liquid").component());
        liquidButton.active = observingGas;
        liquidButton.withCallback(() -> {
            observingGas = false;
            gasButton.active = true;
            liquidButton.active = false;
            configureClientColorimeter();
        });
        gasButton = new IconButton(guiLeft + 25, guiTop + 171, DestroyIcons.VAT_GAS);
        gasButton.setToolTip(DestroyLang.translate("tooltip.colorimeter.menu.gas").component());
        gasButton.active = !observingGas;
        gasButton.withCallback(() -> {
            observingGas = true;
            gasButton.active = false;
            liquidButton.active = true;
            configureClientColorimeter();
        });
        addRenderableWidgets(liquidButton, gasButton);

        leftButton = new IconButton(guiLeft + 10, guiTop + 49, AllIcons.I_MTD_LEFT);
        leftButton.setToolTip(DestroyLang.translate("tooltip.colorimeter.menu.change_species").component());
        leftButton.withCallback(() -> {
            speciesIndex = speciesIndex > 0 ? speciesIndex - 1 : availableSpecies.size() -1;
            species = availableSpecies.get(speciesIndex);
            configureClientColorimeter();
        });
        rightButton = new IconButton(guiLeft + 228, guiTop + 49, AllIcons.I_MTD_RIGHT);
        rightButton.setToolTip(DestroyLang.translate("tooltip.colorimeter.menu.change_species").component());
        rightButton.withCallback(() -> {
            speciesIndex = (speciesIndex < availableSpecies.size() - 1) ? speciesIndex + 1 : 0;
            species = availableSpecies.get(speciesIndex);
            configureClientColorimeter();
        });
        addRenderableWidgets(leftButton, rightButton);
    };

    @Override
    protected int getEditBoxY() {
        return 135;
    };

    private void configureClientColorimeter() {
        colorimeter.configure(species, observingGas);
    };

    @Override
    protected void onThresholdChange(boolean upper, float newValue) {
        DestroyMessages.sendToServer(new RedstoneQuantityMonitorThresholdChangeC2SPacket(upper, newValue, colorimeter.getBlockPos()));
    };

    @Override
    public void onClose() {
        if (species != colorimeter.getMolecule() || observingGas != colorimeter.observingGas) DestroyMessages.sendToServer(new ConfigureColorimeterC2SPacket(observingGas, species, colorimeter.getBlockPos()));
        super.onClose();
    };

    @Override
    protected void renderWindow(GuiGraphics graphics, int mX, int mY, float partialTicks) {
        super.renderWindow(graphics, mX, mY, partialTicks);
        PoseStack ms = graphics.pose();

        UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);
        // Species structure
        if (species != null) {
            ms.pushPose();
            ms.translate(guiLeft + 3, guiTop + 16, 100);
            GuiHelper.startStencil(graphics, 0, 0, 250, 85);
            MoleculeRenderer renderer = species.getRenderer();
            ms.translate((double)-renderer.getWidth() / 2d, (double)-renderer.getHeight() / 2d, 0);
            renderer.render(125, 42, graphics);
            GuiHelper.endStencil();
            ms.popPose();
        };
        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());

        // Species name
        Component name = species == null ? DestroyLang.translate("tooltip.colorimeter.menu.select_species").component() : species.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());
        graphics.drawCenteredString(font, name, guiLeft + 128, guiTop + 105, 0xFFFFFF);
    };

};
