package com.petrolpark.destroy.chemistry.legacy.reactionresult;

import java.util.function.BiFunction;

import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReactionResult;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity;
import com.petrolpark.destroy.core.explosion.SmartExplosion;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExplosionReactionResult extends ReactionResult {

    protected final BiFunction<Level, Vec3, SmartExplosion> explosionFactory;

    public static ExplosionReactionResult small(Float moles, LegacyReaction reaction) {
        return new ExplosionReactionResult(moles, reaction, (level, pos) -> new SmartExplosion(level, null, null, null, pos, 2, 0.5f));
    };

    public ExplosionReactionResult(float moles, LegacyReaction reaction, BiFunction<Level, Vec3, SmartExplosion> explosionFactory) {
        super(moles, reaction);
        this.explosionFactory = explosionFactory;
    };

    @Override
    public void onBasinReaction(Level level, BasinBlockEntity basin) {
        if (level instanceof ServerLevel serverLevel) SmartExplosion.explode(serverLevel, explosionFactory.apply(serverLevel, VecHelper.getCenterOf(basin.getBlockPos())));
    };

    @Override
    public void onVatReaction(Level level, VatControllerBlockEntity vatController) {
        vatController.explode(explosionFactory);
    };
    
};
