package com.petrolpark.destroy.capability.block;

import com.petrolpark.destroy.fluid.DestroyFluids;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VatTankCapability extends CombinedTankWrapper {

    public VatTankCapability(IFluidHandler... fluidHandlers) {
		super(fluidHandlers);
	}

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return DestroyFluids.MIXTURE.get().isSame(stack.getFluid()) && super.isFluidValid(tank, stack);
    };
};
