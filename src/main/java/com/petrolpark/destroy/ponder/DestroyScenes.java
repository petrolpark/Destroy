package com.petrolpark.destroy.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;

public class DestroyScenes {

    public static final PonderTag DESTROY = new PonderTag(Destroy.asResource("destroy"))
        .item(DestroyBlocks.CENTRIFUGE.get())
        .addToIndex();

    private static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Destroy.MOD_ID);

    public static void register() {
        HELPER.addStoryBoard(DestroyBlocks.CENTRIFUGE, "centrifuge", CentrifugeScenes::separate, DESTROY, PonderTag.FLUIDS);

        PonderRegistry.TAGS.forTag(DESTROY)
            .add(DestroyBlocks.CENTRIFUGE)
        ;

        PonderRegistry.TAGS.forTag(PonderTag.FLUIDS)
            .add(DestroyBlocks.CENTRIFUGE)
        ;
    };
};
