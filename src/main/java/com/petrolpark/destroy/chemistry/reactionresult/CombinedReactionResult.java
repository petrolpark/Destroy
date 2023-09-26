package com.petrolpark.destroy.chemistry.reactionresult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
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

    public CombinedReactionResult with(BiFunction<Float, Reaction, ReactionResult> result) {
        childResults.add(result.apply(moles, reaction));
        return this;
    };

    public List<ReactionResult> getChildren() {
        return childResults;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin) {
        for (ReactionResult childResult : childResults) {
            childResult.onBasinReaction(level, basin);
        };
    };

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController) {
        for (ReactionResult childResult : childResults) {
            childResult.onVatReaction(level, vatController);
        };
    };
    
};
