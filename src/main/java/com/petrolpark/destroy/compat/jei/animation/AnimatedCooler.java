package com.petrolpark.destroy.compat.jei.animation;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.petrolpark.destroy.block.entity.CoolerBlockEntity.ColdnessLevel;
import com.petrolpark.destroy.block.partial.DestroyPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;

import net.minecraft.util.Mth;

public class AnimatedCooler extends AnimatedKinetics {

    private ColdnessLevel coldnessLevel;

    public AnimatedCooler withColdness(ColdnessLevel coldnessLevel) {
        this.coldnessLevel = coldnessLevel;
        return this;
    };

    @Override
    public void draw(PoseStack ms, int xOffset, int yOffset) {
        ms.pushPose();
        ms.translate(xOffset, yOffset, 200);
		ms.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		ms.mulPose(Vector3f.YP.rotationDegrees(22.5f));
		int scale = 23;

        float bobbing = Mth.sin((float) ((AnimationTickHolder.getRenderTime() / 16f) % (2 * Math.PI))) / 64; // Displacement of the head due to bobbing
        float shivering = coldnessLevel == ColdnessLevel.FROSTING ? Mth.sin((float) ((AnimationTickHolder.getRenderTime() * 2) % (2 * Math.PI))) : 0f; // Rotation of the head due to shivering

        blockElement(AllBlocks.BLAZE_BURNER.getDefaultState()).atLocal(0d, 1.65d, 0d) // Render Brazier
			.scale(scale)
			.render(ms);

        PartialModel head = DestroyPartials.STRAY_SKULL;

        blockElement(head).atLocal(1, 1.8 + bobbing, 1)
			.rotate(0, 180 + shivering, 0)
			.scale(scale)
			.render(ms);

        ms.popPose();
    };
    
};
