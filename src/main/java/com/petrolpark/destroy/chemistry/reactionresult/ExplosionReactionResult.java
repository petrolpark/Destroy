package com.petrolpark.destroy.chemistry.reactionresult;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionReactionResult extends ReactionResult {

    protected final Explosion explosion;

    public ExplosionReactionResult(float moles, Reaction reaction, Explosion explosion) {
        super(moles, reaction);
        this.explosion = explosion;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture) {
        // TODO Auto-generated method stub
    };
    
};
