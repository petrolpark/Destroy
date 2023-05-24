package com.petrolpark.destroy.item.properties;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.TestTubeItem;

import net.minecraft.client.renderer.item.ItemProperties;

public class DestroyItemProperties {

    public static void register() {
        ItemProperties.register(DestroyItems.TEST_TUBE.get(), Destroy.asResource("empty"), (stack, level, livingEntity, id) -> {
            return TestTubeItem.isEmpty(stack) ? 1.0f : 0.0f;
        });
    };
};
