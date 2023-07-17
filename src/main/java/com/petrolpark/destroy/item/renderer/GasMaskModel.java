package com.petrolpark.destroy.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.destroy.block.model.DestroyPartials;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.BakedModelWrapper;

public class GasMaskModel extends BakedModelWrapper<BakedModel> {

    public GasMaskModel(BakedModel template) {
        super(template);
    };

    @Override
	public BakedModel applyTransform(ItemDisplayContext itemDisplayContext, PoseStack matrix, boolean leftHanded) {
		if (itemDisplayContext == ItemDisplayContext.HEAD) {
			return DestroyPartials.GAS_MASK.get().applyTransform(itemDisplayContext, matrix, leftHanded);
        };
		return super.applyTransform(itemDisplayContext, matrix, leftHanded);
	};
    
};
