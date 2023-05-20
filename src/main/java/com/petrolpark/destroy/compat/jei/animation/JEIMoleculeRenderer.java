package com.petrolpark.destroy.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.client.gui.MoleculeRenderer;

import mezz.jei.api.gui.drawable.IDrawable;

public class JEIMoleculeRenderer extends MoleculeRenderer implements IDrawable {

    public JEIMoleculeRenderer(Molecule molecule) {
        super(molecule);
    };

    @Override
    public int getWidth() {
        return x;
    };

    @Override
    public int getHeight() {
        return y;
    };

    @Override
    public void draw(PoseStack poseStack, int xOffset, int yOffset) {
        render(poseStack, xOffset, yOffset);
    };
    
};
