package com.petrolpark.destroy.item.renderer;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import net.minecraft.client.resources.model.BakedModel;

public abstract class DestroyCustomRenderedItemModel extends CustomRenderedItemModel {

    public DestroyCustomRenderedItemModel(BakedModel template, String basePath) {
        super(template, Destroy.MOD_ID, basePath);
    }
    
};
