package com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.petrolpark.destroy.block.entity.behaviour.fluidTankBehaviour.VatFluidTankBehaviour.VatTankSegment.VatFluidTank;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
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

    public ReadOnlyMixture getCombinedReadOnlyMixture() {
        Map<Molecule, Float> moleculesAndMoles = new HashMap<>();
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        int totalVolume = 0;

        FluidStack liquidStack = getLiquidHandler().getFluid();
        if (!liquidStack.isEmpty()) {
            ReadOnlyMixture liquidMixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, liquidStack.getOrCreateChildTag("Mixture"));
            liquidMixture.getContents(false).forEach(molecule -> moleculesAndMoles.merge(molecule, liquidMixture.getConcentrationOf(molecule) * liquidStack.getAmount(), (f1, f2) -> f1 + f2));
            totalVolume += liquidStack.getAmount();  
        };

        FluidStack gasStack = getGasHandler().getFluid();
        if (!gasStack.isEmpty()) {
            ReadOnlyMixture gasMixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, gasStack.getOrCreateChildTag("Mixture"));
            gasMixture.getContents(false).forEach(molecule -> moleculesAndMoles.merge(molecule, gasMixture.getConcentrationOf(molecule) * gasStack.getAmount(), (f1, f2) -> f1 + f2));
            totalVolume += gasStack.getAmount();
        };

        for (Entry<Molecule, Float> entry : moleculesAndMoles.entrySet()) {
            mixture.addMolecule(entry.getKey(), entry.getValue() / totalVolume); //TODO use different volume as this makes things slow
        };

        return mixture;
    };

    public void setMixture(Mixture mixture, int amount) {
        capability.ifPresent(fluidHandler -> {
            fluidHandler.drain(vatCapacity, FluidAction.EXECUTE);
            liquidFull = false;
            fluidHandler.fill(MixtureFluid.of(amount, mixture), FluidAction.EXECUTE);
        });
    };

    /**
     * Replace all the gas in the gas tank with room temperature and pressure air.
     * @return The gas that was previously stored
     */
    public FluidStack flush(float temperature) {
        FluidStack oldGas = getGasHandler().getFluid();
        getGasHandler().setFluid(DestroyFluids.air(vatCapacity - getLiquidHandler().getFluidAmount(), temperature));
        getGasHandler().flushed = true;
        return oldGas;
    };

    @Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		super.write(nbt, clientPacket);
        if (clientPacket) return;
		nbt.putBoolean("Full", liquidFull);
	};

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		super.read(nbt, clientPacket);
        if (clientPacket) return;
        liquidFull = nbt.getBoolean("Full");
        vatCapacity = getLiquidHandler().getCapacity();
	};

    public void updateGasVolume() {
        if (getGasHandler().isEmpty()) return;
        double freeSpace = vatCapacity - getLiquidHandler().getFluidAmount();
        double gasVolume = getGasHandler().getFluidAmount();
        Mixture mixture =  Mixture.readNBT(getGasHandler().getFluid().getOrCreateChildTag("Mixture"));
        mixture.scale((float)(freeSpace / gasVolume));
        getGasHandler().setFluid(MixtureFluid.of((int)freeSpace, mixture));
    };

    public class VatFluidHandler extends InternalFluidHandler {

        public VatFluidHandler(IFluidHandler[] handlers) {
            super(handlers, false);
        };

        @Override
		public int fill(FluidStack resource, FluidAction action) {
            if (liquidFull) return 0;
            if (!DestroyFluids.isMixture(resource)) return 0;

            boolean simulate = action == FluidAction.SIMULATE;

            Phases phases = Mixture.readNBT(resource.getOrCreateChildTag("Mixture")).separatePhases(resource.getAmount());
            double amountScale = 1f;
 
            if (phases.liquidVolume() > getLiquidHandler().getSpace()) {
                if (!simulate) liquidFull = true;
                amountScale = (phases.liquidVolume() - getLiquidHandler().getSpace()) / phases.liquidVolume();
            };

            // Add liquid - as this is a child of Genius Fluid Tank the mixing-in of the Mixture to the existing Mixture Fluid is already handled
            getLiquidHandler().fill(MixtureFluid.of((int)(phases.liquidVolume() * amountScale + 0.5d), phases.liquidMixture(), ""), action);

            // Add gas
            if (!simulate) {

                Map<Mixture, Double> mixtures = new HashMap<>(2);
                double combinedVolume = 0d;
                int freeSpace = vatCapacity - getLiquidHandler().getFluidAmount();

                if (!getGasHandler().isEmpty()) {
                    FluidStack existingGas = getGasHandler().getFluid();
                    mixtures.put(Mixture.readNBT(existingGas.getOrCreateChildTag("Mixture")), (double)existingGas.getAmount());
                    combinedVolume += existingGas.getAmount();
                };

                if (!phases.gasMixture().isEmpty()) {
                    mixtures.put(phases.gasMixture(), phases.gasVolume());
                    combinedVolume += phases.gasVolume();
                };

                Mixture combinedGasMixture = Mixture.mix(mixtures);
                if (combinedVolume > 0d && !combinedGasMixture.isEmpty()) {
                    combinedGasMixture.scale((float)(freeSpace / combinedVolume)); // Scale it so it takes up all available space not taken up by the liquid
                    getGasHandler().setFluid(MixtureFluid.of(freeSpace, combinedGasMixture));
                };
            };

            return (int)(resource.getAmount() * amountScale);
		};

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return DestroyFluids.isMixture(stack);
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

            /**
             * Whether this tank has been {@link VatFluidTankBehaviour#flush flushed}.
             */
            protected boolean flushed;

            public VatFluidTank(int capacity, boolean isForGas, Consumer<FluidStack> updateCallback) {
                super(capacity, updateCallback);
                this.isForGas = isForGas;
                flushed = false;
            };

            public boolean isEmptyOrFullOfAir() {
                return isEmpty() || flushed;
            };

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return DestroyFluids.isMixture(stack);
            };

            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                flushed = false;
                if (fluid.getAmount() < getCapacity() && !isForGas) liquidFull = false;
            };

            @Override
            public void setFluid(FluidStack stack) {
                super.setFluid(stack);
                flushed = false;
                if (stack.getAmount() < getCapacity() && !isForGas) liquidFull = false;
            };

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                return super.drain(maxDrain, action);
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
