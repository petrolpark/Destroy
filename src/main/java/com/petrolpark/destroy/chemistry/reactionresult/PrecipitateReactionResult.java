package com.petrolpark.destroy.chemistry.reactionResult;

import java.util.List;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PrecipitateReactionResult extends ReactionResult {
    
    private final ItemStack precipitate;

    public PrecipitateReactionResult(float moles, ItemStack precipitate) {
        super(moles);
        this.precipitate = precipitate;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        basin.acceptOutputs(List.of(precipitate), List.of(), false);
    };

    @Override
    public void onVatReaction(Level level, Mixture mixture) {
        // TODO Auto-generated method stub
    };
}
