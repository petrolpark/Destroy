package com.petrolpark.destroy.recipe;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DestroyMysteriousItemConversions {

    static {
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(AllItems.EMPTY_BLAZE_BURNER.asStack(), DestroyBlocks.COOLER.asStack()));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(DestroyItems.IODINE.asStack(), new ItemStack(Items.DRAGON_BREATH)));
        MysteriousItemConversionCategory.RECIPES.add(ConversionRecipe.create(DestroyItems.BUCKET_AND_SPADE.asStack(), DestroyItems.TEAR_BOTTLE.asStack()));
    };

    public static void register() {};
};
