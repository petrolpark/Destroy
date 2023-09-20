package com.petrolpark.destroy.fluid.pipeEffectHandler;

import java.util.Random;

import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.EvaporatingFluidS2CPacket;
import com.petrolpark.destroy.util.PollutionHelper;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import com.simibubi.create.content.fluids.OpenEndedPipe.IEffectHandler;

import net.minecraftforge.fluids.FluidStack;

public class MixtureOpenEndedPipeEffectHandler implements IEffectHandler {

    private static final Random random = new Random();

    @Override
    public boolean canApplyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        return fluid.getFluid().isSame(DestroyFluids.MIXTURE.get());
    };

    @Override
    public void applyEffects(OpenEndedPipe pipe, FluidStack fluid) {
        PollutionHelper.pollute(pipe.getWorld(), fluid);
        if (random.nextInt(20) == 0) DestroyMessages.sendToAllClients(new EvaporatingFluidS2CPacket(pipe.getOutputPos(), fluid));
    };
    
};
