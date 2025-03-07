package com.petrolpark.destroy.content.processing.centrifuge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.DestroyRecipeTypes;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.LegacyMixture.Phases;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.processing.centrifuge.potion.PotionSeparationRecipes;
import com.petrolpark.destroy.core.block.entity.IDirectionalOutputFluidBlockEntity;
import com.petrolpark.destroy.core.block.entity.IHaveLabGoggleInformation;
import com.petrolpark.destroy.core.chemistry.MixtureContentsDisplaySource;
import com.petrolpark.destroy.core.data.advancement.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour;
import com.petrolpark.destroy.core.pollution.PollutingBehaviour;
import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;

public class CentrifugeBlockEntity extends KineticBlockEntity implements IDirectionalOutputFluidBlockEntity, IHaveLabGoggleInformation {

    private static final Object centrifugationRecipeKey = new Object();

    private SmartFluidTankBehaviour inputTank, denseOutputTank, lightOutputTank;
    protected LazyOptional<IFluidHandler> allFluidCapability;

    protected DestroyAdvancementBehaviour advancementBehaviour;
    protected PollutingBehaviour pollutingBehaviour;

    private Direction denseOutputTankFace;

    public int timer;
    private CentrifugationRecipe lastRecipe;

    private boolean pondering; // Whether this Centrifuge is in a Ponder Scene

    public CentrifugeBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        denseOutputTankFace = state.getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        pondering = false;
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        inputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, getEachTankCapacity(), true)
            .whenFluidUpdates(this::onFluidStackChanged);
        denseOutputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, getEachTankCapacity(), true)
            .whenFluidUpdates(this::onFluidStackChanged)
            .forbidInsertion();
        lightOutputTank = new GeniusFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 1, getEachTankCapacity(), true)
            .whenFluidUpdates(this::onFluidStackChanged)
            .forbidInsertion();
        behaviours.addAll(List.of(inputTank, denseOutputTank, lightOutputTank));

        allFluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(inputTank.getCapability().orElse(null), denseOutputTank.getCapability().orElse(null), lightOutputTank.getCapability().orElse(null));
        });

        advancementBehaviour = new DestroyAdvancementBehaviour(this, DestroyAdvancementTrigger.USE_CENTRIFUGE);
        behaviours.add(advancementBehaviour);

        pollutingBehaviour = new PollutingBehaviour(this);
        behaviours.add(pollutingBehaviour);
    };

    /**
     * Attempts to rotate the Centrifuge so that it faces a new face which also has a Pipe. If no Pipe is available, just rotates it anyway.
     * @param shouldSwitch Whether the rotation should prioritise switching faces or staying on the current face
     * @return Whether the Centrifuge was rotated
     */
    @SuppressWarnings("null")
    public boolean attemptRotation(boolean shouldSwitch) {
        if (!hasLevel()) return false;
        if (getLevel().setBlock(getBlockPos(), getBlockState().setValue(CentrifugeBlock.DENSE_OUTPUT_FACE, refreshDirection(this, shouldSwitch ? denseOutputTankFace.getClockWise() : denseOutputTankFace, getDenseOutputTank(), true)), 6)) { // If the output Direction can be successfully changed
            denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
            notifyUpdate(); // Block State has changed
            return true;
        };
        return false;
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (!hasLevel()) return; // Don't do anything if we're not in a Level
        if (getSpeed() == 0) return; // Don't do anything without rotational power
        if (isTankFull(getDenseOutputTank()) || isTankFull(getLightOutputTank())) return; // Don't do anything if output is full
        if (timer > 0) {
            timer -= getProcessingSpeed();
            if (getLevel().isClientSide()) { // It thinks getLevel() can be null (it can't)
                spawnParticles();
                return;
            };
            if (timer <= 0) {
                process();
            };
            sendData();
            return;
        };
        if (inputTank.isEmpty()) return; // Don't do anything more if input Tank is empty

        if (lastRecipe == null || !lastRecipe.getRequiredFluid().test(getInputTank().getFluid())) { // If the Recipe has changed
            FluidStack inputFluidStack = getInputTank().getFluid();

            // Standard recipes
            List<Recipe<?>> possibleRecipes = RecipeFinder.get(centrifugationRecipeKey, getLevel(), r -> r.getType() == DestroyRecipeTypes.CENTRIFUGATION.getType()).stream().filter(r -> {
                CentrifugationRecipe recipe = (CentrifugationRecipe) r;
                if (!recipe.isValidAt(getLevel(), getBlockPos())) return false; // Biome-specific recipes
                if (!recipe.getRequiredFluid().test(inputFluidStack)) return false; // If there is insufficient input Fluid
                if (!canFitFluidInTank(recipe.getDenseOutputFluid(), getDenseOutputTank()) || !canFitFluidInTank(recipe.getLightOutputFluid(), getLightOutputTank())) return false; // If the outputs can't fit
                return true;
            }).collect(Collectors.toList());

            // Potion separation
            if (AllConfigs.server().recipes.allowBrewingInMixer.get() && inputFluidStack.getFluid().isSame(AllFluids.POTION.get()) && inputFluidStack.hasTag()) {
                Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(inputFluidStack.getOrCreateTag().getString("Potion")));
                BottleType bottleType = NBTHelper.readEnum(inputFluidStack.getOrCreateTag(), "BottleType", BottleType.class);
                if (potion != null) {
                    CentrifugationRecipe potionSeparationRecipe = PotionSeparationRecipes.ALL.get(Pair.of(potion, bottleType));
                    if (potionSeparationRecipe != null)
                    possibleRecipes.add(potionSeparationRecipe);
                };
            };

            if (possibleRecipes.size() >= 1) {
                lastRecipe = (CentrifugationRecipe)possibleRecipes.get(0);
            } else { // If no recipe could be found
                lastRecipe = null;
            };
        };

        if (lastRecipe == null) {
            timer = 100; // If we have no Recipe, don't try checking again for another 100 ticks
        } else {
            timer = lastRecipe.getProcessingDuration();
        };

        sendData();
    };

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        timer = compound.getInt("Timer");
        getInputTank().readFromNBT(compound.getCompound("InputTank"));
        getDenseOutputTank().readFromNBT(compound.getCompound("DenseOutputTank"));
        getLightOutputTank().readFromNBT(compound.getCompound("LightOutputTank"));
        denseOutputTankFace = getBlockState().getValue(CentrifugeBlock.DENSE_OUTPUT_FACE);
        super.read(compound, clientPacket);
    };

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("Timer", timer);
        compound.put("InputTank", getInputTank().writeToNBT(new CompoundTag()));
        compound.put("DenseOutputTank", getDenseOutputTank().writeToNBT(new CompoundTag()));
        compound.put("LightOutputTank", getLightOutputTank().writeToNBT(new CompoundTag()));
        super.write(compound, clientPacket);
    };

    public int getProcessingSpeed() {
        return Mth.clamp((int) Math.abs(getSpeed() / 16f), 1, 512);
    };

    public SmartFluidTank getInputTank() {
        return inputTank.getPrimaryHandler();
    };

    public SmartFluidTank getDenseOutputTank() {
        return denseOutputTank.getPrimaryHandler();
    };

    public SmartFluidTank getLightOutputTank() {
        return lightOutputTank.getPrimaryHandler();
    };

    public void process() {
        if (lastRecipe == null) { // If there is no Recipe
            if (DestroyFluids.isMixture(getInputTank().getFluid())) { // If there are Fluids to Centrifuge

                boolean debug = false;
                LegacyMixture mixture = LegacyMixture.readNBT(getInputTank().getFluid().getOrCreateChildTag("Mixture"));

                if (mixture == null) return;
                if (!(DestroyFluids.isMixture(getDenseOutputTank().getFluid()) || getDenseOutputTank().isEmpty()) || !(DestroyFluids.isMixture(getLightOutputTank().getFluid()) || getLightOutputTank().isEmpty())) return; // Don't go any further if either output tank can't take Mixture
                
                int amount = IntStream.of(new int[]{getInputTank().getFluidAmount(), getDenseOutputTank().getSpace() * 2, getLightOutputTank().getSpace() * 2}).min().getAsInt(); // Determine how much can be processed
                if (amount == 0) return; // If either of the two output tanks can't fit anything at all, give up
                float totalVolume = amount / 1000f;

                if (debug) Destroy.LOGGER.info("Total starting volume: "+totalVolume);

                Map<Pair<LegacySpecies, Boolean>, Float> phasedMoleculesRemainingMoles = new HashMap<>();
                Map<Pair<LegacySpecies, Boolean>, Float> phasedMoleculesRemainingVolumes = new HashMap<>();
                Map<Pair<LegacySpecies, Boolean>, Float> phasedMoleculesMolesInLightMixture = new HashMap<>();
                Map<Pair<LegacySpecies, Boolean>, Float> phasedMoleculesMolesInDenseMixture = new HashMap<>();

                Phases phases = mixture.separatePhases(totalVolume);
                float liquidVolume = (float)(double)phases.liquidVolume();
                float gasVolume = totalVolume - liquidVolume;
                LegacyMixture gasMixture = phases.gasMixture();
                gasMixture.scale(gasVolume);
                LegacyMixture liquidMixture = phases.liquidMixture();

                for (LegacySpecies molecule : liquidMixture.getContents(false)) {
                    Pair<LegacySpecies, Boolean> phasedMolecule = Pair.of(molecule, false);
                    float moles = liquidMixture.getConcentrationOf(molecule) * liquidVolume;
                    phasedMoleculesRemainingVolumes.put(phasedMolecule, moles / molecule.getPureConcentration());
                    phasedMoleculesRemainingMoles.put(phasedMolecule, moles);
                };
                
                float totalGasConcentration = gasMixture.getTotalConcentration();
                for (LegacySpecies molecule : gasMixture.getContents(false)) {
                    Pair<LegacySpecies, Boolean> phasedMolecule = Pair.of(molecule, true);
                    float moles = gasMixture.getConcentrationOf(molecule) * gasVolume;
                    phasedMoleculesRemainingVolumes.put(phasedMolecule, moles / totalGasConcentration); 
                    phasedMoleculesRemainingMoles.put(phasedMolecule, moles);
                };

                if (debug) {
                    float sumVolume = 0f;
                    for (float volume : phasedMoleculesRemainingVolumes.values()) sumVolume += volume;
                    Destroy.LOGGER.info("Total volume of all Molecules: "+sumVolume);
                };

                List<Pair<LegacySpecies, Boolean>> orderedPhasedMolecules = new ArrayList<>(phasedMoleculesRemainingVolumes.keySet());
                Collections.sort(orderedPhasedMolecules, (p1, p2) -> {
                    LegacySpecies m1 = p1.getFirst();
                    LegacySpecies m2 = p2.getFirst();
                    return Float.compare(
                        mixture.getConcentrationOf(m2) * m2.getMass() / phasedMoleculesRemainingVolumes.get(p2), // This quantity is proportional to the density of Molecule 1 in this Mixture
                        mixture.getConcentrationOf(m1) * m1.getMass() / phasedMoleculesRemainingVolumes.get(p1)
                    );
                });

                float volumeOfDenseMixture = 0f;

                splitEachMolecule: for (Pair<LegacySpecies, Boolean> phasedMolecule : orderedPhasedMolecules) {
                    
                    LegacySpecies molecule = phasedMolecule.getFirst();
                    if (!phasedMoleculesRemainingMoles.containsKey(phasedMolecule)) continue splitEachMolecule; // Don't try if this Molecule has already been removed (implying it is an ion)
                    float moles = phasedMoleculesRemainingMoles.get(phasedMolecule);

                    if (debug) {
                        Destroy.LOGGER.info("Now splitting '"+phasedMolecule.getFirst().getFullID()+"', gas = "+phasedMolecule.getSecond());
                        Destroy.LOGGER.info("   Moles remaining: "+moles);
                    };

                    if (molecule.getCharge() == 0) {

                        float volume = phasedMoleculesRemainingVolumes.get(phasedMolecule);
                        float volumeInDenseMixture = Math.min(volume, (totalVolume / 2f) - volumeOfDenseMixture);
                        float propotionInDenseMixture = volumeInDenseMixture / volume;

                        if (debug) {
                            Destroy.LOGGER.info("   Total volume: "+volume);
                            Destroy.LOGGER.info("   Volume which could fit in the dense Mixture: "+volumeInDenseMixture);
                            Destroy.LOGGER.info("   Current volume of dense Mixture: "+volumeOfDenseMixture);
                        };

                        phasedMoleculesMolesInDenseMixture.put(phasedMolecule, moles * propotionInDenseMixture);
                        phasedMoleculesMolesInLightMixture.put(phasedMolecule, moles * (1 - propotionInDenseMixture));
                        volumeOfDenseMixture += volumeInDenseMixture;
                        phasedMoleculesRemainingVolumes.replace(phasedMolecule, 0f); // All of any given neutral Molecule will be used up at once. Evens so, we don't strictly need to set this.
                        phasedMoleculesRemainingMoles.replace(phasedMolecule, 0f);
                    
                    } else {

                        findCounterions: while (Optional.ofNullable(phasedMoleculesRemainingVolumes.get(phasedMolecule)).orElse(0f) > 0f) {

                            Pair<LegacySpecies, Boolean> phasedCounterion = getNextIon(orderedPhasedMolecules, phasedMoleculesRemainingVolumes, molecule.getCharge() < 0);
                            LegacySpecies counterion = phasedCounterion.getFirst();
                            if (counterion == null) {
                                Destroy.LOGGER.error("Tried to centrifuge charge-imbalanced Mixture");
                                break findCounterions;
                            };

                            float counterionMolesRequired = -moles * (float)counterion.getCharge() / (float)molecule.getCharge();
                            float proportionAvailable = phasedMoleculesRemainingMoles.get(phasedCounterion) / counterionMolesRequired;

                            if (debug) {
                                Destroy.LOGGER.info("   Trying counter-ion '"+counterion.getFullID()+"', gas = "+phasedCounterion.getSecond());
                                Destroy.LOGGER.info("       Moles of counter-ion required to fully balance: "+counterionMolesRequired);
                                Destroy.LOGGER.info("       Molar quantity proportional to what is required: "+proportionAvailable);
                                Destroy.LOGGER.info("       Current volume of dense Mixture: "+volumeOfDenseMixture);
                            };

                            if (proportionAvailable <= 0f) break findCounterions; // This should never happen
                            
                            float volumeOfMoleculeUsed;
                            float molesOfMoleculeUsed;
                            float volumeOfCounterionUsed;
                            float molesOfCounterionUsed;
                            
                            if (proportionAvailable > 1f) { // If we have more than enough of this counterion to balance this ion, all of this Molecule gets used up
                                volumeOfMoleculeUsed = phasedMoleculesRemainingVolumes.get(phasedMolecule);
                                molesOfMoleculeUsed = moles;
                                volumeOfCounterionUsed = phasedMoleculesRemainingVolumes.get(phasedCounterion) / proportionAvailable;
                                molesOfCounterionUsed = phasedMoleculesRemainingMoles.get(phasedCounterion) / proportionAvailable;
                            } else { // If we have just the right amount, or need more
                                volumeOfMoleculeUsed = phasedMoleculesRemainingVolumes.get(phasedMolecule) * proportionAvailable;
                                molesOfMoleculeUsed = moles * proportionAvailable;
                                volumeOfCounterionUsed = phasedMoleculesRemainingVolumes.get(phasedCounterion);
                                molesOfCounterionUsed = phasedMoleculesRemainingMoles.get(phasedCounterion);
                            };

                            if (debug) {
                                Destroy.LOGGER.info("           Volume of Molecule which will be added: "+volumeOfMoleculeUsed);
                                Destroy.LOGGER.info("           Moles of Molecule which will be added: "+molesOfMoleculeUsed);
                                Destroy.LOGGER.info("           Volume of counter-ion which will be added: "+volumeOfCounterionUsed);
                                Destroy.LOGGER.info("           Moles of counter-ion which will be added: "+molesOfCounterionUsed);
                            };

                            float combinationVolume = volumeOfMoleculeUsed + volumeOfCounterionUsed;
                            float combinedVolumeInDenseMixture = Math.min(combinationVolume, (totalVolume / 2f) - volumeOfDenseMixture);
                            float proportionInDenseMixture = combinedVolumeInDenseMixture / combinationVolume;

                            // Add this Molecule and the counter-ion to the result Mixtures
                            phasedMoleculesMolesInDenseMixture.put(phasedMolecule, molesOfMoleculeUsed * proportionInDenseMixture);
                            phasedMoleculesMolesInDenseMixture.put(phasedCounterion, molesOfCounterionUsed * proportionInDenseMixture);
                            phasedMoleculesMolesInLightMixture.put(phasedMolecule, molesOfMoleculeUsed * (1- proportionInDenseMixture));
                            phasedMoleculesMolesInLightMixture.put(phasedCounterion, molesOfCounterionUsed * (1 - proportionInDenseMixture));

                            // Decrement the remaining numbers of Molecules
                            phasedMoleculesRemainingVolumes.compute(phasedMolecule, (pm, volume) -> {
                                float newVolume = volume - volumeOfMoleculeUsed;
                                return newVolume <= 1 / 256f / 256f ? null : newVolume;
                            });
                            phasedMoleculesRemainingVolumes.compute(phasedCounterion, (pm, volume) -> {
                                float newVolume = volume - volumeOfCounterionUsed;
                                return newVolume <= 1 / 256f / 256f ? null : newVolume;
                            });
                            phasedMoleculesRemainingMoles.compute(phasedMolecule, (pm, mol) -> {
                                float newMol = mol - molesOfMoleculeUsed;
                                return newMol <= 1 / 256f / 256f ? null : newMol;
                            });
                            phasedMoleculesRemainingMoles.compute(phasedCounterion, (pm, mol) -> {
                                float newMol = mol - molesOfCounterionUsed;
                                return newMol <= 1 / 256f / 256f ? null : newMol;
                            });

                            // Increment the amount of dense mixture
                            volumeOfDenseMixture += combinedVolumeInDenseMixture;
                        };
                    };
                };

                LegacyMixture denseMixture = new LegacyMixture();
                denseMixture.setTemperature(mixture.getTemperature());
                LegacyMixture lightMixture = new LegacyMixture();
                lightMixture.setTemperature(mixture.getTemperature());

                for (Pair<LegacyMixture, Map<Pair<LegacySpecies, Boolean>, Float>> mixtureAndMap : List.of(Pair.of(denseMixture, phasedMoleculesMolesInDenseMixture), Pair.of(lightMixture, phasedMoleculesMolesInLightMixture))) {
                    LegacyMixture resultMixture = mixtureAndMap.getFirst();
                    Map<Pair<LegacySpecies, Boolean>, Float> map = mixtureAndMap.getSecond();
                    Map<LegacySpecies, Couple<Float>> moleculeStates = new HashMap<>(map.size()); // Couple is of the form <gas moles, liquid moles>
                    
                    // Determine the number of moles of gas and the number of moles of liquid for each Molecule
                    for (Entry<Pair<LegacySpecies, Boolean>, Float> entry : map.entrySet()) {
                        Pair<LegacySpecies, Boolean> phasedMolecule = entry.getKey();
                        moleculeStates.compute(phasedMolecule.getFirst(), (molecule, couple) -> {
                            if (couple == null) couple = Couple.create(0f, 0f);
                            couple.set(phasedMolecule.getSecond(), entry.getValue());
                            return couple;
                        });
                    };

                    // Add each Molecule to its result Mixture
                    addMoleculesToMixture: for (Entry<LegacySpecies, Couple<Float>> entry : moleculeStates.entrySet()) {
                        Couple<Float> moles = entry.getValue();
                        LegacySpecies molecule = entry.getKey();
                        float totalMoles = moles.getFirst() + moles.getSecond();
                        if (totalMoles <= 0f) continue addMoleculesToMixture;
                        if (debug) {
                            Destroy.LOGGER.info("Adding Molecule '"+molecule.getFullID()+"' to " +(resultMixture == denseMixture ? "dense" : "light")+" Mixture");
                            Destroy.LOGGER.info("   Total moles: "+totalMoles);
                        };
                        resultMixture.addMolecule(molecule, 2f * totalMoles / totalVolume);
                        resultMixture.setState(molecule, moles.getFirst() / totalMoles);
                    };
                };

                // If we've got to this point, the Fluid can be succesfully processed
                getInputTank().drain(amount, FluidAction.EXECUTE);
                getDenseOutputTank().fill(MixtureFluid.of(amount / 2, denseMixture), FluidAction.EXECUTE);
                getLightOutputTank().fill(MixtureFluid.of(amount / 2, lightMixture), FluidAction.EXECUTE);

            } else { // If there is no Mixture to Centrifuge
                return;
            };
        } else { // If there is a Recipe
            if (!canFitFluidInTank(lastRecipe.getDenseOutputFluid(), getDenseOutputTank()) || !canFitFluidInTank(lastRecipe.getLightOutputFluid(), getLightOutputTank()) || hasFluidInTank(lastRecipe.getRequiredFluid(), getLightOutputTank())) return; // Ensure the Recipe can still be Processed
            getInputTank().drain(lastRecipe.getRequiredFluid().getRequiredAmount(), FluidAction.EXECUTE);
            getDenseOutputTank().fill(lastRecipe.getDenseOutputFluid(), FluidAction.EXECUTE);
            getLightOutputTank().fill(lastRecipe.getLightOutputFluid(), FluidAction.EXECUTE);
        };
        advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.USE_CENTRIFUGE);
        notifyUpdate();
    };

    private static Pair<LegacySpecies, Boolean> getNextIon(List<Pair<LegacySpecies, Boolean>> list, Map<Pair<LegacySpecies, Boolean>, Float> map, boolean cation) {
        for (Pair<LegacySpecies, Boolean> pair : list) {
            int charge = pair.getFirst().getCharge();
            if (charge != 0 && charge > 0 == cation && map.containsKey(pair) && map.get(pair) > 0f) return pair;
        };
        return Pair.of(null, null);
    };

    @SuppressWarnings("null")
    public void spawnParticles() {
        FluidStack fluidStack = inputTank.getPrimaryHandler().getFluid();
        if (fluidStack.isEmpty() || !hasLevel()) return;

        RandomSource random = getLevel().getRandom(); // It thinks getLevel() might be null

        ParticleOptions data = FluidFX.getFluidParticle(fluidStack);
        float angle = random.nextFloat() * 360;
        Vec3 offset = new Vec3(0, 0, 0.7f);
        offset = VecHelper.rotate(offset, angle, Axis.Y);
        Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Axis.Y);

        Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
        target = VecHelper.offsetRandomly(target.subtract(offset), random, 1/ 128f);
        getLevel().addParticle(data, center.x, center.y, center.z, target.x, target.y, target.z); // It thinks getLevel() might be null
    };

    @Nonnull
    @Override
    @SuppressWarnings("null")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == Direction.UP) {
                return inputTank.getCapability().cast();
            } else if (side == Direction.DOWN) {
                return lightOutputTank.getCapability().cast();
            } else if (side == denseOutputTankFace || pondering) {
                return denseOutputTank.getCapability().cast();
            } else if (side == null) { // For the PollutingBehaviour, it needs all tanks
                return allFluidCapability.cast();
            };
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        allFluidCapability.invalidate();
    };

    public int getEachTankCapacity() {
        return DestroyAllConfigs.SERVER.blocks.centrifugeCapacity.get();
    };

    private void onFluidStackChanged() {
        notifyUpdate();
    };

    /**
     * Let this Centrifuge know we're in a Ponder.
     * This makes it so the dense Fluid can be pulled from any side.
     */
    public void setPondering() {
        pondering = true;
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        // if (MAX_LUBRICATION_LEVEL != 0) {
        //     DestroyLang.translate("tooltip.centrifuge.lubrication")
        //         .style(ChatFormatting.WHITE)
        //         .space()
        //         .add(DestroyLang.barMeterComponent(lubricationLevel, MAX_LUBRICATION_LEVEL, Math.min(MAX_LUBRICATION_LEVEL, 20)))
        //         .forGoggles(tooltip);
        // };

        DestroyLang.fluidContainerInfoHeader(tooltip);
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.input_tank"), getInputTank());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.dense_output_tank"), getDenseOutputTank());
        DestroyLang.tankInfoTooltip(tooltip, DestroyLang.translate("tooltip.centrifuge.light_output_tank"), getLightOutputTank());
        
        return true;
    };

    public static class CentrifugeDisplaySource extends MixtureContentsDisplaySource {

        private final Function<CentrifugeBlockEntity, SmartFluidTank> tankGetter;
        private final String tankId;

        private CentrifugeDisplaySource(String tankId, Function<CentrifugeBlockEntity, SmartFluidTank> tankGetter) {
            super(false);
            this.tankId = tankId;
            this.tankGetter = tankGetter;
        };

        public static CentrifugeDisplaySource createInput() {
            return new CentrifugeDisplaySource("input", CentrifugeBlockEntity::getInputTank);
        }

        public static CentrifugeDisplaySource createDenseOutput() {
            return new CentrifugeDisplaySource("dense_output", CentrifugeBlockEntity::getDenseOutputTank);
        }

        public static CentrifugeDisplaySource createLightOutput() {
            return new CentrifugeDisplaySource("light_output", CentrifugeBlockEntity::getLightOutputTank);
        }

        @Override
        public FluidStack getFluidStack(DisplayLinkContext context) {
            if (context.getSourceBlockEntity() instanceof CentrifugeBlockEntity centrifuge) {
                return tankGetter.apply(centrifuge).getFluid();
            };
            return FluidStack.EMPTY;
        };

        @Override
        public Component getName() {
            return DestroyLang.translate("display_source.centrifuge."+tankId).component();
        };
    };
};
