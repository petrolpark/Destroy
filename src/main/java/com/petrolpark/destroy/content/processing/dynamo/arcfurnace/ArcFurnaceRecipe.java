package com.petrolpark.destroy.content.processing.dynamo.arcfurnace;

import com.petrolpark.destroy.DestroyRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;

public class ArcFurnaceRecipe extends BasinRecipe {

    @Override
    protected int getMaxInputCount() {
        return 10; // destroy:mixing/stainless_steel_efficient_fluxed has 10.
    };

    public ArcFurnaceRecipe(ProcessingRecipeParams params) {
        super(DestroyRecipeTypes.ARC_FURNACE, params);
    };
    
};
