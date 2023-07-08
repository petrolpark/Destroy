package com.petrolpark.destroy.chemistry.reactionResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Level;

public class CombinedReactionResult extends ReactionResult {

    protected List<ReactionResult> childResults;

    public CombinedReactionResult(float moles, Reaction reaction) {
        super(moles, reaction);
        childResults = new ArrayList<>();
    };

    public CombinedReactionResult with(ReactionResult result) {
        childResults.add(result);
        return this;
    };

    public CombinedReactionResult with(Collection<ReactionResult> results) {
        childResults.addAll(results);
        return this;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        for (ReactionResult childResult : childResults) {
            childResult.onBasinReaction(level, basin, mixture);
        };
    };

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture) {
        for (ReactionResult childResult : childResults) {
            childResult.onVatReaction(level, vatController, mixture);
        };
    };
    
};
