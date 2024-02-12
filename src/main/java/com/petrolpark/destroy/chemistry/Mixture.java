package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.error.ChemistryException;
import com.petrolpark.destroy.chemistry.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.recipe.ReactionInBasinRecipe.ReactionInBasinResult;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;


public class Mixture extends ReadOnlyMixture {

    protected static final int TICKS_PER_SECOND = 20;

    /**
     * A Map of all {@link ReactionResult Results} of {@link Reaction Reactions} generated in this Mixture, mapped
     * to their 'concentrations' (moles of the Reaction which have occured per Bucket of this Mixture, since the last
     * instance of that Reaction Result was dealt with).
     */
    protected Map<ReactionResult, Float> reactionResults;
    /**
     * {@link Molecule Molecules} which do not have a name space or ID.
     */
    protected List<Molecule> novelMolecules;

    /**
     * All {@link Reaction Reactions} with specific Reactants and specified {@link GenericReaction Generic Reactions}
     * which are possible given the {@link Molecule Molecules} in this Mixture.
     */
    protected List<Reaction> possibleReactions;

    /**`
     * Every {@link Molecule} in this Mixture that has a {@link Group functional Group}, indexed by the {@link Group#getType Type} of that Group.
     * Molecules are stored as {@link com.petrolpark.destroy.chemistry.genericReaction.GenericReactant Generic Reactants}.
     * Molecules which have multiple of the same Group are indexed for each occurence of the Group.
     */
    protected Map<GroupType<?>, List<GenericReactant<?>>> groupIDsAndMolecules;

    /**
     * Whether this Mixture has reached equilibrium. This means either:
     * <ul>
     * <li>No more {@link Reaction Reactions} will occur.</li>
     * <li>Any further Reactions that occur will balance each other out and there will be no change in concentration of any {@link Molecule}.</li>
     * </ul>
     */
    protected boolean equilibrium;

    /**
     * The Molecule which has the boiling point which is closest to the current temperature of the Mixture, but higher.
     * Either value may be {@code null}.
     */
    Pair<Float, Molecule> nextHigherBoilingPoint;
    
    /**
     * The Molecule which has the boiling point which is closest to the current temperature of the Mixture, but lower.
     * Either value may be {@code null}.
     */
    Pair<Float, Molecule> nextLowerBoilingPoint;

    /**
     * Some Molecules which get removed get swiftly added back in the next tick. To prevent refreshing the Reactions every time, we keep hold of them for a while until we know they're definitely gone.
     * This maps Molecules which have 0 concentration to the ticks they have left to live.
     */
    Map<Molecule, Integer> moleculesToRemove;

    public Mixture() {
        super();

        reactionResults = new HashMap<>();

        novelMolecules = new ArrayList<>();

        possibleReactions = new ArrayList<>();
        groupIDsAndMolecules = new HashMap<>();

        nextHigherBoilingPoint = Pair.of(Float.MAX_VALUE, null);
        nextLowerBoilingPoint = Pair.of(0f, null);
        moleculesToRemove = new HashMap<>();

        equilibrium = false;
    };

    /**
     * Get a Mixture containing only the given Molecule, unless it is charged, in which case get a Mixture
     * containing the sodium salt or chloride of the ion.
     * @param molecule
     */
    public static Mixture pure(Molecule molecule) {
        Mixture mixture = new Mixture();
        if (molecule.getCharge() == 0) {
            mixture.addMolecule(molecule, molecule.getPureConcentration());
            return mixture;
        };
        Molecule otherIon = molecule.getCharge() < 0 ? DestroyMolecules.SODIUM_ION : DestroyMolecules.CHLORIDE;
        int chargeMagnitude = Math.abs(molecule.getCharge());
        mixture.addMolecule(molecule, 1f);
        mixture.addMolecule(otherIon, chargeMagnitude);
        mixture.recalculateVolume(1000);
        return mixture;
    };

    public static Mixture readNBT(CompoundTag compound) {
        Mixture mixture = new Mixture();
        
        if (compound == null) {
            Destroy.LOGGER.warn("Null Mixture loaded");
            return mixture;
        };

        mixture.translationKey = compound.getString("TranslationKey"); // Set to "" if the key is not present

        if (compound.contains("Temperature")) mixture.temperature = compound.getFloat("Temperature");

        ListTag contents = compound.getList("Contents", Tag.TAG_COMPOUND);
        contents.forEach(tag -> {
            CompoundTag moleculeTag = (CompoundTag) tag;
            Molecule molecule = Molecule.getMolecule(moleculeTag.getString("Molecule"));
            mixture.internalAddMolecule(molecule, moleculeTag.getFloat("Concentration"), false);
            if (moleculeTag.contains("Gaseous",Tag.TAG_FLOAT)) {
                mixture.states.put(molecule, moleculeTag.getFloat("Gaseous"));
            } else { // If we're not told the state, guess it
                mixture.states.put(molecule, molecule.getBoilingPoint() < mixture.temperature ? 1f : 0f);
            };
        });

        mixture.equilibrium = compound.getBoolean("AtEquilibrium");

        if (compound.contains("Results", Tag.TAG_LIST)) {
            ListTag results = compound.getList("Results", Tag.TAG_COMPOUND);
            results.forEach(tag -> {
                CompoundTag resultTag = (CompoundTag) tag;
                ReactionResult result = Reaction.get(resultTag.getString("Result")).getResult();
                if (result == null) return;
                mixture.reactionResults.put(result, resultTag.getFloat("MolesPerBucket"));
            });
        };

        mixture.updateName();
        mixture.updateColor();
        mixture.refreshPossibleReactions();
        mixture.updateNextBoilingPoints();

        return mixture;
    };

    @Override
    public CompoundTag writeNBT() {
        CompoundTag tag = super.writeNBT();
        tag.putBoolean("AtEquilibrium", equilibrium);

        if (!reactionResults.isEmpty()) {
            tag.put("Results", NBTHelper.writeCompoundList(reactionResults.entrySet(), entry -> {
                CompoundTag resultTag = new CompoundTag();
                resultTag.putString("Result", entry.getKey().getReaction().getFullId());
                resultTag.putFloat("MolesPerBucket", entry.getValue());
                return resultTag;
            }));
        };
        return tag;
    };

    /**
     * Set the temperature (in kelvins) of this Mixture. This is mutative.
     * @param temperature in Kelvins
     * @return This Mixture
     */
    public Mixture setTemperature(float temperature) {
        this.temperature = temperature;
        // Ensure everything has the right temperature
        for (Molecule molecule : contents.keySet()) {
            if (molecule.getBoilingPoint() < temperature) {
                states.put(molecule, 1f);
            } else {
                states.put(molecule, 0f);
            };
        };
        return this; 
    };

    /**
     * Adds a {@link Molecule} to this Mixture.
     * If the Molecule is already in the Mixture, its concentration is increased by the given amount.
     * @param molecule The Molecule to add
     * @param concentration The initial concentration of this Molecule, or the amount to add
     */
    @Override
    public Mixture addMolecule(Molecule molecule, float concentration) {

        if (getConcentrationOf(molecule) > 0f) { // If we already have this Molecule
            changeConcentrationOf(molecule, concentration, true);
            updateName();
            updateColor();
            return this;
        };

        // If we're not adding a pre-existing Molecule
        internalAddMolecule(molecule, concentration, true);
        equilibrium = false;
        return this;
    };

    public List<Molecule> getContents(boolean excludeNovel) {
        return contents.keySet().stream().filter(molecule -> getConcentrationOf(molecule) > 0f && (!molecule.isNovel() || !excludeNovel)).toList();
    };

    /**
     * Creates a new Mixture by mixing together existing ones. This does not give the volume of the new Mixture.
     * @param mixtures A Map of all Mixtures to their volumes (in Buckets)
     * @return A new Mixture instance
     */
    public static Mixture mix(Map<Mixture, Double> mixtures) {
        if (mixtures.size() == 0) return new Mixture();
        if (mixtures.size() == 1) return mixtures.keySet().iterator().next();
        Mixture resultMixture = new Mixture();
        Map<Molecule, Double> moleculesAndMoles = new HashMap<>(); // A Map of all Molecules to their quantity in moles (not their concentration)
        Map<ReactionResult, Double> reactionResultsAndMoles = new HashMap<>(); // A Map of all Reaction Results to their quantity in moles
        double totalAmount = 0d;
        float totalEnergy = 0f;

        for (Entry<Mixture, Double> mixtureAndAmount : mixtures.entrySet()) {
            Mixture mixture = mixtureAndAmount.getKey();
            double amount = mixtureAndAmount.getValue();
            totalAmount += amount;

            for (Entry<Molecule, Float> entry : mixture.contents.entrySet()) {
                Molecule molecule = entry.getKey();
                float concentration = entry.getValue();
                moleculesAndMoles.merge(molecule, concentration * amount, (m1, m2) -> m1 + m2); // Add the Molecule to the map if it's a new one, or increase the existing molar quantity otherwise
                totalEnergy += molecule.getMolarHeatCapacity() * concentration * mixture.temperature * amount; // Add all the energy that would be required to raise this Molecule from 0K to its current temperature
                totalEnergy += molecule.getLatentHeat() * concentration * mixture.states.get(molecule) * amount; // Add all the energy that would be required to vaporise this Molecule, if necessary
            };

            for (Entry<ReactionResult, Float> entry : mixture.reactionResults.entrySet()) {
                reactionResultsAndMoles.merge(entry.getKey(), entry.getValue() * amount, (r1, r2) -> r1 + r2); // Same for Reaction Results
            };
        };

        for (Entry<Molecule, Double> moleculeAndMoles : moleculesAndMoles.entrySet()) {
            Molecule molecule = moleculeAndMoles.getKey();
            resultMixture.internalAddMolecule(molecule, (float)(moleculeAndMoles.getValue() / totalAmount), false); // Add all these Molecules to the new Mixture
            resultMixture.states.put(molecule, 0f); // Set it to entirely liquid as we will soon be reheating the Mixture from 0K
        };

        for (Entry<ReactionResult, Double> reactionResultAndMoles : reactionResultsAndMoles.entrySet()) {
            resultMixture.incrementReactionResults(reactionResultAndMoles.getKey().getReaction(), (float)(reactionResultAndMoles.getValue() / totalAmount)); // Add all Reaction Results to the new Mixture
        };

        resultMixture.temperature = 0f; // Initially set the temperature of the new Mixture to 0K
        resultMixture.updateNextBoilingPoints();
        resultMixture.heat(totalEnergy / (float)totalAmount); // Now heat it up with the total internal energy of all component Mixtures

        resultMixture.refreshPossibleReactions();
        resultMixture.updateName();
        resultMixture.updateColor();
        resultMixture.updateNextBoilingPoints();

        return resultMixture;
    };

    @Override
    public float getConcentrationOf(Molecule molecule) {
        return super.getConcentrationOf(molecule);
    };

    /**
     * Whether this Mixture will {@link Mixture#equilibrium react any further}.
     */
    public boolean isAtEquilibrium() {
        return equilibrium;
    };

    /**
     * Let this Mixture know it should no longer be at {@link Mixture#equilibrium equilibrium}.
     */
    public void disturbEquilibrium() {
        equilibrium = false;
    };

    /**
     * Reacts the contents of this Mixture for one tick, if it is not already at {@link Mixture#equilibrium equilibrium}.
     * @param context
     * @param cycles Number of times each tick the reactions should be enacted
     */
    public void reactForTick(ReactionContext context, int cycles) {

        boolean shouldUpdateDisplay = true;

        for (int cycle = 0; cycle < cycles; cycle++) {

            if (equilibrium) { // If we have already reached equilibrium, nothing more is going to happen, so don't bother reacting
                shouldUpdateDisplay = false;
                break; 
            };

            equilibrium = true; // Start by assuming we have reached equilibrium
            boolean shouldRefreshPossibleReactions = false; // Rather than refreshing the possible Reactions every time a new Molecule is added or removed, start by assuming we won't need to, and flag for refreshing if we ever do

            Map<Molecule, Float> oldContents = new HashMap<>(contents); // Copy all the old concentrations of everything

            Map<Reaction, Float> reactionRates = new HashMap<>(); // Rates of all Reactions
            List<Reaction> orderedReactions = new ArrayList<>(); // A list of Reactions in the order of their current rate, fastest first

            orderEachReaction: for (Reaction possibleReaction : possibleReactions) {
                if (possibleReaction.consumesItem()) continue orderEachReaction; // Don't include Reactions which CONSUME Items at this stage

                for (IItemReactant itemReactant : possibleReaction.getItemReactants()) { // Check all Reactions have the necessary Item catalysts
                    boolean validStackFound = false; // Start by assuming we won't have the required Item Stack...
                    checkAllItems: for (ItemStack stack : context.availableItemStacks) {
                        if (itemReactant.isItemValid(stack)) {
                            validStackFound = true; // ...If we do, correct this assumption
                            break checkAllItems;
                        };
                    };
                    if (!validStackFound) continue orderEachReaction; // If we don't have the requesite Item Stacks, don't do this Reaction
                };

                reactionRates.put(possibleReaction, calculateReactionRate(possibleReaction, context) / cycles); // Calculate the Reaction data for this tick
                orderedReactions.add(possibleReaction); // Add the Reaction to the rate-ordered list, which is currently not sorted
            };

            Collections.sort(orderedReactions, (r1, r2) -> reactionRates.get(r1).compareTo(reactionRates.get(r2))); // Sort the Reactions by rate

            doEachReaction: for (Reaction reaction : orderedReactions) { // Go through each Reaction, fastest first

                Float molesOfReaction = reactionRates.get(reaction); // We are reacting over one tick, so moles of Reaction that take place in this time = rate of Reaction in M per tick

                for (Molecule reactant : reaction.getReactants()) {
                    int reactantMolarRatio = reaction.getReactantMolarRatio(reactant);
                    float reactantConcentration = getConcentrationOf(reactant);
                    if (reactantConcentration < reactantMolarRatio * molesOfReaction) { // Determine the limiting reagent, if there is one
                        molesOfReaction = reactantConcentration / (float) reactantMolarRatio; // If there is a new limiting reagent, alter the moles of reaction which will take place
                    };
                };

                if (molesOfReaction <= 0f) continue doEachReaction; // Don't bother going any further if this Reaction won't happen

                shouldRefreshPossibleReactions |= doReaction(reaction, molesOfReaction); // Increment the amount of this Reaction which has occured, add all products and remove all reactants
            };

            // Check now if we have actually reached equilibrium or if that was a false assumption at the start
            for (Molecule molecule : oldContents.keySet()) {
                if (!areVeryClose(oldContents.get(molecule), getConcentrationOf(molecule))) { // If there's something that has changed concentration noticeably in this tick...
                    equilibrium = false; // ...we cannot have reached equilibrium
                };
            };

            if (shouldRefreshPossibleReactions) { // If we added a new Molecule at any point
                refreshPossibleReactions();
            };

        };

        // Purge removed Molecules
        boolean shouldUpdateReactions = false;
        Iterator<Entry<Molecule, Integer>> iterator = moleculesToRemove.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Molecule, Integer> entry = iterator.next();
            entry.setValue(entry.getValue() - 1);
            if (entry.getValue() <= 0) {
                removeMolecule(entry.getKey()); // Completely remove the Molecule from the Mixture if its TTL has expired
                iterator.remove();  
                shouldUpdateReactions = true; // Now we know the Molecule isn't coming back, we can update the reactions to reflect its removal
            };
        };
        if (shouldUpdateReactions) refreshPossibleReactions();
        
        if (shouldUpdateDisplay) {
            updateName();
            updateColor();
        };
    };

    /**
     * Add or take heat from this Mixture. This will boil/condense Molecules and change the temperature.
     * @param energy In joules per bucket
     */
    public void heat(float energyDensity) {
        float volumetricHeatCapacity = getVolumetricHeatCapacity();
        if (volumetricHeatCapacity == 0f) return;

        float temperatureChange = energyDensity / volumetricHeatCapacity; // The theoretical temperature change if no boiling or condensation occurs

        if (temperatureChange == 0f) {
            return;
        } else if (temperatureChange > 0f) { // If the temperature would be increasing
            if (nextHigherBoilingPoint.getSecond() != null && temperature + temperatureChange >= nextHigherBoilingPoint.getFirst()) { // If a Molecule needs to boil before the temperature can change

                temperatureChange = nextHigherBoilingPoint.getFirst() - temperature; // Only increase the temperature by enough to get to the next BP
                temperature += temperatureChange; // Raise the Mixture to the boiling point
                energyDensity -= temperatureChange * getVolumetricHeatCapacity(); // Energy leftover once the Mixture has been raised to the boiling point

                Molecule molecule = nextHigherBoilingPoint.getSecond();
                float liquidConcentration = getConcentrationOf(molecule) * (1f - states.get(molecule)); // The moles per bucket of liquid Molecules
                float energyRequiredToFullyBoil = liquidConcentration * molecule.getLatentHeat(); // The energy density required to boil all remaining liquid

                if (energyDensity > energyRequiredToFullyBoil) { // If there is leftover energy once the Molecule has been boiled
                    states.put(molecule, 1f); // Convert the Molecule fully to gas
                    temperature += 0.01f; // Increase the temperature slightly so the new next higher Molecule isn't the one we just finished boiling
                    updateNextBoilingPoints();
                    heat(energyDensity - energyRequiredToFullyBoil); // Continue heating
                } else { // If there is no leftover energy and the Molecule is still boiling
                    float boiled = energyDensity / (molecule.getLatentHeat() * getConcentrationOf(molecule)); // The proportion of all of the Molecule which is additionally boiled
                    states.merge(molecule, boiled, (f1, f2) -> f1 + f2);
                };

                equilibrium = false; // Equilibrium is broken when a Molecule evaporates
            } else {
                temperature += temperatureChange;
            };
        } else { // If the temperature would be decreasing
            if (nextLowerBoilingPoint.getSecond() != null && temperature + temperatureChange < nextLowerBoilingPoint.getFirst()) { // If a Molecule needs to condense before the temperature can change

                temperatureChange = nextLowerBoilingPoint.getFirst() - temperature; // Only decrease the temperature by enough to get to the next condensation point
                temperature += temperatureChange; // Decrease the Mixture to the boiling point
                energyDensity -= temperatureChange * getVolumetricHeatCapacity(); // Additional energy once the Mixture has been lowered to the condensation point

                Molecule molecule = nextLowerBoilingPoint.getSecond();
                float gasConcentration = getConcentrationOf(molecule) * states.get(molecule);
                float energyReleasedWhenFullyCondensed = gasConcentration * molecule.getLatentHeat(); // The energy density which could be released when all remaining gas is condensed

                if (energyDensity < -energyReleasedWhenFullyCondensed) { // If there is more energy that needs to be released than the condensation can supply
                    states.put(molecule, 0f); // Convert the Molecule fully to liquid
                    temperature -= 0.01f; // Decrease the temperature slightly so the new next lower Molecule isn't the one we just finished condensing
                    updateNextBoilingPoints();
                    heat(energyDensity + energyReleasedWhenFullyCondensed); // Continue cooling
                } else {
                    float condensed = -energyDensity / (molecule.getLatentHeat() * getConcentrationOf(molecule));
                    states.merge(molecule, 1f - condensed, (f1, f2) -> f1 + f2 - 1f);
                };

                equilibrium = false; // Equilibrium is broken when a Molecule condenses

            } else {
                temperature += temperatureChange;
            };
        };

        temperature = Math.max(temperature, 0.0001f);
    };

    /**
     * Enact all {@link Reactions} that {@link Reaction#getItemReactants involve Item Stacks}. This does not just
     * include dissolutions, but Item-catalyzed Reactions too.
     * @param availableStacks The Item Stacks available to this Mixture. This Stacks in this List will be modified
     * @param volume The amount of this Mixture there is, in buckets
     */
    public void dissolveItems(ReactionContext context, double volume) {
        List<ItemStack> availableStacks = List.copyOf(context.availableItemStacks);
        if (availableStacks.isEmpty()) return;
        boolean shouldRefreshReactions = false;

        List<Reaction> orderedReactions = new ArrayList<>();

        for (Reaction possibleReaction : possibleReactions) {
            if (!possibleReaction.consumesItem()) continue; // Ignore Reactions which don't consume Items
            orderedReactions.add(possibleReaction); // Add the Reaction to the list of possible Reactions, which is currently not ordered
        };

        if (orderedReactions.isEmpty()) return; // Don't go any further if there aren't any items to dissolve

        Collections.sort(possibleReactions, (r1, r2) -> ((Float)calculateReactionRate(r1, context)).compareTo(calculateReactionRate(r2, context))); // Order the list of Item-consuming Reactions by rate, in case multiple of them want the same Item

        tryEachReaction: for (Reaction reaction : orderedReactions) {

            /*
             * Copies of available Stacks mapped to their real counterparts.
             * When simulating, check the copies, as multiple different Item Reactants for one Reaction might want the same Item Stack
             * and we want to make sure a Stack doesn't get used twice. If a Stack's copy is successfully used, its original is added
             * to the second Map so if the simulation is successful, the Item Reactant can easily find the Item Stack it is to consume.
             * This is still not perfect as if Reactant A wants Stacks A or B, and Reactant B only wants Stack A, and Reactant A gets
             * Stack A before Reactant B can, it will fail. Therefore you should be careful about what Item Stacks your Reaction wants.
             */
            Map<ItemStack, ItemStack> copiesAndStacks = new HashMap<>(availableStacks.size());
            /*
             * Item Reactants mapped to the actual Stacks they will consume.
             */
            Map<IItemReactant, ItemStack> reactantsAndStacks = new HashMap<>(reaction.getItemReactants().size());

            for (ItemStack stack : availableStacks) { // Fill the map
                if (!stack.isEmpty()) copiesAndStacks.put(stack.copy(), stack); // Check if the Item Stack has since been emptied by another Reaction
            };

            while (true) { // Go on dissolving Items until we run out

                for (Molecule reactant : reaction.getReactants()) { // Check we have enough non-Item reactants
                    if (getConcentrationOf(reactant) < (float)reaction.getReactantMolarRatio(reactant) * reaction.getMolesPerItem() / (float)volume) continue tryEachReaction; // If there is not enough Reactant to use up the Item Stacks, give up now
                };
                
                for (IItemReactant itemReactant : reaction.getItemReactants()) {
                    boolean validItemFound = false; // Start by assuming we haven't yet come across the right Stack
                    for (ItemStack stackCopy : copiesAndStacks.keySet()) {
                        if (itemReactant.isItemValid(stackCopy)) {
                            validItemFound = true; // We have now found the right Stack
                            if (!itemReactant.isCatalyst()) { // If this Item gets used up
                                itemReactant.consume(stackCopy); // Consume the Stack copy so we know for future simulations that it can't be used
                                reactantsAndStacks.put(itemReactant, copiesAndStacks.get(stackCopy)); // Store the actual Item Stack to be consumed later
                            };
                        };
                    };
                    if (!validItemFound) continue tryEachReaction; // If the simulation was a failure, move onto the next Reaction.
                };

                // If we've gotten to this point, all Items can be successfully consumed

                for (IItemReactant itemReactant : reaction.getItemReactants()) {
                    if (!itemReactant.isCatalyst()) itemReactant.consume(reactantsAndStacks.get(itemReactant)); // Consume each actual Item Stack
                };

                equilibrium = false;
                shouldRefreshReactions |= doReaction(reaction, reaction.getMolesPerItem() / (float)volume); // Add all Molecular products and remove Molecular reactants
            }
        };

        updateName();
        updateColor();
        
        if (shouldRefreshReactions) refreshPossibleReactions();
    };

    /**
     * Corrects the volume of this Mixture, to prevent things like Mixtures containing
     * 1M water and nothing else (which is physically impossible). This is mutative.
     * @param initialVolume The initial volume (in mB)
     * @return The new volume (in mB) of this Mixture
     * @deprecated This doesn't account for the space occupied by gases
     */
    @Deprecated
    public int recalculateVolume(int initialVolume) {
        if (contents.isEmpty()) return 0;
        double initialVolumeInBuckets = (double)initialVolume / 1000d;
        double newVolumeInBuckets = 0d;

        // Molecules
        Map<Molecule, Double> molesOfMolecules = new HashMap<>();
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            Molecule molecule = entry.getKey();
            double molesOfMolecule = entry.getValue() * initialVolumeInBuckets;
            molesOfMolecules.put(molecule, molesOfMolecule);
            newVolumeInBuckets += molesOfMolecule / molecule.getPureConcentration();
        };
        for (Entry<Molecule, Double> entry : molesOfMolecules.entrySet()) {
            contents.replace(entry.getKey(), (float)(entry.getValue() / newVolumeInBuckets));
        };

        // Results
        Map<ReactionResult, Float> resultsCopy = new HashMap<>(reactionResults);
        for (Entry<ReactionResult, Float> entry : resultsCopy.entrySet()) {
            reactionResults.replace(entry.getKey(), (float)(entry.getValue() * initialVolumeInBuckets / newVolumeInBuckets));
        };

        return (int)((newVolumeInBuckets * 1000) + 0.5);
    };

    /**
     * Adjust the concentrations of this Mixture so the number of moles is conserved if the volume is changed. The change is isothermic.
     * @param volumeIncreaseFactor The multiplicative factor which has been applied to the volume.
     */
    public void scale(float volumeIncreaseFactor) {
        contents.replaceAll((molecule, concentration) -> concentration / volumeIncreaseFactor);
        reactionResults.replaceAll((reactionResult, molesPerBucket) -> molesPerBucket / volumeIncreaseFactor);
    };

    public static record Phases(Mixture gasMixture, Double gasVolume, Mixture liquidMixture, Double liquidVolume) {};

    /**
     * Get two new Mixtures from one - one containing all gas, one containing all liquid.
     * This doesn't mutate this Mixture.
     * @param initialVolume The initial volume of this Mixture from which to scale, ideally in buckets.
     * @return A {@link Phases} record containing:<ul>
     * <li>{@code gasMixture} A new Mixture containing all gases</li>
     * <li>{@code gasVolume} This is always {@code 1d}, allowing it to be rescaled later.</li>
     * <li>{@code liquidMixture} A new Mixture containing all liquids.</li>
     * <li>{@code liquidVolume} A volume of liquid in the same units as {@code initialVolume}.</li></ul>
     */
    public Phases separatePhases(double initialVolume) {
        Map<Molecule, Double> liquidMoles = new HashMap<>();
        Map<Molecule, Double> gasMoles = new HashMap<>();

        double newLiquidVolume = 0d;
        double newGasVolume = 1d;

        Mixture liquidMixture = new Mixture();
        Mixture gasMixture = new Mixture();

        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            Molecule molecule = entry.getKey();
            float concentration = entry.getValue();
            float proportionGaseous = states.get(molecule);

            // Liquid
            double molesOfLiquidMolecule = concentration * (1f - proportionGaseous) * initialVolume;
            liquidMoles.put(molecule, molesOfLiquidMolecule);
            newLiquidVolume += molesOfLiquidMolecule / molecule.getPureConcentration();

            // Gas
            gasMoles.put(molecule, concentration * proportionGaseous * initialVolume);
        };

        // Put Molecules in new Mixtures
        for (Entry<Molecule, Double> entry : liquidMoles.entrySet()) {
            double moles = entry.getValue();
            if (moles == 0d) continue;
            liquidMixture.internalAddMolecule(entry.getKey(), (float)(moles / newLiquidVolume), false);
            liquidMixture.states.put(entry.getKey(), 0f);
        };
        for (Entry<Molecule, Double> entry : gasMoles.entrySet()) {
            double moles = entry.getValue();
            if (moles == 0d) continue;
            gasMixture.internalAddMolecule(entry.getKey(), (float)(moles / newGasVolume), false);
            gasMixture.states.put(entry.getKey(), 1f);
        };

        // Add Reaction Results to new Mixtures
        for (Entry<ReactionResult, Float> entry : reactionResults.entrySet()) {
            double resultMoles = entry.getValue() * initialVolume;
            liquidMixture.reactionResults.put(entry.getKey(), (float)(resultMoles / newLiquidVolume));
            gasMixture.reactionResults.put(entry.getKey(), (float)(resultMoles / newGasVolume));
        };

        liquidMixture.temperature = temperature;
        gasMixture.temperature = temperature;
        liquidMixture.refreshPossibleReactions();
        gasMixture.refreshPossibleReactions();
        liquidMixture.equilibrium = equilibrium;
        gasMixture.equilibrium = equilibrium;

        return new Phases(gasMixture, newGasVolume, liquidMixture, newLiquidVolume);
    };

    /**
     * Increase the number of moles of Reaction which have occured, add all products, and remove all reactants.
     * @param reaction
     * @param molesPerBucket Moles (per Bucket) of Reaction
     * @return Whether the possible Reactions for this Mixture should be updated
     */
    protected boolean doReaction(Reaction reaction, float molesPerBucket) {

        boolean shouldRefreshPossibleReactions = false;

        for (Molecule reactant : reaction.getReactants()) {
            changeConcentrationOf(reactant, - (molesPerBucket * reaction.getReactantMolarRatio(reactant)), false); // Use up the right amount of all the reagents
        };

        addEachProduct: for (Molecule product : reaction.getProducts()) {
            if (product.isNovel() && getConcentrationOf(product) == 0f) { // If we have a novel Molecule that we don't think currently exists in the Mixture...
                if (internalAddMolecule(product, molesPerBucket * reaction.getProductMolarRatio(product), false)) { // ...add it with this method, as this automatically checks for pre-existing novel Molecules, and if it was actually a brand new Molecule...
                    shouldRefreshPossibleReactions = true; // ...flag this
                }; 
                continue addEachProduct;
            };

            if (getConcentrationOf(product) == 0f) { // If we are adding a new product, the possible Reactions will change
                shouldRefreshPossibleReactions = true;
            };
            changeConcentrationOf(product, molesPerBucket * reaction.getProductMolarRatio(product), false); // Increase the concentration of the product
        };

        heat(-reaction.getEnthalpyChange() * 1000 * molesPerBucket);
        incrementReactionResults(reaction, molesPerBucket);

        return shouldRefreshPossibleReactions;
    };

    /**
     * Increase the number of moles of this Reaction which have occured in this Mixture.
     * @param reaction
     * @param molesPerBucket Moles (per Bucket) of this Reaction
     */
    protected void incrementReactionResults(Reaction reaction, float molesPerBucket) {
        if (!reaction.hasResult()) return;
        ReactionResult result = reaction.getResult();
        reactionResults.merge(result, molesPerBucket, (f1, f2) -> f1 + f2);
    };

    /**
     * {@link Mixture#reactForTick React} this Mixture until it reaches {@link Mixture#equilibrium equilibrium}. This is mutative.
     * @return A {@link com.petrolpark.destroy.recipe.ReactionInBasinRecipe.ReactionInBasinResult ReactionInBasinResult} containing
     * the number of ticks it took to reach equilibrium, the {@link ReactionResult Reaction Results} and the new volume of Mixture.
     * @param volume (in mB) of this Reaction
     * @param availableStacks Item Stacks available for reacting. This List and its contents will be modified.
     * @param heatingPower The power being supplied to this Basin by the {@link com.petrolpark.destroy.util.vat.IVatHeaterBlock heater} below it.
     * @param outsideTemperature The {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution#getLocalTemperature temperature} outside the Basin.
     */
    public ReactionInBasinResult reactInBasin(int volume, List<ItemStack> availableStacks, float heatingPower, float outsideTemperature) {
        float volumeInBuckets = (float)volume / 1000f;
        int ticks = 0;

        ReactionContext context = new ReactionContext(availableStacks, 0f); 
        dissolveItems(context, volumeInBuckets); // Dissolve all Items
        while (!equilibrium && ticks < 600) { // React the Mixture
            float energyChange = heatingPower / TICKS_PER_SECOND;
            energyChange += (outsideTemperature - temperature) * 100f / TICKS_PER_SECOND; // Fourier's Law (sort of), the Basin has a fixed conductance of 100 andthe divide by 20 is for 20 ticks per second
            if (Math.abs(energyChange) > 0.0001f) {
                heat(1000 * energyChange / volume); // 1000 converts getFluidAmount() in mB to Buckets
            };
            reactForTick(context, 1);
            ticks++;
        };

        if (ticks == 0) return new ReactionInBasinResult(0, Map.of(), volume); // If no reactions occured (because we were already at equilibrium), cancel early

        int amount = recalculateVolume(volume);

        return new ReactionInBasinResult(ticks, getCompletedResults(volumeInBuckets), amount);
    };

    /**
     * If any {@link Mixture#reactionResults results} have had enough moles to have occured, remove them from the Mixture and return them here.
     * This is mutative.
     * @param volumeInBuckets The amount of Mixture
     * @return A Set of Reaction Results mapped to the number of times that Reaction result occurs
     */
    public Map<ReactionResult, Integer> getCompletedResults(double volumeInBuckets) {
        Map<ReactionResult, Integer> results = new HashMap<>();
        if (reactionResults.isEmpty()) return results;
        for (ReactionResult result : reactionResults.keySet()) {

            if (result.isOneOff()) {
                results.put(result, 1);
                continue;
            };

            Float molesPerBucketOfReaction = reactionResults.get(result);
            int numberOfResult = (int) (volumeInBuckets * molesPerBucketOfReaction / result.getRequiredMoles());
            if (numberOfResult == 0) continue;

            // Decrease the amount of Reaction that has happened
            reactionResults.replace(result, molesPerBucketOfReaction - numberOfResult * result.getRequiredMoles() / (float)volumeInBuckets);

            results.put(result, numberOfResult);
        };
        // reactionResults.keySet().removeIf(result -> { // Remove any one-off Results and Results which have run out
        //     return result.isOneOff() || areVeryClose(reactionResults.get(result), 0f);
        // }); 
        return results;
    };

    /**
     * Get the heat capacity (in joules per bucket-kelvin) of this Mixture.
     */
    public float getVolumetricHeatCapacity() {
        float totalHeatCapacity = 0f;
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            totalHeatCapacity += entry.getKey().getMolarHeatCapacity() * entry.getValue();
        };
        return totalHeatCapacity;
    };

    /**
     * Set the Molecules which will be next to be condense or boil if the temperature of this Mixture changes.
     */
    protected void updateNextBoilingPoints() {
        nextHigherBoilingPoint = Pair.of(Float.MAX_VALUE, null);
        nextLowerBoilingPoint = Pair.of(0f, null);
        for (Molecule molecule : contents.keySet()) {
            float bp = molecule.getBoilingPoint();
            if (bp <= temperature) {
                if (bp > nextLowerBoilingPoint.getFirst()) nextLowerBoilingPoint = Pair.of(bp, molecule);
            }
            if (bp >= temperature) { // If the boiling point is higher than the current temperture.
                if (bp < nextHigherBoilingPoint.getFirst()) nextHigherBoilingPoint = Pair.of(bp, molecule);
            };
        };
    };

    /**
     * Adds a {@link Molecule} to this Mixture.
     * If a novel Molecule is being added, it is checked against pre-existing novel Molecules
     * and if a matching one already exists, the concentration of it is increased.
     * @param molecule The Molecule to add
     * @param concentration The starting concentration for the Molecule
     * @param shouldRefreshReactions Whether to {@link Mixture#refreshPossibleReactions refresh possible Reactions} -
     * this should only be set to false when multiple Molecules are being added/removed at once (such as when {@link Mixture#reactForTick reacting})
     * and it makes sense to only refresh the Reactions once
     * @return {@code true} if the possible reactions need to be refreshed (because a new Molecule was added); {@code false} otherwise
     * @see Mixture#addMolecule The wrapper for this method
     * @see Mixture#changeConcentrationOf Modifying the concentration of pre-existing Molecule
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean internalAddMolecule(Molecule molecule, float concentration, boolean shouldRefreshReactions) {

        boolean newMoleculeAdded = true; // Start by assuming we're adding a brand new Molecule to this solution

        if (contents.containsKey(molecule)) { // Just in case this Molecule is already in the Mixture, increase its concentration
            changeConcentrationOf(molecule, concentration, shouldRefreshReactions);
            return false;
        };

        if (!molecule.isNovel()) super.addMolecule(molecule, concentration);

        List<Group<?>> functionalGroups = molecule.getFunctionalGroups();
        if (functionalGroups.size() != 0) {
            for (Group group : functionalGroups) { // Unparameterised raw type
                addGroupToMixture(molecule, group); // Unchecked conversion
            };
        };

        if (molecule.isNovel()) { // If this is a novel Molecule, it might already match to one of our existing novel Molecules
            boolean found = false; // Start by assuming it's not already in the Mixture
            for (Molecule novelMolecule : novelMolecules) { // Check every novel Molecule
                if (novelMolecule.getFullID().equals(molecule.getFullID())) {
                    found = true;
                    newMoleculeAdded = false; // We haven't actually added a brand new Molecule so flag this
                    changeConcentrationOf(novelMolecule, concentration, true);
                    equilibrium = false;
                };
            };
            if (!found) {
                super.addMolecule(molecule, concentration);
                novelMolecules.add(molecule); // If it was actually a brand new Molecule, add it to the novel list
            }; 
        };

        if (shouldRefreshReactions && newMoleculeAdded) {
            refreshPossibleReactions();
        };

        equilibrium = false;

        return newMoleculeAdded; // Return whether or not we actually added a brand new Molecule
    };

    private <G extends Group<G>> void addGroupToMixture(Molecule molecule, G group) {
        GroupType<G> groupType = group.getType();
        if (!groupIDsAndMolecules.containsKey(groupType)) {
            groupIDsAndMolecules.put(groupType, new ArrayList<>());
        };
        groupIDsAndMolecules.get(groupType).add(new GenericReactant<>(molecule, group));
    };

    /**
     * Removes the given {@link Molecule} from this Mixture, if that Molecule is already in it.
     * This does not refresh possible {@link Reaction Reactions}.
     * @param molecule
     * @return This Mixture
     */
    private Mixture removeMolecule(Molecule molecule) {

        List<Group<?>> functionalGroups = molecule.getFunctionalGroups();
        if (functionalGroups.size() != 0) {
            for (Group<?> group : functionalGroups) {
                groupIDsAndMolecules.get(group.getType()).removeIf((reactant) -> {
                    return reactant.getMolecule() == molecule;
                });
            };
        };
        if (molecule.isNovel()) novelMolecules.remove(molecule);

        contents.remove(molecule);
        equilibrium = false; // As we have removed a Molecule the position of equilibrium is likely to change
        updateNextBoilingPoints();

        return this;
    };

    /**
     * Alters the concentration of a {@link Molecule} in a Mixture.
     * This does not update the {@link ReadOnlyMixture#getName name} or equilibrium status of the Mixture.
     * @param molecule If not present in the Mixture, will be added to the Mixture
     * @param change The <em>change</em> in concentration, not the new value (can be positive or negative)
     * @param shouldRefreshReactions Whether to alter the possible {@link Reaction Reactions} in the case that a new Molecule is added to the Mixture (should almost always be {@code true})
     */
    private Mixture changeConcentrationOf(Molecule molecule, float change, boolean shouldRefreshReactions) {
        Float currentConcentration = getConcentrationOf(molecule);

        if (!contents.containsKey(molecule) && change > 0f) internalAddMolecule(molecule, change, shouldRefreshReactions);

        if (currentConcentration <= 0f && change < 0f) throw new IllegalArgumentException("Attempted to decrease concentration of Molecule '" + molecule.getFullID()+"', which was not in a Mixture. The Mixture contains " + getContentsString());

        float newConcentration = Math.max(currentConcentration + change, 0f);
        contents.replace(molecule, newConcentration);
        if (newConcentration <= 0f) moleculesToRemove.put(molecule, 10); // Mark this Molecule as imminent for removal - but don't actually remove it in case it gets added back soon
        if (newConcentration > 0f) moleculesToRemove.remove(molecule); // This molecule no longer needs to be removed if it was going to be
        return this;
    };

    /**
     * Get the rate - in moles of Reaction per Bucket <em>per tick</em> (not per second) - at which this {@link Reaction} will proceed in this Mixture.
     * @param reaction
     */
    private float calculateReactionRate(Reaction reaction, ReactionContext context) {
        float rate = reaction.getRateConstant(temperature) / (float) TICKS_PER_SECOND;
        for (Molecule molecule : reaction.getOrders().keySet()) {
            rate *= (float)Math.pow(getConcentrationOf(molecule), reaction.getOrders().get(molecule));
        };
        if (reaction.needsUV()) rate *= context.UVPower;
        return rate;
    };

    /**
     * Determine all {@link Reaction Reactions} - including {@link GenericReactions Generic Reactions} that are possible with the {@link Molecule Molecules} in this Mixture,
     * and update the {@link Mixture#possibleReactions stored possible Reactions} accordingly.
     * This should be called whenever new Molecules have been {@link Mixture#addMolecule added} to the Mixture, or a Molecule has been removed entirely, but rarely otherwise.
     */
    private void refreshPossibleReactions() {
        possibleReactions = new ArrayList<>();
        Set<Reaction> newPossibleReactions = new HashSet<>();

        // Generate specific Generic Reactions
        for (GroupType<?> groupType : groupIDsAndMolecules.keySet()) { // Only search for Generic Reactions of Groups present in this Molecule
            checkEachGenericReaction: for (GenericReaction genericReaction : Group.getReactionsOfGroupByID(groupType)) {

                if (genericReaction.involvesSingleGroup()) { // Generic Reactions involving only one functional Group
                    newPossibleReactions.addAll(specifySingleGroupGenericReactions(genericReaction, groupIDsAndMolecules.get(groupType)));
                
                } else { // Generic Reactions involving two functional Groups
                    if (!(genericReaction instanceof DoubleGroupGenericReaction<?, ?> dggr)) continue checkEachGenericReaction; // This check should never fail
                    if (groupType != dggr.getFirstGroupType()) continue checkEachGenericReaction; // Only generate Reactions when we're dealing with the first Group type
                    
                    GroupType<?> secondGroupType = dggr.getSecondGroupType();
                    if (!groupIDsAndMolecules.keySet().contains(secondGroupType)) continue checkEachGenericReaction; // We can't do this generic reaction if we only have one group type
                    
                    List<Pair<GenericReactant<?>, GenericReactant<?>>> reactantPairs = new ArrayList<>();
                    for (GenericReactant<?> firstGenericReactant : groupIDsAndMolecules.get(groupType)) {
                        for (GenericReactant<?> secondGenericReactant : groupIDsAndMolecules.get(secondGroupType)) {
                            reactantPairs.add(Pair.of(firstGenericReactant, secondGenericReactant));
                        };
                    };

                    newPossibleReactions.addAll(specifyDoubleGroupGenericReactions(dggr, reactantPairs));
                };
            };
        };

        //All Reactions
        for (Molecule possibleReactant : contents.keySet()) {
            newPossibleReactions.addAll(possibleReactant.getReactantReactions());
        };
        for (Reaction reaction : newPossibleReactions) {
            //possibleReactions.add(reaction);

            /* 
             * This checks if all necessary Reactants were present before proceeding, however this leads to some infinite loops
             * where one half of a reversible Reaction would happen one tick, then the other one the next, etc.
             */ 
            Boolean reactionHasAllReactants = true;
            for (Molecule necessaryReactantOrCatalyst : reaction.getOrders().keySet()) {
                if (getConcentrationOf(necessaryReactantOrCatalyst) == 0) {
                    reactionHasAllReactants = false;
                    break;
                };
            };
            if (reactionHasAllReactants) {
                possibleReactions.add(reaction);
            };
        };

    };

    /**
     * Given a {@link SingleGroupGenericReaction Generic Reaction} involving only one {@link Group functional Group},
     * generates the specified {@link Reaction Reactions} that apply to this Mixture.
     * 
     * <p>For example, if the Generic Reaction supplied is the {@link com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydrolysis hydration of an alkene},
     * and <b>reactants</b> includes {@code destroy:ethene}, the returned collection will include a Reaction with {@code destroy:ethene} and {@code destroy:water} as reactants,
     * {@code destroy:ethanol} as a product, and all the appropriate rate constants and catalysts as defined in the {@link com.petrolpark.destroy.chemistry.index.AlkeneHydrolysis.AlkeneHydration#generateReaction generator}.</p>
     * 
     * @param <G> <b>G</b> The Group to which this Generic Reaction applies
     * @param genericReaction
     * @param reactants All {@link GenericReactant Reactants} that have the Group
     * @return A Collection of all specified Reactions
     */
    @SuppressWarnings("unchecked")
    private <G extends Group<G>> List<Reaction> specifySingleGroupGenericReactions(GenericReaction genericReaction, List<GenericReactant<?>> reactants) {
        List<Reaction> reactions = new ArrayList<>();
        SingleGroupGenericReaction<G> singleGroupGenericReaction = (SingleGroupGenericReaction<G>) genericReaction; // Unchecked conversion
        for (GenericReactant<?> reactant : reactants) {
            try {
                Reaction reaction = singleGroupGenericReaction.generateReaction((GenericReactant<G>)reactant);
                if (reaction != null) reactions.add(reaction); // Unchecked conversion
            } catch(ChemistryException e) {
                // Don't do anything for chemistry exceptions
            };
        };
        return reactions;
    };

    /**
     * Given a {@link DoubleGroupGenericReaction Generic Reaction} involving two {@link Group functional Groups},
     * generates the specified {@link Reaction Reactions} that apply to this Mixture.
     * 
     * <p>For example, if the Generic Reaction supplied is {@link com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideEsterification esterification},
     * and this this Mixture contains methanoyl chloride, ethanoyl chloride, and ethanol, the returned collection will include two Reactions, one of which makes 
     * ethyl ethanoate and the other ethyl methanoate.</p>
     * 
     * @param <G1> <b>G1</b> The first Group to which this Generic Reaction applies
     * @param <G2> <b>G2</b> The second Group to which this Generic Reaction applies
     * @param genericReaction
     * @param reactantPairs All possible pairs of {@link GenericReactant Reactants} in this Mixture
     * @return A Collection of specified Reactions
     * @see Mixture#specifySingleGroupGenericReactions A more in-depth description
     */
    @SuppressWarnings("unchecked")
    private <G1 extends Group<G1>, G2 extends Group<G2>> List<Reaction> specifyDoubleGroupGenericReactions(GenericReaction genericReaction, List<Pair<GenericReactant<?>, GenericReactant<?>>> reactantPairs) {
            DoubleGroupGenericReaction<G1, G2> doubleGroupGenericReaction = (DoubleGroupGenericReaction<G1, G2>) genericReaction; // Unchecked conversion
            List<Reaction> reactions = new ArrayList<>();
            for (Pair<GenericReactant<?>, GenericReactant<?>> reactantPair : reactantPairs) {
                if (reactantPair.getFirst().getMolecule() == reactantPair.getSecond().getMolecule()) continue; // Cannot React Molecules with themselves
                try {
                    Reaction reaction = doubleGroupGenericReaction.generateReaction((GenericReactant<G1>)reactantPair.getFirst(), (GenericReactant<G2>)reactantPair.getSecond());
                    if (reaction != null) reactions.add(reaction); // Unchecked conversions {
                } catch(ChemistryException e) {
                    // Do nothing for chemistry exceptions
                };
            };
            return reactions;
    };

    public static boolean areVeryClose(Float f1, Float f2) {
        return Math.abs(f1 - f2) <= 0.0001f;
    };

    /**
     * The context for the {@link Mixture#reactForTick reaction} of a {@link Mixture}.
     * <strong>Do not modify its fields, or anything contained within them.</em>
     */
    public static class ReactionContext {

        public final ImmutableList<ItemStack> availableItemStacks;
        public final float UVPower;

        public ReactionContext(List<ItemStack> availableItemStacks, float UVPower) {
            this.availableItemStacks = ImmutableList.copyOf(availableItemStacks);
            this.UVPower = UVPower;
        };
    };
};
