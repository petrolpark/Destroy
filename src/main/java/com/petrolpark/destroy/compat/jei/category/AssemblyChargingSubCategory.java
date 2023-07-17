package com.petrolpark.destroy.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.compat.jei.animation.AnimatedDynamo;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;

import net.minecraft.client.gui.GuiGraphics;

public class AssemblyChargingSubCategory extends SequencedAssemblySubCategory {

    AnimatedDynamo dynamo;

    public AssemblyChargingSubCategory() {
        super(25);
        dynamo = new AnimatedDynamo(false);
    };

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
		ms.translate(-5, 50, 0);
		ms.scale(.6f, .6f, .6f);
		dynamo.draw(graphics, getWidth() / 2, 0);
		ms.popPose();
    };
    
}
