package com.petrolpark.destroy.item;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.renderer.SwissArmyKnifeRenderer;

import net.minecraft.client.renderer.item.ItemProperties;

public class DestroyItemProperties {
  
    public static void register() {
        ItemProperties.register(DestroyItems.SWISS_ARMY_KNIFE.get(), Destroy.asResource("component"), SwissArmyKnifeRenderer.RenderedTool::getItemProperty);
    };
};
