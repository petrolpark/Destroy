package com.petrolpark.destroy.compat.jei.animation;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.petrolpark.destroy.client.DestroyPartials;
import com.petrolpark.destroy.content.processing.cooler.CoolerBlockEntity.ColdnessLevel;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class AnimatedCooler extends AnimatedKinetics {

    private ColdnessLevel coldnessLevel;

    public AnimatedCooler withColdness(ColdnessLevel coldnessLevel) {
        this.coldnessLevel = coldnessLevel;
        return this;
    };

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(xOffset, yOffset, 200);
		ms.mulPose(Axis.XP.rotationDegrees(-15.5f));
		ms.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 23;

        float bobbing = Mth.sin((float) ((AnimationTickHolder.getRenderTime() / 16f) % (2 * Math.PI))) / 64; // Displacement of the head due to bobbing
        float shivering = coldnessLevel == ColdnessLevel.FROSTING ? Mth.sin((float) ((AnimationTickHolder.getRenderTime() * 2) % (2 * Math.PI))) : 0f; // Rotation of the head due to shivering

        blockElement(AllBlocks.BLAZE_BURNER.getDefaultState()).atLocal(0d, 1.65d, 0d) // Render Brazier
			.scale(scale)
			.render(graphics);

        PartialModel head = DestroyPartials.STRAY_SKULL;

        blockElement(head).atLocal(1, 1.8 + bobbing, 1)
			.rotate(0, 180 + shivering, 0)
			.scale(scale)
			.render(graphics);

        ms.popPose();
    };
    
};
