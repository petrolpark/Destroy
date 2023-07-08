package com.petrolpark.destroy.chemistry.reactionResult;

import java.util.List;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrecipitateReactionResult extends ReactionResult {
    
    private final ItemStack precipitate;

    public PrecipitateReactionResult(float moles, Reaction reaction, ItemStack precipitate) {
        super(moles, reaction);
        this.precipitate = precipitate;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        basin.acceptOutputs(List.of(precipitate), List.of(), false);
    }

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onVatReaction'");
    };

};
