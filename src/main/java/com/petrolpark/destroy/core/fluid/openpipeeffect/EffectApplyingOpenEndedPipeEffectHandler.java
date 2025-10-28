package com.petrolpark.destroy.core.fluid.openpipeeffect;

import java.util.List;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

@MoveToPetrolparkLibrary
public class EffectApplyingOpenEndedPipeEffectHandler implements OpenPipeEffectHandler {

    protected final MobEffectInstance effect;
    protected final Fluid fluid;

    public EffectApplyingOpenEndedPipeEffectHandler(MobEffectInstance effect, Fluid fluid) {
        this.effect = effect;
        this.fluid = fluid;
    };

    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if (level.getGameTime() % 5 != 0) return;
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAffectedByPotions);
        for (LivingEntity entity : entities) entity.addEffect(effect);
    }
};
