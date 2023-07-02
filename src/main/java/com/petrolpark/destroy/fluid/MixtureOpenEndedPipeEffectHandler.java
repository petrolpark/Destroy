package com.petrolpark.destroy.fluid;

import com.petrolpark.destroy.block.entity.behaviour.PollutingBehaviour;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.content.fluids.OpenEndedPipe.IEffectHandler;

import net.minecraftforge.fluids.FluidStack;

public class MixtureOpenEndedPipeEffectHandler implements IEffectHandler {

    @Override
    public boolean canApplyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        return fluid.getFluid().isSame(DestroyFluids.MIXTURE.get());
    };

    @Override
    public void applyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        PollutingBehaviour.pollute(pipe.getWorld(), pipe.getOutputPos(), fluid);
    };
    
};
