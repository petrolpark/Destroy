package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;

public class DestroyMysteriousItemConversions {

    static {
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.EMPTY_BLAZE_BURNER.asStack(), DestroyBlocks.COOLER.asStack()));
    };

    public static void register() {};
};
