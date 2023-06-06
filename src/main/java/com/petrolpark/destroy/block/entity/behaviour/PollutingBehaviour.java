package com.petrolpark.destroy.block.entity.behaviour;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.EvaporatingFluidS2CPacket;
import com.petrolpark.destroy.util.PollutionHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Behaviour for Tile Entities which contain Fluids and should release those Fluids into the atmosphere if destroyed.
 */
public class PollutingBehaviour extends BlockEntityBehaviour {

    public static BehaviourType<PollutingBehaviour> TYPE = new BehaviourType<>();

    public PollutingBehaviour(SmartBlockEntity be) {
        super(be);
    };

    @Override
    public void destroy() {
        List<FluidStack> fluidsToRelease = new ArrayList<>();
        IFluidHandler availableFluids = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (availableFluids == null) return;
        for (int tankNo = 0; tankNo < availableFluids.getTanks(); tankNo++) {
            FluidStack fluidStack = availableFluids.getFluidInTank(tankNo);
            if (fluidStack.isEmpty()) continue;
            fluidsToRelease.add(fluidStack);
        };

        pollute(getWorld(), getPos(), fluidsToRelease.toArray(new FluidStack[0]));

        super.destroy();
    };

    /**
     * Release the given Fluids into the environment and summon evaporation particles.
     * @param level The Level in which the pollution is taking place
     * @param blockPos The position from which the evaporation Particles should originate
     * @param fluidStacks The Fluids with which to pollute
     */
    public static void pollute(Level level, BlockPos blockPos, FluidStack ...fluidStacks) {
        if (level.isClientSide()) return;
        for (FluidStack fluidStack : List.of(fluidStacks)) {
            PollutionHelper.pollute(level, fluidStack);
            DestroyMessages.sendToAllClients(new EvaporatingFluidS2CPacket(blockPos, fluidStack));
        };
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
