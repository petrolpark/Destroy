package com.petrolpark.destroy.chemistry.reactionresult;

import java.util.function.Supplier;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrecipitateReactionResult extends ReactionResult {
    
    private final Supplier<ItemStack> precipitate;

    public PrecipitateReactionResult(float moles, Reaction reaction, Supplier<ItemStack> precipitate) {
        super(moles, reaction);
        this.precipitate = precipitate;
    };

    public ItemStack getPrecipitate() {
        return precipitate.get();
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        // Do nothing, this is handled in ReactionInBasinRecipe
    };

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onVatReaction'");
    };

};
