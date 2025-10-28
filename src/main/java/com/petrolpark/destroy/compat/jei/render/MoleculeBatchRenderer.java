package com.petrolpark.destroy.compat.jei.render;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.core.chemistry.MoleculeRenderer;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class MoleculeBatchRenderer {

    private final List<BatchedMolecule> molecules;
    private final List<BatchedMolecule> moleculesWithCharge;

    public static final ResourceLocation FONT_LOCATION = Destroy.asResource("charge");
    public static final Style FONT = Style.EMPTY.withFont(FONT_LOCATION);

    public MoleculeBatchRenderer(Minecraft minecraft, List<BatchRenderElement<LegacySpecies>> elements) {
        molecules = new ArrayList<>();
        moleculesWithCharge = new ArrayList<>();

        for (BatchRenderElement<LegacySpecies> element : elements) {
            int charge = element.ingredient().getCharge();
            FormattedCharSequence chargeStr = null;
            if (charge != 0)
            {
                String s = charge > 0 ? "+" : "-";
                if (Math.abs(charge) > 1)
                    s = Math.abs(charge) + s;
                chargeStr = FormattedCharSequence.forward(s, FONT);
            }

            BatchedMolecule m = new BatchedMolecule(element.ingredient().getRenderer(), element.x(), element.y(), chargeStr);
            molecules.add(m);
            if(chargeStr != null) moleculesWithCharge.add(m);
        }
    }

    public void render(GuiGraphics graphics, Minecraft minecraft) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        MultiBufferSource.BufferSource buffer = graphics.bufferSource();
        for (BatchedMolecule mol : molecules) {
            MoleculeRenderer renderer = mol.renderer;
            renderer.renderItem(mol.x, mol.y, 16, 16, poseStack, buffer);
        }
        buffer.endBatch();

        for (BatchedMolecule mol : moleculesWithCharge) {
            poseStack.pushPose();
            poseStack.translate(mol.x, mol.y, 100);
            Font fontRenderer = Minecraft.getInstance().font;
            graphics.drawString(fontRenderer, mol.charge, 17-fontRenderer.width(mol.charge), -1, 0xFFFFFF, true);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private record BatchedMolecule(MoleculeRenderer renderer, int x, int y, FormattedCharSequence charge) {}

    public static class Cache {
        private final LoadingCache<List<BatchRenderElement<LegacySpecies>>, MoleculeBatchRenderer> cache =
            CacheBuilder.newBuilder()
                .maximumSize(6)
                .build(new CacheLoader<>() {
                    @Override
                    public MoleculeBatchRenderer load(List<BatchRenderElement<LegacySpecies>> elements) {
                        Minecraft minecraft = Minecraft.getInstance();
                        return new MoleculeBatchRenderer(minecraft, elements);
                    }
                });

        public void renderBatch(GuiGraphics guiGraphics, List<BatchRenderElement<LegacySpecies>> elements) {
            MoleculeBatchRenderer batchData = cache.getUnchecked(elements);

            Minecraft minecraft = Minecraft.getInstance();
            batchData.render(guiGraphics, minecraft);
        }
    }
}
