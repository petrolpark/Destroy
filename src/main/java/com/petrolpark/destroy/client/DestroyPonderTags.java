package com.petrolpark.destroy.client;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.TagBuilder;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderTag;
import net.createmod.ponder.foundation.registration.DefaultPonderTagRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.createmod.ponder.foundation.registration.PonderTagBuilder;
import net.createmod.ponder.foundation.registration.PonderTagRegistry;
import net.minecraft.resources.ResourceLocation;

public class DestroyPonderTags {

    public static final ResourceLocation
    CHEMISTRY = Destroy.asResource("chemistry"),

    DESTROY = Destroy.asResource("destroy"),

    VAT_SIDE_BLOCKS = Destroy.asResource("vat_side_blocks");

    private static PonderTagRegistrationHelper<ResourceLocation> helper = null;
    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        DestroyPonderTags.helper = helper;

        helper.registerTag(DestroyPonderTags.CHEMISTRY)
                .addToIndex()
                .item(AllBlocks.BASIN)
            .item(DestroyBlocks.BLACKLIGHT)
            .item(AllBlocks.BLAZE_BURNER)
            .item(DestroyBlocks.BUBBLE_CAP)
            .item(DestroyBlocks.CENTRIFUGE)
            .item(DestroyBlocks.COOLER)
            .item(AllBlocks.MECHANICAL_MIXER)
            .item(DestroyBlocks.VAT_CONTROLLER)
                .icon(DestroyItems.TEST_TUBE.getId())
                .register();
        ;
        
        helper.registerTag(DestroyPonderTags.DESTROY)
                .addToIndex()
            .item(DestroyBlocks.AGING_BARREL)
            .item(DestroyBlocks.BLACKLIGHT)
            .item(DestroyBlocks.BUBBLE_CAP)
            .item(DestroyBlocks.CATALYTIC_CONVERTER)
            .item(DestroyBlocks.CENTRIFUGE)
            .item(DestroyBlocks.COOLER)
            .item(DestroyBlocks.CREATIVE_PUMP)
            .item(DestroyBlocks.CUSTOM_EXPLOSIVE_MIX)
            .item(DestroyBlocks.DYNAMO)
            .item(DestroyBlocks.EXTRUSION_DIE)
            .item(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .item(DestroyBlocks.KEYPUNCH)
            .item(DestroyItems.POLLUTION_SYMBOL)
            .item(DestroyBlocks.PUMPJACK)
            .item(DestroyBlocks.REDSTONE_PROGRAMMER)
            .item(DestroyItems.SEISMOMETER)
            .item(DestroyItems.SEISMOGRAPH)
            .item(DestroyBlocks.SIPHON)
            .item(DestroyBlocks.TREE_TAP)
            .item(DestroyBlocks.VAT_CONTROLLER)
                .icon(DestroyItems.LOGO.getId())
                .register();
        ;

        TagBuilder vatSideBlocksTagBuilder = helper.registerTag(DestroyPonderTags.VAT_SIDE_BLOCKS);
        VatMaterial.BLOCK_MATERIALS.forEach((blockIngredient, material) -> blockIngredient.getDisplayedItemStacks().forEach(stack -> vatSideBlocksTagBuilder.item(stack.getItem())));
        vatSideBlocksTagBuilder.icon(DestroyBlocks.VAT_CONTROLLER.getId()).register();

        helper.registerTag(AllCreatePonderTags.FLUIDS)
            .item(DestroyBlocks.BUBBLE_CAP)
            .item(DestroyBlocks.CATALYTIC_CONVERTER)
            .item(DestroyBlocks.CENTRIFUGE)
            .item(DestroyBlocks.CREATIVE_PUMP)
            .item(DestroyBlocks.PUMPJACK)
            .item(DestroyBlocks.SIPHON)
            .item(DestroyBlocks.TREE_TAP)
            .item(DestroyBlocks.VAT_CONTROLLER)
        ;

        helper.registerTag(AllCreatePonderTags.KINETIC_APPLIANCES)
            .item(DestroyBlocks.CENTRIFUGE)
            .item(DestroyBlocks.DYNAMO)
            .item(DestroyBlocks.KEYPUNCH)
            .item(DestroyBlocks.MECHANICAL_SIEVE)
            .item(DestroyBlocks.PUMPJACK)
            .item(DestroyBlocks.TREE_TAP)
        ;

        helper.registerTag(AllCreatePonderTags.ARM_TARGETS)
            .item(DestroyBlocks.AGING_BARREL)
        ;

        helper.registerTag(AllCreatePonderTags.REDSTONE)
            .item(DestroyBlocks.DYNAMO)
            .item(DestroyBlocks.REDSTONE_PROGRAMMER)
            .item(DestroyBlocks.SIPHON)
        ;

        helper.registerTag(AllCreatePonderTags.DISPLAY_SOURCES)
            .item(DestroyBlocks.BUBBLE_CAP)
            .item(DestroyBlocks.CENTRIFUGE)
            .item(DestroyBlocks.COLORIMETER)
            .item(DestroyBlocks.POLLUTOMETER)
            .item(DestroyBlocks.VAT_CONTROLLER)
        ;

        helper.registerTag(AllCreatePonderTags.CONTRAPTION_ACTOR)
            .item(DestroyBlocks.EXTRUSION_DIE)
        ;

        helper.registerTag(AllCreatePonderTags.CREATIVE)
            .item(DestroyBlocks.CREATIVE_PUMP)
        ;
    };

    public static final void refreshVatMaterialsTag() {
        VatMaterial.BLOCK_MATERIALS.keySet().forEach(blockIngredient -> blockIngredient.getDisplayedItemStacks().forEach(stack -> helper.registerTag(VAT_SIDE_BLOCKS).item(stack.getItem())));
    };
    
};
