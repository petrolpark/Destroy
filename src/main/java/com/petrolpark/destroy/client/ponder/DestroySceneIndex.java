package com.petrolpark.destroy.client.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

public class DestroySceneIndex {

    public static final PonderTag DESTROY = new PonderTag(Destroy.asResource("destroy"))
        .item(DestroyItems.LOGO.get())
        .addToIndex();

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

        // Tags
        
        PonderRegistry.TAGS.forTag(DESTROY)
            .add(DestroyBlocks.AGING_BARREL)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COOLER)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyItems.HYPERACCUMULATING_FERTILIZER)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.FLUIDS)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.KINETIC_APPLIANCES)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.ARM_TARGETS)
            .add(DestroyBlocks.AGING_BARREL)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.REDSTONE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.DISPLAY_SOURCES)
            .add(DestroyBlocks.POLLUTOMETER)
        ;
    };
};
