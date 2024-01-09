package com.petrolpark.destroy.capability.blockEntity;

import com.petrolpark.destroy.block.entity.VatControllerBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class VatSideTankCapability extends CombinedTankWrapper {

    private final VatSideBlockEntity vatSide;

    public VatSideTankCapability(VatSideBlockEntity vatSide, IFluidHandler liquidOutput, IFluidHandler gasOutput, IFluidHandler input) {
		super(new IFluidHandler[]{liquidOutput, gasOutput, input});
        this.vatSide = vatSide;
	};

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return DestroyFluids.isMixture(stack);
    };

    public IFluidHandler getLiquidOutput() {
        return itemHandler[0];
    };

    public IFluidHandler getGasOutput() {
        return itemHandler[1];
    };

    public IFluidHandler getInput() {
        return itemHandler[2];
    };

    @Override
    public int fill(FluidStack stack, FluidAction fluidAction) {
        VatControllerBlockEntity controller = vatSide.getController();
        if (controller == null || !controller.canFitFluid()) return 0;
        return getInput().fill(stack, fluidAction);
    };

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = (vatSide.isPipeSubmerged(false, null) ? getLiquidOutput() : getGasOutput()).drain(resource, action);
        updateVatGasVolume(drained, action);
        return drained;
    };

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = (vatSide.isPipeSubmerged(false, null) ? getLiquidOutput() : getGasOutput()).drain(maxDrain, action);
        updateVatGasVolume(drained, action);
        return drained;
    };

    @SuppressWarnings("null")
    protected void updateVatGasVolume(FluidStack drained, FluidAction action) {
        if (action == FluidAction.EXECUTE && !drained.isEmpty() && vatSide.getController() != null && !vatSide.getLevel().isClientSide()) {
            vatSide.getController().updateCachedMixture();
            vatSide.getController().updateGasVolume();
        };
    };
};
