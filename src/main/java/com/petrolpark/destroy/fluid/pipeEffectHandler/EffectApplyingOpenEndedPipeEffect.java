package com.petrolpark.destroy.fluid.pipeEffectHandler;

import java.util.List;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.content.fluids.OpenEndedPipe.IEffectHandler;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EffectApplyingOpenEndedPipeEffect implements IEffectHandler {

    protected final MobEffectInstance effect;
    protected final Fluid fluid;

    public EffectApplyingOpenEndedPipeEffect(MobEffectInstance effect, Fluid fluid) {
        this.effect = effect;
        this.fluid = fluid;
    };

    @Override
    public boolean canApplyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        return fluid.getFluid().isSame(this.fluid);
    };

    @Override
    public void applyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        Level world = pipe.getWorld();
        if (world.getGameTime() % 5 != 0) return;
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, pipe.getAOE(), LivingEntity::isAffectedByPotions);
        for (LivingEntity entity : entities) entity.addEffect(effect);
    };
    
};
