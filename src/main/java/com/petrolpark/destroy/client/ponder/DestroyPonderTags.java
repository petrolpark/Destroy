package com.petrolpark.destroy.client.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.util.vat.VatMaterial;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.foundation.ponder.PonderTagRegistry.TagBuilder;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class DestroyPonderTags {

    public static final PonderTag

    CHEMISTRY = new PonderTag(Destroy.asResource("chemistry"))
        .item(DestroyItems.TEST_TUBE)
        .addToIndex(),
    
    DESTROY = new PonderTag(Destroy.asResource("destroy"))
        .item(DestroyItems.LOGO)
        .addToIndex(),

    VAT_SIDE_BLOCKS = new PonderTag(Destroy.asResource("vat_side_blocks"))
        .item(DestroyBlocks.VAT_CONTROLLER);

    public static void register() {

        PonderRegistry.TAGS.forTag(DestroyPonderTags.CHEMISTRY)
            .add(AllBlocks.BASIN)
            .add(DestroyBlocks.BLACKLIGHT)
            .add(AllBlocks.BLAZE_BURNER)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COOLER)
            .add(AllBlocks.MECHANICAL_MIXER)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;
        
        PonderRegistry.TAGS.forTag(DestroyPonderTags.DESTROY)
            .add(DestroyBlocks.AGING_BARREL)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COAXIAL_GEAR)
            .add(DestroyBlocks.COOLER)
            .add(DestroyBlocks.DOUBLE_CARDAN_SHAFT)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyBlocks.EXTRUSION_DIE)
            .add(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .add(DestroyBlocks.PLANETARY_GEARSET)
            .add(DestroyBlocks.PUMPJACK)
            .add(DestroyItems.SEISMOMETER)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        TagBuilder vatSideBlockBuilder = PonderRegistry.TAGS.forTag(DestroyPonderTags.VAT_SIDE_BLOCKS);
        VatMaterial.BLOCK_MATERIALS.forEach((block, material) -> vatSideBlockBuilder.add(block));

        PonderRegistry.TAGS.forTag(AllPonderTags.FLUIDS)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.PUMPJACK)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_RELAYS)
            .add(DestroyBlocks.COAXIAL_GEAR)
            .add(DestroyBlocks.DOUBLE_CARDAN_SHAFT)
            .add(DestroyBlocks.PLANETARY_GEARSET)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.ARM_TARGETS)
            .add(DestroyBlocks.AGING_BARREL)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.REDSTONE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.POLLUTOMETER)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.CONTRAPTION_ACTOR)
            .add(DestroyBlocks.EXTRUSION_DIE)
        ;
    };
    
};
