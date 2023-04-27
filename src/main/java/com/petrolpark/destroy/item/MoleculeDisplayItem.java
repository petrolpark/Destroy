package com.petrolpark.destroy.item;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.client.gui.JEIMoleculeRenderer;
import com.petrolpark.destroy.item.renderer.DestroyTooltipComponent;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MoleculeDisplayItem extends Item {

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

    private Molecule getMolecule(ItemStack itemStack) {
        if (!DestroyItems.MOLECULE_DISPLAY.isIn(itemStack)) return null;
        return Molecule.getMolecule(itemStack.getOrCreateTag().getString("Molecule"));
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

        private final JEIMoleculeRenderer molecule;

        public ClientMoleculeTooltipComponent(MoleculeTooltip tooltipComponent) {
            molecule = new JEIMoleculeRenderer(tooltipComponent.getMolecule());
        };

        @Override
        public int getHeight() {
            return 20;
        };

        @Override
        public int getWidth(Font pFont) {
            return 20;
        };

        @Override
        public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 401);
            molecule.draw(poseStack, mouseX + 10, mouseY + 10);
            poseStack.popPose();
        };
    };
    
};
