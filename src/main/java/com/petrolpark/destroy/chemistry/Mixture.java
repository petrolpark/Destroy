package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;

import net.minecraft.nbt.CompoundTag;

public class Mixture extends ReadOnlyMixture {

    private static final int TICKS_PER_SECOND = 20;

    /**
     * {@link Molecule Molecules} which can donate protons.
     */
    private List<Molecule> acids;

    /**
     * {@link Molecule Molecules} which do not have a name space or ID.
     */
    private List<Molecule> novelMolecules;

    /**
     * All {@link Reaction Reactions} with specific Reactants and specified {@link GenericReaction Generic Reactions}
     * which are possible given the {@link Molecule Molecules} in this Mixture.
     */
    private List<Reaction> possibleReactions;

    /**
     * Every {@link Molecule} in this Mixture that has a {@link Group functional Group}, indexed by the {@link Group#getID ID} of that Group.
     * Molecules are stored as {@link com.petrolpark.destroy.chemistry.genericreaction.GenericReactant Generic Reactants}.
     * Molecules which have multiple of the same Group are indexed for each occurence of the Group.
     */
    private Map<String, List<GenericReactant<?>>> groupIDsAndMolecules;

    /**
     * Whether this Mixture has reached equilibrium. This means either:
     * <ul>
     * <li>No more {@link Reaction Reactions} will occur.</li>
     * <li>Any further Reactions that occur will balance each other out and there will be no change in concentration of any {@link Molecule}.</li>
     * </ul>
     */
    private boolean equilibrium;

    public Mixture() {
        super();

        acids = new ArrayList<>();
        novelMolecules = new ArrayList<>();

        possibleReactions = new ArrayList<>();
        groupIDsAndMolecules = new HashMap<>();

        equilibrium = false;
    };

    public static Mixture readNBT(CompoundTag compound) {
        Mixture mixture = (Mixture)ReadOnlyMixture.readNBT(compound);
        mixture.refreshPossibleReactions();
        return mixture;
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
            return this;
        };

        // If we're not adding a pre-existing Molecule
        internalAddMolecule(molecule, concentration, true);
        return this;
    };

    @Override
    public float getConcentrationOf(Molecule molecule) {
        if (molecule == DestroyMolecules.PROTON) {
            return 0f; //TODO determine pH
        };
        return super.getConcentrationOf(molecule);
    };

    /**
     * Checks that this Mixture contains a suitable concentration of the given {@link Molecule}, and that all other substances present are solvents or low-concentration impurities.
     * This is used in Recipes.
     * @param molecule Only known (non-novel) Molecules (i.e. those with a name space) will be detected
     * @param concentration
     */
    public boolean hasUsableMolecule(Molecule molecule, float concentration) {
        if (!contents.containsKey(molecule)) return false;
        if (!areVeryClose(concentration, getConcentrationOf(molecule))) return false; //TODO replace with a more lenient check
        for (Entry<Molecule, Float> otherMolecule : contents.entrySet()) {
            if (otherMolecule.getKey() == molecule) continue; // If this is the Molecule we want, ignore it.
            if (otherMolecule.getKey().hasTag(DestroyMolecules.Tags.SOLVENT)) continue; // If this is a solvent, ignore it
            if (otherMolecule.getValue() < IMPURITY_THRESHOLD) continue; // If this impurity is in low-enough concentration, ignore it.
            return false;
        };
        return true;
    };

    /**
     * Reacts the contents of this Mixture for one tick, if it is not already at equilibrium.
     */
    public Set<ReactionResult> reactForTick() {

        Set<ReactionResult> results = new HashSet<>();
        if (equilibrium) return results; // If we have already reached equilibrium, nothing more is going to happen, so don't bother reacting

        equilibrium = true; // Start by assuming we have reached equilibrium
        Boolean shouldRefreshPossibleReactions = false; // Rather than refreshing the possible Reactions every time a new Molecule is added or removed, start by assuming we won't need to, and flag for refreshing if we ever do

        Map<Molecule, Float> oldContents = new HashMap<>(contents); // Copy all the old concentrations of everything
        Map<Reaction, Float> reactionRates = new HashMap<>(); // Rates of all Reactions
        List<Reaction> orderedReactions = new ArrayList<>(); // A list of Reactions in the order of their current rate, fastest first

        for (Reaction possibleReaction : possibleReactions) {
            reactionRates.put(possibleReaction, calculateReactionRate(possibleReaction)); // Calculate the Reaction data for this tick
            orderedReactions.add(possibleReaction); // Add the Reaction to the rate-ordered list, which is currently not sorted
        };

        Collections.sort(orderedReactions, (r1, r2) -> reactionRates.get(r1).compareTo(reactionRates.get(r2))); // Sort the Reactions by rate

        for (Reaction reaction : orderedReactions) { // Go through each Reaction, fastest first

            Float molesOfReaction = reactionRates.get(reaction); // We are reacting over one tick, so moles of Reaction that take place in this time = rate of Reaction in M per tick

            for (Molecule reactant : reaction.getReactants()) {
                int reactantMolarRatio = reaction.getReactantMolarRatio(reactant);
                if (contents.get(reactant) < reactantMolarRatio * molesOfReaction) { // Determine the limiting reagent, if there is one
                    molesOfReaction = contents.get(reactant) / (float) reactantMolarRatio; // If there is a new limiting reagent, alter the moles of reaction which will take place
                    shouldRefreshPossibleReactions = true; // If there is a new limiting reagent, one Molecule is going to be used up, so the possible Reactions will change
                };
            };

            for (Molecule reactant : reaction.getReactants()) {
                changeConcentrationOf(reactant, - (molesOfReaction * reaction.getReactantMolarRatio(reactant)), false); // Use up the right amount of all the reagents
            };

            addEachProduct: for (Molecule product : reaction.getProducts()) {
                
                if (product.isNovel() && getConcentrationOf(product) == 0f) { // If we have a novel Molecule that we don't think currently exists in the Mixture...
                    if (internalAddMolecule(product, molesOfReaction * reaction.getProductMolarRatio(product), false)) { // ...add it with this method, as this automatically checks for pre-existing novel Molecules, and if it was actually a brand new Molecule...
                        shouldRefreshPossibleReactions = true; // ...flag this
                    }; 
                    continue addEachProduct;
                };

                if (getConcentrationOf(product) == 0f) { // If we are adding a new product, the possible Reactions will change
                    shouldRefreshPossibleReactions = true;
                };
                changeConcentrationOf(product, molesOfReaction * reaction.getProductMolarRatio(product), false); // Increase the concentration of the product
            };
        };

        // Check now if we have actually reached equilibrium or if that was a false assumption at the start
        for (Molecule molecule : oldContents.keySet()) {
            if (!areVeryClose(oldContents.get(molecule), getConcentrationOf(molecule))) { // If there's something that has changed concentration noticeably in this tick...
                equilibrium = false; // ...we cannot have reached equilibrium
            };
        };

        if (shouldRefreshPossibleReactions) { // If we added a new Molecule or removed an old one at any point
            refreshPossibleReactions();
        };

        updateName();

        return results;
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
     * @return {@code true} if a brand new Molecule that was not already in this Mixture was added; {@code false} otherwise
     * @see Mixture#addMolecule The wrapper for this method
     * @see Mixture#changeConcentrationOf Modifying the concentration of pre-existing Molecule
     */
    private boolean internalAddMolecule(Molecule molecule, float concentration, Boolean shouldRefreshReactions) {

        super.addMolecule(molecule, concentration);

        boolean newMoleculeAdded = true; // Start by assuming we're adding a brand new Molecule to this solution

        List<Group> functionalGroups = molecule.getFunctionalGroups();
        if (functionalGroups.size() != 0) {
            for (Group group : functionalGroups) {
                String groupID = group.getID();
                if (!groupIDsAndMolecules.containsKey(groupID)) {
                    groupIDsAndMolecules.put(groupID, new ArrayList<>());
                };
                groupIDsAndMolecules.get(groupID).add(new GenericReactant<>(molecule, group));
            };
        };

        //TODO check for acids

        if (molecule.isNovel()) { // If this is a novel Molecule, it might already match to one of our existing novel Molecules
            boolean found = false; // Start by assuming it's not already in the Mixture
            for (Molecule novelMolecule : novelMolecules) { // Check every novel Molecule
                if (novelMolecule.getFullID().equals(molecule.getFullID())) {
                    found = true;
                    newMoleculeAdded = false; // We haven't actually added a brand new Molecule so flag this
                    changeConcentrationOf(molecule, concentration, true);
                };
            };
            if (!found) novelMolecules.add(molecule); // If it was actually a brand new Molecule, add it to the novel list
        };

        if (shouldRefreshReactions && newMoleculeAdded) {
            refreshPossibleReactions();
        };

        equilibrium = false; // Now that there's a new Molecule we cannot guarantee equilibrium

        return newMoleculeAdded; // Return whether or not we actually added a brand new Molecule
    };

    /**
     * Removes the given {@link Molecule} from this Mixture, if that Molecule is already in it.
     * This does not refresh possible {@link Reaction Reactions}.
     * @param molecule
     * @return This Mixture
     */
    private Mixture removeMolecule(Molecule molecule) {
        if (acids.contains(molecule)) {
            //TODO remove from acids
        };

        List<Group> functionalGroups = molecule.getFunctionalGroups();
        if (functionalGroups.size() != 0) {
            for (Group group : functionalGroups) {
                groupIDsAndMolecules.get(group.getID()).removeIf((reactant) -> {
                    return reactant.getMolecule() == molecule;
                });
            };
        };

        contents.remove(molecule);
        equilibrium = false; // Now that a Molecule has been removed we cannot guarantee equilibrium

        return this;
    };

    /**
     * Alters the concentration of a {@link Molecule} in a Mixture.
     * This does not update the {@link ReadOnlyMixture#getName name} of the Mixture.
     * @param molecule If not present in the Mixture, will be added to the Mixture
     * @param change The <em>change</em> in concentration, not the new value (can be positive or negative)
     * @param shouldRefreshReactions Whether to alter the possible {@link Reaction Reactions} in the case that a new Molecule is added to the Mixture
     */
    private Mixture changeConcentrationOf(Molecule molecule, Float change, boolean shouldRefreshReactions) {
        Float currentConcentration = getConcentrationOf(molecule);

        if (molecule == DestroyMolecules.PROTON) {
            Destroy.LOGGER.warn("Attempted to change concentration of H+");
            return this; // Concentration of H+ should never be altered by changing H+ concentration directly - only by changing concentration of acids
        };

        if (currentConcentration == 0f) {
            if (change > 0) {
                internalAddMolecule(molecule, change, shouldRefreshReactions);
            } else {
                Destroy.LOGGER.warn("Attempted to change concentration of a Molecule which was not in the Mixture.");
            };
        };

        float newConcentration = currentConcentration + change;
        if (newConcentration <= 0f) {
            removeMolecule(molecule);
        } else {
            contents.replace(molecule, newConcentration);
        };
        
        return this;
    };

    /**
     * Get the rate - in moles of Reaction per liter <em>per tick</em> (not per second) - at which this {@link Reaction} will proceed in this Mixture.
     * @param reaction
     */
    private float calculateReactionRate(Reaction reaction) {
        float rate = reaction.getRateConstant(temperature) / (float) TICKS_PER_SECOND;
        for (Molecule molecule : reaction.getOrders().keySet()) {
            rate *= (float)Math.pow(getConcentrationOf(molecule), reaction.getOrders().get(molecule));
        };
        return rate;
    };

    /**
     * Determine all {@link Reaction Reactions} - including {@link GenericReactions Generic Reactions} that are possible with the {@link Molecule Molecules} in this Mixture,
     * and update the {@link Mixture#possibleReactions stored possible Reactions} accordingly.
     * This should be called whenever new Molecules have been {@link Mixture#addMolecule added} to the Mixture, or a Molecule has been removed entirely.
     */
    private void refreshPossibleReactions() {
        possibleReactions = new ArrayList<>();
        Set<Reaction> newPossibleReactions = new HashSet<>();

        // Generate specific Generic Reactions
        for (String id : groupIDsAndMolecules.keySet()) {
            for (GenericReaction genericReaction : Group.getReactionsOfGroupByID(id)) {
                if (genericReaction.involvesSingleGroup()) { // Generic Reactions involving only one functional Group
                    newPossibleReactions.addAll(specifySingleGroupGenericReactions(genericReaction, groupIDsAndMolecules.get(id)));
                } else { // Generic Reactions involving two functional Groups
                    //TODO
                    //TODO check for polymerisation
                };
            };
        };

        //All Reactions
        for (Molecule possibleReactant : contents.keySet()) {
            newPossibleReactions.addAll(possibleReactant.getReactantReactions());
        };
        for (Reaction reaction : newPossibleReactions) {
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
     * <p>For example, if the Generic Reaction supplied is the {@link com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydration hydration of an alkene},
     * and <b>reactants</b> includes {@code destroy:ethene}, the returned collection will include a Reaction with {@code destroy:ethene} and {@code destroy:water} as reactants,
     * {@code destroy:ethanol} as a product, and all the appropriate rate constants and catalysts as defined in the {@link com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydration#generateReaction generator}.</p>
     * 
     * @param <G> <b>G</b> The Group to which this Generic Reaction applies
     * @param genericReaction
     * @param reactants All {@link GenericReactant Reactants} that have the Group.
     * @return A Collection of all specified Reactions.
     */
    @SuppressWarnings("unchecked")
    private <G extends Group> List<Reaction> specifySingleGroupGenericReactions(GenericReaction genericReaction, List<GenericReactant<?>> reactants) {
        try {
            SingleGroupGenericReaction<G> singleGroupGenericReaction = (SingleGroupGenericReaction<G>) genericReaction; // Unchecked conversion
            List<Reaction> reactions = new ArrayList<>();
            for (GenericReactant<?> reactant : reactants) {
                reactions.add(singleGroupGenericReaction.generateReaction((GenericReactant<G>)reactant)); // Unchecked conversion
            };
            return reactions;
        } catch(Error e) {
            throw new IllegalStateException("Wasn't able to generate Single-Group Reaction: " + e);
        }
    };

    private boolean areVeryClose(Float f1, Float f2) {
        return Math.abs(f1 - f2) <= 0.00001f;
    };
}
