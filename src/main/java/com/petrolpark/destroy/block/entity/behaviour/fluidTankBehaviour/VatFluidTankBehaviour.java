package com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour.VatTankSegment.VatFluidTank;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Mixture.Phases;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class VatFluidTankBehaviour extends GeniusFluidTankBehaviour {

    protected boolean liquidFull;
    protected int vatCapacity;

    public VatFluidTankBehaviour(SmartBlockEntity be, int vatCapacity) {
        super(SmartFluidTankBehaviour.TYPE, be, 2, vatCapacity, false);

        IFluidHandler[] handlers = new IFluidHandler[2];
        for (int i = 0; i < 2; i++) {
			VatTankSegment tankSegment = new VatTankSegment(vatCapacity, i == 1);
			this.tanks[i] = tankSegment;
			handlers[i] = tankSegment.getTank();
		};
        capability = LazyOptional.of(() -> new VatFluidHandler(handlers));

        liquidFull = false;

        this.vatCapacity = vatCapacity;
    };

    public static boolean isMixture(FluidStack fs) {
        return DestroyFluids.MIXTURE.get().isSame(fs.getFluid());
    };

    public VatFluidTank getLiquidHandler() {
        return getLiquidTank().getTank();
    };

    public VatFluidTank getGasHandler() {
        return getGasTank().getTank();
    };

    public VatTankSegment getLiquidTank() {
        return (VatTankSegment)super.getPrimaryTank();
    };

    public VatTankSegment getGasTank() {
        return (VatTankSegment)tanks[1];
    };

    public void setVatCapacity(int capacity) {
        vatCapacity = capacity;
        for (TankSegment tankSegment : tanks) {
            ((VatTankSegment)tankSegment).getTank().setCapacity(capacity);
        };
    };

    public boolean isFull() {
        return liquidFull;
    };

    /**
     * Get the Mixture with the {@link VatFluidTankBehaviour#vatCapacity volume of the whole Vat},
     * containing the same number of moles of all Molecules present in both the {@link VatFluidTankBehaviour#getLiquidHandler liquid}
     * and {@link VatFluidTankBehaviour#getGasHandler gas} phases.
     */
    public Mixture getCombinedMixture() {
        Map<Mixture, Double> mixtures = new HashMap<>(2);
        int totalVolume = 0;

        FluidStack liquidStack = getLiquidHandler().getFluid();
        if (!liquidStack.isEmpty()) {
            mixtures.put(Mixture.readNBT(liquidStack.getOrCreateChildTag("Mixture")), liquidStack.getAmount() / 1000d);
            totalVolume += liquidStack.getAmount();  
        };

        FluidStack gasStack = getGasHandler().getFluid();
        if (!gasStack.isEmpty()) {
            mixtures.put(Mixture.readNBT(gasStack.getOrCreateChildTag("Mixture")), gasStack.getAmount() / 1000d);
            totalVolume += gasStack.getAmount();
        };

        Mixture mixture = Mixture.mix(mixtures);
        mixture.scale((float)vatCapacity / (float)totalVolume); //TODO use different volume as this makes things slowww
        return mixture;
    };

    public void setMixture(Mixture mixture, int amount) {
        capability.ifPresent(fluidHandler -> {
            fluidHandler.drain(vatCapacity, FluidAction.EXECUTE);
            liquidFull = false;
            fluidHandler.fill(MixtureFluid.of(amount, mixture), FluidAction.EXECUTE);
        });
    };

    @Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
		nbt.putBoolean("Full", liquidFull);
	};

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
        liquidFull = nbt.getBoolean("Full");
        vatCapacity = getLiquidHandler().getCapacity();
	};

    public class VatFluidHandler extends InternalFluidHandler {

        public VatFluidHandler(IFluidHandler[] handlers) {
            super(handlers, false);
        };

        @Override
		public int fill(FluidStack resource, FluidAction action) {
            if (liquidFull) return 0;
            if (!resource.getFluid().isSame(DestroyFluids.MIXTURE.get())) return 0;

            boolean simulate = action == FluidAction.SIMULATE;

            Phases phases = Mixture.readNBT(resource.getOrCreateChildTag("Mixture")).separatePhases(resource.getAmount());
            double amountScale = 1f;

            if (phases.liquidVolume() > getLiquidHandler().getSpace()) {
                if (simulate) liquidFull = true;
                amountScale = (phases.liquidVolume() - getLiquidHandler().getSpace()) / phases.liquidVolume();
            };

            // Add liquid - as this is a child of Genius Fluid Tank the mixing-in of the Mixture to the existing Mixture Fluid is already handled
            getLiquidHandler().fill(MixtureFluid.of((int)(phases.liquidVolume() * amountScale), phases.liquidMixture(), ""), action);
            
            // Add gas
            if (!simulate && !phases.gasMixture().isEmpty()) {
                FluidStack existingGas = getGasHandler().getFluid();

                int freeSpace = vatCapacity - getLiquidHandler().getFluidAmount();

                Mixture combinedGasMixture;
                double combinedVolume;

                if (existingGas.isEmpty()) { // If gas is being added for the first time
                    combinedGasMixture = phases.gasMixture();
                    combinedVolume = phases.gasVolume();
                } else { // If there is already gas
                    combinedGasMixture = Mixture.mix(Map.of(phases.gasMixture(), phases.gasVolume(), Mixture.readNBT(existingGas.getOrCreateChildTag("Mixture")), (double)existingGas.getAmount()));
                    combinedVolume = phases.gasVolume() + existingGas.getAmount();
                };

                combinedGasMixture.scale((float)(freeSpace / combinedVolume)); // Scale it so it takes up all available space not taken up by the liquid
                getGasHandler().setFluid(MixtureFluid.of(freeSpace, combinedGasMixture));
            };
			
            liquidFull = false;
            return (int)(resource.getAmount() * amountScale);
		};

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return isMixture(stack);
        };

    };

    public class VatTankSegment extends GeniusTankSegment {

        public VatTankSegment(int capacity, boolean isForGas) {
            super(capacity);
            tank = new VatFluidTank(capacity, isForGas, f -> onFluidStackChanged());
        };

        public VatFluidTank getTank() {
            return (VatFluidTank)tank;
        };

        public class VatFluidTank extends GeniusFluidTank {

            private final boolean isForGas;

            public VatFluidTank(int capacity, boolean isForGas, Consumer<FluidStack> updateCallback) {
                super(capacity, updateCallback);
                this.isForGas = isForGas;
            };

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                FluidStack drained = super.drain(resource, action);
                if (!isForGas && drained.getAmount() > 0 && action == FluidAction.EXECUTE) liquidFull = false;
                return drained;
            };

            @NotNull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                FluidStack drained = super.drain(maxDrain, action);
                if (!isForGas && drained.getAmount() > 0 && action == FluidAction.EXECUTE) liquidFull = false;
                return drained;
            };

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return isMixture(stack);
            };
        };
    };


    /**
     * @deprecated Use {@link VatFluidTankBehaviour#getLiquidHandler()} and {@link VatFluidTankBehaviour#getGasHandler()} instead.
     */
    @Deprecated
    @Override
    public SmartFluidTank getPrimaryHandler() {
        return null;
    };

    /**
     * @deprecated Use {@link VatFluidTankBehaviour#getLiquidTank()} and {@link VatFluidTankBehaviour#getGasTank()} instead.
     */
    @Deprecated
    @Override
    public TankSegment getPrimaryTank() {
        return null;
    };
    
};
