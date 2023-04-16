package com.petrolpark.destroy.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.partial.DestroyPartials;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

public class GasMaskModel extends BakedModelWrapper<BakedModel> {

    public GasMaskModel(BakedModel template) {
        super(template);
    };

    @Override
	public BakedModel applyTransform(TransformType cameraTransformType, PoseStack matrix, boolean leftHanded) {
		if (cameraTransformType == TransformType.HEAD) {
			return DestroyPartials.GAS_MASK.get().applyTransform(cameraTransformType, matrix, leftHanded);
        };
		return super.applyTransform(cameraTransformType, matrix, leftHanded);
	};
    
};
