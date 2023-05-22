package com.petrolpark.destroy.client.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;

public class DestroyPonderIndex {

    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Destroy.MOD_ID);

    public static void register() {

        // Aging Barrel
        HELPER.forComponents(DestroyBlocks.AGING_BARREL)
            .addStoryBoard("aging_barrel", DestroyScenes::agingBarrel);

        // Bubble Cap
        HELPER.forComponents(DestroyBlocks.BUBBLE_CAP)
            .addStoryBoard("bubble_cap", DestroyScenes::bubbleCap);

        // Centrifuge
        HELPER.forComponents(DestroyBlocks.CENTRIFUGE)
            .addStoryBoard("centrifuge", DestroyScenes::centrifuge);
        
        // Dynamo
        HELPER.forComponents(DestroyBlocks.DYNAMO)
            .addStoryBoard("dynamo/redstone", DestroyScenes::dynamoRedstone)
            .addStoryBoard("dynamo/charging", DestroyScenes::dynamoCharging);

        // Hyperaccumulating Fertilizer
        HELPER.forComponents(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .addStoryBoard("phytomining", DestroyScenes::phytomining);
    };
};
