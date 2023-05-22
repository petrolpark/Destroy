package com.petrolpark.destroy.client.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class DestroyPonderTags {

    public static final PonderTag DESTROY = new PonderTag(Destroy.asResource("destroy"))
        .item(DestroyItems.LOGO.get())
        .addToIndex();

    public static void register() {
        
        PonderRegistry.TAGS.forTag(DestroyPonderTags.DESTROY)
            .add(DestroyBlocks.AGING_BARREL)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COOLER)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyItems.HYPERACCUMULATING_FERTILIZER)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.FLUIDS)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.ARM_TARGETS)
            .add(DestroyBlocks.AGING_BARREL)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.REDSTONE)
            .add(DestroyBlocks.DYNAMO)
        ;

        PonderRegistry.TAGS.forTag(AllPonderTags.DISPLAY_SOURCES)
            .add(DestroyBlocks.POLLUTOMETER)
        ;
    };
    
};
