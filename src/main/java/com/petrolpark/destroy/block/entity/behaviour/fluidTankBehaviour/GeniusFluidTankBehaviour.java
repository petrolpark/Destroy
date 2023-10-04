package com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour;

import java.util.Map;
import java.util.function.Consumer;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class GeniusFluidTankBehaviour extends SmartFluidTankBehaviour {
    
    /**
     * Mostly copied from the {@link com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour Create source code}, 
     * switching the {@link com.simibubi.create.foundation.fluid.SmartFluidTank SmartFluidTanks} for {@link GeniusFluidTank GeniusFluidTanks},
     * which allow Mixtures to be added even if the NBT does not exactly match. 
     * @param type Whether this is an input or output tank
     * @param be The BlockEntity to which this behaviour is attached
     * @param tanks The number of tanks this behaviour should have
     * @param tankCapacity The capacity (in mB) of each tank
     * @param enforceVariety Don't really know what this does
     */
    public GeniusFluidTankBehaviour(BehaviourType<SmartFluidTankBehaviour> type, SmartBlockEntity be, int tanks, int tankCapacity, boolean enforceVariety) {
        super(type, be, tanks, tankCapacity, enforceVariety);
        IFluidHandler[] handlers = new IFluidHandler[tanks];
        for (int i = 0; i < tanks; i++) {
			GeniusTankSegment tankSegment = new GeniusTankSegment(tankCapacity);
			this.tanks[i] = tankSegment;
			handlers[i] = tankSegment.getTank();
		};
        capability = LazyOptional.of(() -> new InternalFluidHandler(handlers, enforceVariety));
    };

    public class GeniusTankSegment extends TankSegment {

        public GeniusTankSegment(int capacity) {
            super(capacity);
            tank = new GeniusFluidTank(capacity, f -> onFluidStackChanged());
        };

        protected GeniusFluidTank getTank() {
            return (GeniusFluidTank)tank;  
        };

    };

    public static class GeniusFluidTank extends SmartFluidTank {

        public GeniusFluidTank(int capacity, Consumer<FluidStack> updateCallback) {
            super(capacity, updateCallback);
        };

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int filled = super.fill(resource, action);
            if (filled == 0 && getSpace() > 0) { // If we wouldn't usually be able to insert, and we're not full (i.e. the Fluids are 'different')
                if (!DestroyFluids.MIXTURE.get().isSame(resource.getFluid()) || !DestroyFluids.MIXTURE.get().isSame(fluid.getFluid())) return 0;
                if (!resource.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND) || !fluid.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) return 0;
                Mixture existingMixture = Mixture.readNBT(fluid.getOrCreateTag().getCompound("Mixture"));
                Mixture addedMixture = Mixture.readNBT(resource.getOrCreateTag().getCompound("Mixture"));

                int amountOfMixtureAdded = Math.min(getSpace(), resource.getAmount());
                int existingAmount = fluid.getAmount();
                if (!action.simulate()) { // We don't need to do anything further if we're just simulating
                    Mixture newMixture = Mixture.mix(Map.of(existingMixture, (double)existingAmount / 1000d, addedMixture, (double)amountOfMixtureAdded / 1000d));
                    setFluid(MixtureFluid.of(existingAmount + amountOfMixtureAdded, newMixture));
                };

                return amountOfMixtureAdded; 
            };
            return filled;
        };

    };
    
};
