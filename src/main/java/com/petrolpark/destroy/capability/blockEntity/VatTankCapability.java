package com.petrolpark.destroy.capability.blockEntity;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VatTankCapability extends CombinedTankWrapper {

    private final VatSideBlockEntity vatSide;

    public VatTankCapability(VatSideBlockEntity vatSide, IFluidHandler... fluidHandlers) {
		super(fluidHandlers);
        this.vatSide = vatSide;
	};

    @Override
    public int fill(FluidStack stack, FluidAction fluidAction) {
        VatControllerBlockEntity controller = vatSide.getController();
        if (controller == null || controller.isFull()) return 0;
        return super.fill(stack, fluidAction);
    };

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!vatSide.isPipeSubmerged(false, null)) return FluidStack.EMPTY; // TODO be able to extract gases
        return super.drain(resource, action);
    };

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (!vatSide.isPipeSubmerged(false, null)) return FluidStack.EMPTY; // TODO be able to extract gases
        return super.drain(maxDrain, action);
    };

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return DestroyFluids.MIXTURE.get().isSame(stack.getFluid()) && super.isFluidValid(tank, stack);
    };
};
