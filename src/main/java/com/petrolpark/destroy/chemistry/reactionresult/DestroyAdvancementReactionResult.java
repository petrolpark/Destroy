package com.petrolpark.destroy.chemistry.reactionResult;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Level;

/**
 * Awards a {@link com.petrolpark.destroy.advancement.DestroyAdvancements Destroy Advancement} when enough of a Reaction takes place.
 * It is recommended that addon creators make their own child class of {@link com.petrolpark.destroy.chemistry.ReactionResult ReactionResult} for Advancements rather than trying to piggyback off this.
 */
public class DestroyAdvancementReactionResult extends ReactionResult {

    private final DestroyAdvancements advancement;

    public DestroyAdvancementReactionResult(float moles, DestroyAdvancements advancement) {
        super(moles);
        this.advancement = advancement;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        basin.getBehaviour(DestroyAdvancementBehaviour.TYPE).awardDestroyAdvancement(advancement);
    };

    @Override
    public void onVatReaction(Level level, Mixture mixture) {
        // TODO Auto-generated method stub
    };
    
};
