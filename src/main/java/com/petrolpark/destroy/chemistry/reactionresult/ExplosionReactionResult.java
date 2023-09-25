package com.petrolpark.destroy.chemistry.reactionresult;

import java.util.function.BiFunction;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.util.ExplosionHelper;
import com.petrolpark.destroy.world.explosion.SmartExplosion;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplosionReactionResult extends ReactionResult {

    protected final BiFunction<Level, Vec3, SmartExplosion> explosionFactory;

    public static ExplosionReactionResult small(float moles, Reaction reaction) {
        return new ExplosionReactionResult(moles, reaction, (level, pos) -> new SmartExplosion(level, null, null, null, pos, 2, 0.5f));
    };

    public ExplosionReactionResult(float moles, Reaction reaction, BiFunction<Level, Vec3, SmartExplosion> explosionFactory) {
        super(moles, reaction);
        this.explosionFactory = explosionFactory;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin, Mixture mixture) {
        if (level instanceof ServerLevel serverLevel) ExplosionHelper.explode(serverLevel, explosionFactory.apply(serverLevel, VecHelper.getCenterOf(basin.getBlockPos())));
    };

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController, Mixture mixture) {
        vatController.explode(); //TODO swap to use explosive factory
    };
    
};
