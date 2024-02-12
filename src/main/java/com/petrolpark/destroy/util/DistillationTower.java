package com.petrolpark.destroy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.BubbleCapBlockEntity;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.MixtureFluid;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class DistillationTower {

    private static final Object distillationRecipeKey = new Object();
    private static final int PROCESS_TIME = 100; // How often (in ticks) to attempt processing

    private BlockPos position; // The bottom of the Distillation Tower
    private List<BubbleCapBlockEntity> bubbleCaps;
    private DistillationRecipe lastRecipe;
    private int tick;

    public DistillationTower(Level level, BlockPos controllerPos) { // Create a new Distillation Tower from scratch
        position = controllerPos;
        bubbleCaps = new ArrayList<>();
        tick = PROCESS_TIME;
        int i = 0;
        while (true) {
            BlockEntity be = level.getBlockEntity(controllerPos.above(i));
            if (be == null || !(be instanceof BubbleCapBlockEntity bubbleCap)) {
                break;
            } else {
                addBubbleCap(bubbleCap);
            };
            i++;
        };
    };

    public DistillationTower(CompoundTag compound, Level level, BlockPos pos) { // Create a new Distillation Tower from NBT
        position = pos;
        tick = compound.getInt("Tick");
        int height = compound.getInt("Height");
        bubbleCaps = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            BlockEntity be = level.getBlockEntity(position.above(i));
            if (be == null || !(be instanceof BubbleCapBlockEntity bubbleCap)) {
                if (!level.isClientSide()) Destroy.LOGGER.warn("Could not load Distillation Tower starting at "+pos+". New height is "+(i+1));
                break;
            } else {
                addBubbleCap(bubbleCap);
            };
        };
    };

    public BlockPos getControllerPos() {
        return position;
    };
    
    public int getHeight() {
        return bubbleCaps.size();
    };

    public BubbleCapBlockEntity getControllerBubbleCap() {
        return getHeight() == 0 ? null : bubbleCaps.get(0);
    };

    /**
     * Adds a Bubble Cap to the top of the Distillation Tower, and updates the information that Bubble Cap has.
     * The Bubble Cap will only be added if it is not already in this Distillation Tower.
     */
    public void addBubbleCap(BubbleCapBlockEntity bubbleCap) {
        if (bubbleCaps.contains(bubbleCap)) return;
        bubbleCap.addToDistillationTower(this);
        bubbleCaps.add(bubbleCap);
        getControllerBubbleCap().sendData(); // Let the controller know it's changed
    };

    /**
     * Removes this Bubble Cap and all Bubble Caps above it from the Distillation Tower.
     * @param bubbleCapToRemove If not present in this Distillation Tower, nothing happens
     */
    public void removeBubbleCap(BubbleCapBlockEntity bubbleCapToRemove) {
        List<BubbleCapBlockEntity> newBubbleCaps = new ArrayList<>();
        for (BubbleCapBlockEntity bubbleCap : bubbleCaps) {
            if (bubbleCap == bubbleCapToRemove) {
                break;
            };
            newBubbleCaps.add(bubbleCap);
        };
        bubbleCaps = newBubbleCaps;
    };

    public void tick(Level level) {
        tick--;
        if (tick <= 0) {
            findRecipe(level);
            process();
            tick = PROCESS_TIME; // Reset counter
        };
    };

    public void findRecipe(Level level) {
        if (getControllerBubbleCap() == null) return;
        SmartFluidTank inputTank = getControllerBubbleCap().getTank();
        if (lastRecipe == null || !lastRecipe.getRequiredFluid().test(inputTank.getFluid())) { // If the Recipe has changed
            List<Recipe<?>> possibleRecipes = RecipeFinder.get(distillationRecipeKey, level, r -> r.getType() == DestroyRecipeTypes.DISTILLATION.getType()).stream().filter(r -> {
                DistillationRecipe recipe = (DistillationRecipe) r;
                if (!recipe.getRequiredFluid().test(inputTank.getFluid())) return false; // If there is insufficient input Fluid
                return true;
            }).collect(Collectors.toList());
            if (possibleRecipes.size() >= 1) {
                lastRecipe = (DistillationRecipe)possibleRecipes.get(0);
            } else {
                lastRecipe = null;
            };
        };
    };

    /**
     * Applies the current Recipe.
     * @return Whether the Recipe was successfully processed
     */
    public boolean process() {

        BubbleCapBlockEntity controller = getControllerBubbleCap();
        if (controller == null) return false;
        Level level = controller.getLevel();
        if (level == null) return false;

        // Mixtures
        FluidStack fluidStack = getControllerBubbleCap().getTank().getFluid();
        if (fluidStack.isEmpty()) return false;
        if (DestroyFluids.isMixture(fluidStack.getFluid()) && fluidStack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) {
            ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluidStack.getOrCreateTag().getCompound("Mixture"));
            List<FluidStack> fractions = getFractionsOfMixture(mixture, fluidStack.getAmount(), getHeight() - 1);
            if (fractions.size() <= 1) return false; // If the only result is the residue, there is no point distilling
            for (boolean simulate : Iterate.trueAndFalse) {

                // Check if resultant Fluids can fit
                int i = 0;
                for (FluidStack fraction : fractions) { // Ignore the first 'fraction', this is the residue and goes in the Reboiler
                    if (i == 0) {
                        i++;
                        continue;
                    }; 
                    BubbleCapBlockEntity bubbleCap = bubbleCaps.get(i);

                    SmartFluidTank tankToTryFill = simulate ? bubbleCap.getTank() : bubbleCap.getInternalTank(); // Try filling the visual tank, but actually fill the internal tank so the Bubble Cap isn't overfilled
                    if (simulate && !bubbleCap.getInternalTank().isEmpty()) return false; // If the tanks are still filling from a previous distillation, don't try distilling again

                    if (tankToTryFill.fill(fraction, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE) < fraction.getAmount()) return false; // If not all the Fluid can be added to this Bubble Cap return false
                    if (!simulate) bubbleCap.setTicksToFill(i * BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
                    i++;
                };
            };

            // If we've got to this point, the distillation is successful
            FluidStack fluidDrained = controller.getTank().drain(BubbleCapBlockEntity.getTankCapacity(), FluidAction.EXECUTE); // Drain the Reboiler of what is being distilled
            controller.getTank().fill(fractions.get(0), FluidAction.EXECUTE); // Fill the Reboiler with residue
            controller.particleFluid = fluidDrained.copy();
            controller.onDistill();
            return true;
        };

        // Recipes
        if (lastRecipe == null) return false;
        if (lastRecipe.getFractions() > getHeight() - 1) return false;

        FluidStack fluidDrained = FluidStack.EMPTY;
        for (boolean simulate : Iterate.trueAndFalse) { // First simulate to check if all the Fluids can actually fit, then execute if they do. 

            // Check if heat requirement is fulfilled
            if (!lastRecipe.getRequiredHeat().testBlazeBurner(BasinBlockEntity.getHeatLevelOf(controller.getLevel().getBlockState(controller.getBlockPos().below(1))))) return false;

            // Check if required Fluid is present
            int requiredFluidAmount = lastRecipe.getRequiredFluid().getRequiredAmount();
            fluidDrained = getControllerBubbleCap().getTank().drain(requiredFluidAmount, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
            if (fluidDrained.getAmount() < requiredFluidAmount) { // If there is not enough Fluid in the controller Bubble Cap
                return false;
            };

            // Check if resultant Fluids can fit
            for (int i = 0; i < lastRecipe.getFractions(); i++) {
                FluidStack distillate = lastRecipe.getFluidResults().get(i);
                BubbleCapBlockEntity bubbleCap = bubbleCaps.get(i + 1);

                SmartFluidTank tankToTryFill = simulate ? bubbleCap.getTank() : bubbleCap.getInternalTank(); // Try filling the visual tank, but actually fill the internal tank so the Bubble Cap isn't overfilled
                if (simulate && !bubbleCap.getInternalTank().isEmpty()) return false; // If the Tanks are still filling from a previous distillation, don't try distilling again
                
                if (tankToTryFill.fill(distillate, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE) < distillate.getAmount()) return false; // If not all the Fluid can be added to this Bubble Cap
                if (!simulate) bubbleCap.setTicksToFill(i * BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
            };

        };
        // If we've got to this point, the Recipe is being successfully processed
        controller.particleFluid = fluidDrained.copy();
        controller.onDistill();
        return true;
    };

    /**
     * Get the Fluid Stacks into which a Mixture Fluid Stack will separate when distilled.
     * @param mixture The Mixture being distilled
     * @param mixtureAmount The amount (in mB) of this Mixture
     * @param numberOfFractions The maximum number of fractions this should be separated into, not including any possible residue
     * @param heatLevel The Heat Level of the Blaze Burner (or lack thereof) which is heating the controller Bubble Cap
     * @return A list of Fluid Stacks of maximum size {@code numberOfFractions + 1}, with the first being the residue, and the rest being subsequent fractions
     */
    private List<FluidStack> getFractionsOfMixture(ReadOnlyMixture mixture, int mixtureAmount, int numberOfFractions) {
        List<FluidStack> fractions = new ArrayList<>(numberOfFractions);

        float roomTemperature = LevelPollution.getLocalTemperature(getControllerBubbleCap().getLevel(), getControllerPos());
        float maxTemperature = Math.max(getTemperatureForDistillationTower(getControllerBubbleCap().getLevel(), getControllerPos()), mixture.getTemperature());

        if (numberOfFractions == 0) return fractions;
        if (numberOfFractions == 1) return List.of(MixtureFluid.of(mixtureAmount, mixture));

        Mixture gasMixture = new Mixture().setTemperature(roomTemperature); // If there are gases, these do not separate by boiling point (as they never condense), so these are all grouped into one FluidStack
        boolean thereAreGases = false;

        Mixture residueMixture = new Mixture(); // If there are Molecules with a higher boiling point than the Mixture or Blaze Burner can reach, these do not separate by boiling point as they never evaporate

        List<Molecule> liquids = new ArrayList<>();

        float lowestBoilingPoint = roomTemperature;
        float highestBoilingPoint = roomTemperature;

        for (Molecule molecule : mixture.getContents(false)) {
            if (molecule.getBoilingPoint() < roomTemperature) { // Add all gases to the gas fraction
                thereAreGases = true;
                gasMixture.addMolecule(molecule, mixture.getConcentrationOf(molecule));
                continue;
            };
            if (molecule.getBoilingPoint() > maxTemperature) {
                residueMixture.addMolecule(molecule, mixture.getConcentrationOf(molecule));
                continue;
            };
            liquids.add(molecule);
            lowestBoilingPoint = Math.min(lowestBoilingPoint, molecule.getBoilingPoint());
            highestBoilingPoint = Math.max(highestBoilingPoint, molecule.getBoilingPoint());
        };

        if (thereAreGases) numberOfFractions--; // If there is a gas fraction, there must be one fewer liquid fractions

        float interval = (highestBoilingPoint - lowestBoilingPoint) / numberOfFractions; // Split the whole temperature range into (numberOfFraction) equal-sized temperature ranges...
        List<Mixture> liquidMixtures = new ArrayList<>(numberOfFractions);
        for (int i = 0; i < numberOfFractions; i++) liquidMixtures.add(new Mixture().setTemperature(roomTemperature));

        for (Molecule molecule : liquids) {
            checkEachFraction: for (int fraction = 0; fraction < numberOfFractions; fraction++) { // ...If a Molecule's BP is in the nth temperature range, it goes in the nth fraction
                if (molecule.getBoilingPoint() <= lowestBoilingPoint + ((fraction + 1) * interval)) {
                    liquidMixtures.get(fraction).addMolecule(molecule, mixture.getConcentrationOf(molecule));
                    break checkEachFraction;
                };
            };
        };

        int residueAmount = residueMixture.recalculateVolume(mixtureAmount);
        fractions.add(MixtureFluid.of(residueAmount, residueMixture)); // Add Residue regardless of whether there is anything there

        for (int fraction = 0; fraction < numberOfFractions; fraction++) { // Add all the liquid fractions
            Mixture fractionMixture = liquidMixtures.get(fraction);
            int amount = fractionMixture.recalculateVolume(mixtureAmount);
            if (amount == 0) continue;
            fractions.add(MixtureFluid.of(amount, fractionMixture));
        };

        if (thereAreGases) {
            int amount = gasMixture.recalculateVolume(mixtureAmount);
            fractions.add(MixtureFluid.of(amount, gasMixture)); // Add the gas fraction if necessary
        };

        return fractions;
    };

    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Height", getHeight());
        compound.putInt("Tick", tick);
        return compound;
    };

    /**
     * Get the temperature (in kelvins) to which this Heat Level will heat the Distillation Tower.
     */
    public static float getTemperatureForDistillationTower(Level level, BlockPos pos) {
        float roomTemperature = LevelPollution.getLocalTemperature(level, pos);
        float temperature = roomTemperature;
        HeatLevel heatLevel = BasinBlockEntity.getHeatLevelOf(level.getBlockState(pos.below()));
        if (heatLevel.name() == "FROSTING") temperature = 273f;
        switch (heatLevel) {
            case FADING:
                temperature = 350f;
                break;
            case KINDLED:
                temperature = 400;
                break;
            case SEETHING:
                temperature = 600;
                break;
            default:
        };
        return temperature;
    };

};
