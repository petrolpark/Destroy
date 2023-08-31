package com.petrolpark.destroy.item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.item.tooltip.DestroyTooltipComponent;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.DestroyLang.TemperatureUnit;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MoleculeDisplayItem extends Item {

    private static final DecimalFormat df = new DecimalFormat();

    static {
        df.setMinimumFractionDigits(1);
        df.setMaximumFractionDigits(1);
    };

    public MoleculeDisplayItem(Properties properties) {
        super(properties);
    };

    public static ItemStack with(Molecule molecule) {
        ItemStack stack = new ItemStack(DestroyItems.MOLECULE_DISPLAY.get(), 1);
        stack.getOrCreateTag().putString("Molecule", molecule.getFullID());
        return stack;
    };

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        Molecule molecule = getMolecule(stack);
        if (molecule == null) return Optional.empty();
        return Optional.of(new MoleculeTooltip(molecule));
    };

    private static Molecule getMolecule(ItemStack itemStack) {
        if (!DestroyItems.MOLECULE_DISPLAY.isIn(itemStack)) return null;
        return Molecule.getMolecule(itemStack.getOrCreateTag().getString("Molecule"));
    };

    public static List<Component> getLore(Molecule molecule) {
        List<Component> tooltip = new ArrayList<>();
        if (molecule == null) return tooltip;

        boolean novel = molecule.isNovel();
        boolean charged = molecule.getCharge() != 0;
        boolean hypothetical = molecule.isHypothetical();
        boolean nerdMode = DestroyAllConfigs.CLIENT.chemistry.nerdMode.get();

        TemperatureUnit unit = DestroyAllConfigs.CLIENT.chemistry.temperatureUnit.get();
        
        if (nerdMode && !novel) tooltip.add(DestroyLang.translate("tooltip.molecule.formula", molecule.getSerlializedMolecularFormula(true) + DestroyLang.toSuperscript(molecule.getSerializedCharge(false))).component().withStyle(ChatFormatting.GRAY));
        if (charged && (!nerdMode || novel)) tooltip.add(DestroyLang.translate("tooltip.molecule.charge", molecule.getSerializedCharge(true)).component().withStyle(ChatFormatting.GRAY));
        if (!hypothetical) tooltip.add(DestroyLang.translate("tooltip.molecule.mass", df.format(molecule.getMass())).component().withStyle(ChatFormatting.GRAY));
        if (!charged && !hypothetical) {
            tooltip.add(DestroyLang.translate("tooltip.molecule.boiling_point", unit.of(molecule.getBoilingPoint(), df)).component().withStyle(ChatFormatting.GRAY));
            tooltip.add(DestroyLang.translate("tooltip.molecule.density", df.format(molecule.getDensity())).component().withStyle(ChatFormatting.GRAY));
            if (nerdMode) tooltip.add(DestroyLang.translate("tooltip.molecule.heat_capacity", df.format(molecule.getMolarHeatCapacity())).component().withStyle(ChatFormatting.GRAY));
        };

        Set<MoleculeTag> tags = molecule.getTags();
        if (!tags.isEmpty()) tooltip.add(Component.literal(" "));
        for (MoleculeTag tag : tags) {
            tooltip.add(tag.getFormattedName());
        };

        return tooltip;
    };

    public static class MoleculeTooltip extends DestroyTooltipComponent<MoleculeTooltip, ClientMoleculeTooltipComponent> {

        private final Molecule molecule;
    
        public MoleculeTooltip(Molecule molecule) {
            super(ClientMoleculeTooltipComponent::new);
            this.molecule = molecule;
        };

        public Molecule getMolecule() {
            return this.molecule;
        };
    };

    public static class ClientMoleculeTooltipComponent implements ClientTooltipComponent {

        private final MoleculeRenderer renderer;

        private int height;
        private int width;

        public ClientMoleculeTooltipComponent(MoleculeTooltip tooltipComponent) {
            renderer = tooltipComponent.getMolecule().getRenderer();

            height = renderer.getHeight() + 15;
            width = renderer.getWidth() + 15;
        };

        @Override
        public int getHeight() {
            return height;
        };

        @Override
        public int getWidth(Font pFont) {
            return width;
        };

        @Override
        public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose(); 
            poseStack.translate(0, 0, 401);
            renderer.render(mouseX + 10, mouseY + 5, graphics);
            poseStack.popPose();
        };
    };
    
};
