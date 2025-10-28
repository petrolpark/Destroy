package com.petrolpark.destroy.core.fluid.openpipeeffect;

import com.simibubi.create.impl.effect.LavaEffectHandler;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

public class BurningOpenEndedPipeEffectHandler extends LavaEffectHandler {

    protected final Fluid fluid;

    public BurningOpenEndedPipeEffectHandler(Fluid fluid) {
        this.fluid = fluid;
    };

    @Override
    public void apply(Level level, AABB area, FluidStack fluid) {
        if(!canApplyEffects(fluid)) return;
        super.apply(level, area, fluid);
    }

    public boolean canApplyEffects(FluidStack fluid) {
        return fluid.getFluid().isSame(this.fluid);
    };
};
