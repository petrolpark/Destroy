package com.petrolpark.destroy.chemistry.reactionresult;

import com.petrolpark.destroy.chemistry.ReactionResult;

import net.minecraft.world.item.ItemStack;

public class PrecipitateReactionResult extends ReactionResult {
    
    private ItemStack precipitate;

    public PrecipitateReactionResult(ItemStack precipitate) {
        this.precipitate = precipitate;
    };

    public ItemStack getPrecipitate() {
        return precipitate;
    };
}
