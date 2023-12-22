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

        // Blacklight
        HELPER.forComponents(DestroyBlocks.BLACKLIGHT)
            .addStoryBoard("vat/uv", DestroyScenes::uv);

        // Bubble Cap
        HELPER.forComponents(DestroyBlocks.BUBBLE_CAP)
            .addStoryBoard("bubble_cap/generic", DestroyScenes::bubbleCapGeneric)
            .addStoryBoard("bubble_cap/mixtures", DestroyScenes::bubbleCapMixtures);

        // Centrifuge
        HELPER.forComponents(DestroyBlocks.CENTRIFUGE)
            .addStoryBoard("centrifuge", DestroyScenes::centrifuge);

        // Coaxial Gear
        HELPER.forComponents(DestroyBlocks.COAXIAL_GEAR)
            .addStoryBoard("coaxial_gear_shaftless", DestroyScenes::coaxialGearShaftless)
            .addStoryBoard("coaxial_gear_through", DestroyScenes::coaxialGearThrough);

        // Cooler
        HELPER.forComponents(DestroyBlocks.COOLER)
            .addStoryBoard("cooler", DestroyScenes::cooler);

        // Differential
        HELPER.forComponents(DestroyBlocks.DIFFERENTIAL)
            .addStoryBoard("differential", DestroyScenes::differential);

        // Double Cardan Shaft
        HELPER.forComponents(DestroyBlocks.DOUBLE_CARDAN_SHAFT)
            .addStoryBoard("double_cardan_shaft", DestroyScenes::doubleCardanShaft);
        
        // Dynamo
        HELPER.forComponents(DestroyBlocks.DYNAMO)
            .addStoryBoard("dynamo/redstone", DestroyScenes::dynamoRedstone)
            .addStoryBoard("dynamo/charging", DestroyScenes::dynamoCharging);
            //.addStoryBoard("dynamo/electrolysis", DestroyScenes::dynamoElectrolysis);

        // Extrusion Die
        HELPER.forComponents(DestroyBlocks.EXTRUSION_DIE)
            .addStoryBoard("extrusion_die", DestroyScenes::extrusionDie);

        // Hyperaccumulating Fertilizer
        HELPER.forComponents(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .addStoryBoard("phytomining", DestroyScenes::phytomining);

        // Planetary Gearset
        HELPER.forComponents(DestroyBlocks.PLANETARY_GEARSET)
            .addStoryBoard("planetary_gearset", DestroyScenes::planetaryGearset);

        // Pumpjack
        HELPER.forComponents(DestroyBlocks.PUMPJACK)
            .addStoryBoard("pumpjack", DestroyScenes::pumpjack);

        // Vat Controller
        HELPER.forComponents(DestroyBlocks.VAT_CONTROLLER)
            .addStoryBoard("vat/construction", DestroyScenes::vatConstruction)
            .addStoryBoard("vat/interaction", DestroyScenes::vatInteraction)
            .addStoryBoard("vat/uv", DestroyScenes::uv);
    };
};
