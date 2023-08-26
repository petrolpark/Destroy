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
import com.simibubi.create.content.processing.recipe.HeatCondition;
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
                Destroy.LOGGER.warn("Could not load Distillation Tower starting at "+pos+". New height is "+(i+1));
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
        if (DestroyFluids.MIXTURE.get().isSame(fluidStack.getFluid()) && fluidStack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) {
            if (!HeatCondition.HEATED.testBlazeBurner(BasinBlockEntity.getHeatLevelOf(level.getBlockState(getControllerPos().below())))) return false; //TODO replace with something which looks at heat level and decides maximum temperature
            ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));
            List<FluidStack> fractions = getFractionsOfMixture(mixture, fluidStack.getAmount(), getHeight() - 1);
            for (boolean simulate : Iterate.trueAndFalse) {
                // Drain reboiler
                if (!simulate) getControllerBubbleCap().getTank().drain(BubbleCapBlockEntity.getTankCapacity(), FluidAction.EXECUTE);

                // Check if resultant Fluids can fit
                int i = 0;
                for (FluidStack fraction : fractions) {
                    BubbleCapBlockEntity bubbleCap = bubbleCaps.get(i + 1);

                    SmartFluidTank tankToTryFill = simulate ? bubbleCap.getTank() : bubbleCap.getInternalTank(); // Try filling the visual tank, but actually fill the internal tank so the Bubble Cap isn't overfilled
                    if (simulate && !bubbleCap.getInternalTank().isEmpty()) return false; // If the Tanks are still filling from a previous distillation, don't try distilling again

                    if (tankToTryFill.fill(fraction, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE) < fraction.getAmount()) return false; // If not all the Fluid can be added to this Bubble Cap return false;
                    if (!simulate) bubbleCap.setTicksToFill(i * BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate());
                    i++;
                };
            };
            return true;
        };

        // Recipes
        if (lastRecipe == null) return false;
        if (lastRecipe.getFractions() > getHeight() - 1) return false;

        FluidStack fluidDrained = FluidStack.EMPTY;
        for (boolean simulate : Iterate.trueAndFalse) { // First simulate to check if all the Fluids can actually fit, then execute if they do. 

            // Check if heat requirement is fulfilled
            if (!lastRecipe.getRequiredHeat().testBlazeBurner(BasinBlockEntity.getHeatLevelOf(level.getBlockState(getControllerPos().below())))) return false;

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
        controller.getTank().drain(lastRecipe.getRequiredFluid().getRequiredAmount(), FluidAction.EXECUTE);
        controller.particleFluid = fluidDrained.copy();
        controller.onDistill();
        return true;
    };

    private List<FluidStack> getFractionsOfMixture(ReadOnlyMixture mixture, int mixtureAmount, int numberOfFractions) {
        List<FluidStack> fractions = new ArrayList<>(numberOfFractions);

        if (numberOfFractions == 0) return fractions;
        if (numberOfFractions == 1) return List.of(MixtureFluid.of(mixtureAmount, mixture));

        //TODO account for the maximum temperature a Blaze Burner can supply

        Mixture gasMixture = new Mixture(); // If there are gases, these do not separate by boiling point (as they never condense), so these are all grouped into one FluidStack
        boolean thereAreGases = false;
        List<Molecule> liquids = new ArrayList<>();

        float roomTemperature = LevelPollution.getLocalTemperature(getControllerBubbleCap().getLevel(), getControllerPos());

        float lowestBoilingPoint = roomTemperature;
        float highestBoilingPoint = roomTemperature;

        for (Molecule molecule : mixture.getContents(false)) {
            if (molecule.getBoilingPoint() < roomTemperature) { // Add all gases to the gas fraction
                thereAreGases = true;
                gasMixture.addMolecule(molecule, mixture.getConcentrationOf(molecule));
                continue;
            } else {
                liquids.add(molecule);
            };
            lowestBoilingPoint = Math.min(lowestBoilingPoint, molecule.getBoilingPoint());
            highestBoilingPoint = Math.max(highestBoilingPoint, molecule.getBoilingPoint());
        };

        if (thereAreGases) numberOfFractions--; // If there is a gas fraction, there must be one fewer liquid fractions

        float interval = (highestBoilingPoint - lowestBoilingPoint) / numberOfFractions; // Split the whole temperature range into (numberOfFraction) equal-sized temperature ranges...
        List<Mixture> liquidMixtures = new ArrayList<>(numberOfFractions);
        for (int i = 0; i < numberOfFractions; i++) liquidMixtures.add(new Mixture());

        for (Molecule molecule : liquids) {
            checkEachFraction: for (int fraction = numberOfFractions - 1; fraction >= 0; fraction--) { // ...If a Molecule's BP is in the nth temperature range, it goes in the nth fraction
                if (molecule.getBoilingPoint() >= lowestBoilingPoint + (fraction * interval)) {
                    liquidMixtures.get(fraction).addMolecule(molecule, mixture.getConcentrationOf(molecule));
                    break checkEachFraction;
                };
            };
        };

        for (int fraction = 0; fraction < numberOfFractions; fraction++) { // Add all the liquid fractions
            Mixture fractionMixture = liquidMixtures.get(fraction);
            int amount = fractionMixture.recalculateVolume(mixtureAmount);
            if (amount == 0) continue;
            fractions.add(MixtureFluid.of(amount, fractionMixture, ""));
        };

        if (thereAreGases) {
            int amount = gasMixture.recalculateVolume(mixtureAmount);
            fractions.add(MixtureFluid.of(amount, gasMixture, "")); // Add the gas fraction if necessary
        };

        return fractions;
    };


    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Height", getHeight());
        compound.putInt("Tick", tick);
        return compound;
    };

};
