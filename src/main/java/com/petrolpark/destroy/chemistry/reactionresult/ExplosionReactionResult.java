package com.petrolpark.destroy.chemistry.reactionResult;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionReactionResult extends ReactionResult {

    protected final Explosion explosion;

    public ExplosionReactionResult(float moles, Explosion explosion) {
        super(moles);
        this.explosion = explosion;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        // TODO Auto-generated method stub
    };

    @Override
    public void onVatReaction(Level level, Mixture mixture) {
        // TODO Auto-generated method stub
    };
    
};
