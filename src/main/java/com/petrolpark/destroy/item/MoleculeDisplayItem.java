package com.petrolpark.destroy.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;
import com.petrolpark.destroy.item.renderer.DestroyTooltipComponent;
import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MoleculeDisplayItem extends Item {

    private static final DecimalFormat df = new DecimalFormat();

    static {
        df.setMinimumFractionDigits(1);
        df.setMinimumFractionDigits(1);
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
        if (molecule.getCharge() != 0) tooltip.add(DestroyLang.translate("tooltip.molecule.charge", molecule.getSerializedCharge(true)).component().withStyle(ChatFormatting.GRAY));
        tooltip.add(DestroyLang.translate("tooltip.molecule.mass", df.format(molecule.getMass())).component().withStyle(ChatFormatting.GRAY));
        tooltip.add(DestroyLang.translate("tooltip.molecule.density", df.format(molecule.getDensity())).component().withStyle(ChatFormatting.GRAY));
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
            renderer = new MoleculeRenderer(tooltipComponent.getMolecule());

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
        public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
            poseStack.pushPose(); 
            poseStack.translate(0, 0, 401);
            renderer.render(poseStack, mouseX + 10, mouseY + 10);
            poseStack.popPose();
        };
    };
    
};
