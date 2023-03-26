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

    private List<Molecule> acids;
    private List<Molecule> novelMolecules;

    private List<Reaction> possibleReactions;
    private Map<String, List<GenericReactant<?>>> groupIDsAndMolecules;

    public Mixture() {
        super();

        acids = new ArrayList<>();
        novelMolecules = new ArrayList<>();

        possibleReactions = new ArrayList<>();
        groupIDsAndMolecules = new HashMap<>();
    };

    /**
     * Generates a Mixture from the given Compound Tag, and generates the Reactions for it.
     * @param compound
     */
    public static Mixture readNBT(CompoundTag compound) {
        Mixture mixture = (Mixture)ReadOnlyMixture.readNBT(compound);
        mixture.refreshPossibleReactions();
        return mixture;
    };

    @Override
    public Mixture addMolecule(Molecule molecule, float concentration) {
        return internalAddMolecule(molecule, concentration, true);
    };

    @Override
    public float getConcentrationOf(Molecule molecule) {
        if (molecule == DestroyMolecules.PROTON) {
            return 0f; //TODO determine pH
        };
        return super.getConcentrationOf(molecule);
    };

    /**
     * Checks that this Mixture contains a suitable concentration of the given Molecule, and that all other substances present are solvents or low-concentration impurities.
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

    public Set<ReactionResult> react() {

        Set<ReactionResult> results = new HashSet<>();

        Boolean concentrationsChanged = true;
        while (concentrationsChanged) {
            concentrationsChanged = false;

            Map<Molecule, Float> oldContents = new HashMap<>(contents);
            Map<Reaction, Float> reactionRates = new HashMap<>();
            List<Reaction> orderedReactions = new ArrayList<>();

            for (Reaction possibleReaction : possibleReactions) {
                reactionRates.put(possibleReaction, calculateReactionRate(possibleReaction));
                orderedReactions.add(possibleReaction);
            };

            Collections.sort(orderedReactions, (r1, r2) -> {
                return reactionRates.get(r1).compareTo(reactionRates.get(r2));
            });

            Boolean shouldRefreshPossibleReactions = false;

            for (Reaction reaction : orderedReactions) {

                Float molesOfReaction = reactionRates.get(reaction);

                for (Molecule reactant : reaction.getReactants()) {
                    if (contents.get(reactant) < reaction.getReactantMolarRatio(reactant) * molesOfReaction) { // Determine the limiting reagent, if there is one
                        molesOfReaction = contents.get(reactant);
                        shouldRefreshPossibleReactions = true;
                    };
                };

                for (Molecule reactant : reaction.getReactants()) {
                    changeConcentrationOf(reactant, - (molesOfReaction * reaction.getReactantMolarRatio(reactant)));
                };

                for (Molecule product : reaction.getProducts()) {

                    //Check if a new novel Molecule already exists in solution
                    if (product.isNovel() && getConcentrationOf(product) == 0f) {
                        Boolean matchFound = false;
                        for (Molecule novelMolecule : novelMolecules) {
                            if (product.getFullID().equals(novelMolecule.getFullID())) {
                                matchFound = true;
                                product = novelMolecule;
                                break;
                            };
                        };
                        if (!matchFound) {
                            novelMolecules.add(product);
                            shouldRefreshPossibleReactions = true;
                            internalAddMolecule(product, molesOfReaction * reaction.getProductMolarRatio(product), false);
                            continue;
                        };
                    };

                    changeConcentrationOf(product, molesOfReaction * reaction.getProductMolarRatio(product));
                };
            };

            for (Molecule molecule : oldContents.keySet()) {
                if (!areVeryClose(oldContents.get(molecule), getConcentrationOf(molecule))) {
                    concentrationsChanged = true;
                };
            };

            if (shouldRefreshPossibleReactions) {
                refreshPossibleReactions();
            };
            
        };

        updateName();

        return results;
    };

    private Mixture internalAddMolecule(Molecule molecule, float concentration, Boolean shouldRefreshReactions) {

        super.addMolecule(molecule, concentration);

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

        if (shouldRefreshReactions) {
            refreshPossibleReactions();
        };
        return this;
    };

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

        return this;
    };

    /**
     * Alters the concentration of a Molecule in a solution.
     * @param molecule If not present in the Mixture, this Molecule will be added to the Mixture and the possible Reactions will be altered.
     * @param change The <em>change</em> in concentration, not the new value (can be positive or negative).
     */
    private Mixture changeConcentrationOf(Molecule molecule, Float change) {
        Float currentConcentration = getConcentrationOf(molecule);

        if (molecule == DestroyMolecules.PROTON) {
            return this; //concentration of H+ should never be altered by changing H+ concentration directly - only by changing concentration of acids
        };

        if (currentConcentration == 0f) {
            if (change > 0) {
                addMolecule(molecule, change);
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
        
        updateName();
        return this;
    };

    /**
     * Get the rate (in moles of Reaction per liter per second) at which this Reaction will proceed in this Mixture.
     * @param reaction
     */
    private float calculateReactionRate(Reaction reaction) {
        float rate = reaction.getRateConstant(temperature);
        for (Molecule molecule : reaction.getOrders().keySet()) {
            rate = rate * (float)Math.pow(contents.get(molecule), reaction.getOrders().get(molecule));
        };
        return rate;
    };

    private void refreshPossibleReactions() {
        System.out.println("refreshing reactions for some god forsaken reason");
        possibleReactions = new ArrayList<>();
        Set<Reaction> newPossibleReactions = new HashSet<>();

        //Generate specific Generic Reactions
        for (String id : groupIDsAndMolecules.keySet()) {
            for (GenericReaction genericReaction : Group.getReactionsOfGroupByID(id)) {
                if (genericReaction.involvesSingleGroup()) { // Generic Reactions involving only one functional Group
                    newPossibleReactions.addAll(specifySingleGroupGenericReactions(genericReaction, groupIDsAndMolecules.get(id)));
                } else { // Generic Reactions involving two functional Groups
                    //TODO
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

    @SuppressWarnings("unchecked")
    private <T extends Group> List<Reaction> specifySingleGroupGenericReactions(GenericReaction genericReaction, List<GenericReactant<?>> reactants) {
        try {
            SingleGroupGenericReaction<T> singleGroupGenericReaction = (SingleGroupGenericReaction<T>) genericReaction; // Unchecked conversion
            List<Reaction> reactions = new ArrayList<>();
            for (GenericReactant<?> reactant : reactants) {
                reactions.add(singleGroupGenericReaction.generateReaction((GenericReactant<T>)reactant)); // Unchecked conversion
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
