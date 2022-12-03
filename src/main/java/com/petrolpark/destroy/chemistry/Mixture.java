package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.utility.NBTHelper;

import net.minecraft.nbt.CompoundTag;

public class Mixture {

    private Map<Molecule, Float> contents;
    private List<Molecule> acids;
    private List<Reaction> possibleReactions;
    private int temperature;

    public Mixture() {
        contents = new HashMap<>();
        possibleReactions = new ArrayList<>();
        temperature = 0;
    };

    public CompoundTag writeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Temperature", temperature);
        compound.put("Contents", NBTHelper.writeCompoundList(contents.keySet(), (molecule) -> {
            CompoundTag moleculeTag = new CompoundTag();
            moleculeTag.putString("Molecule", molecule.getFullID());
            moleculeTag.putFloat("Concentration", contents.get(molecule));
            return moleculeTag;
        }));
        return compound;
    };

    public void pee() {
        System.out.println(writeNBT().toString());
        // for (Molecule molecule : contents.keySet()) {
        //     System.out.println(molecule.getName().getString() + contents.get(molecule));
        // };
    };

    public Mixture addMolecule(Molecule molecule, float concentration) {
        return addMolecule(molecule, concentration, true);
    };

    public float getConcentrationOf(Molecule molecule) {

        if (molecule == DestroyMolecules.PROTON) {
            return 0f; //TODO determine pH
        };

        //if we're not dealing with a Proton
        if (contents.containsKey(molecule)) {
            return contents.get(molecule);
        } else {
            return 0f;
        }
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
                    if (contents.get(reactant) < reaction.getReactantMolarRatio(reactant) * molesOfReaction) { //determine the limiting reagent, if there is one
                        molesOfReaction = contents.get(reactant);
                        shouldRefreshPossibleReactions = true;
                    };
                };

                for (Molecule reactant : reaction.getReactants()) {
                    changeConcentrationOf(reactant, - (molesOfReaction * reaction.getReactantMolarRatio(reactant)));
                };

                for (Molecule product : reaction.getProducts()) {
                    if (getConcentrationOf(product) == 0f) {
                        shouldRefreshPossibleReactions = true;
                        addMolecule(product, molesOfReaction * reaction.getProductMolarRatio(product), false);
                    } else {
                        changeConcentrationOf(product, molesOfReaction * reaction.getProductMolarRatio(product));
                    };
                };
            };

            for (Molecule molecule : oldContents.keySet()) {
                if (!areVeryClose(oldContents.get(molecule), contents.get(molecule))) {
                    concentrationsChanged = true;
                };
                if (areVeryClose(oldContents.get(molecule), 0f)) {
                    contents.remove(molecule);
                };
            };

            if (shouldRefreshPossibleReactions) {
                refreshPossibleReactions();
            };
            
        };

        return results;
    };

    private Mixture addMolecule(Molecule molecule, float concentration, Boolean shouldRefreshReactions) {
        if (molecule.isHypothetical()) {
            throw new IllegalStateException("Cannot add hypothetical Molecule "+molecule.getFullID()+ " to a real Mixture.");
        };
        contents.put(molecule, concentration);
        // if (molecule.isAcidic()) { //TODO fiddle about with acids
        //     acids.add(molecule);
        // };
        if (shouldRefreshReactions) refreshPossibleReactions();
        return this;
    };

    /**
     * Alters the concentration of a Molecule in a solution.
     * @param molecule If not present in the Mixture, an error will be raised (use {@link Mixture#addMolecule addMolecule()} instead). If this is the Proton, the concentrations of the conjugate bases in this solution will also be altered.
     * @param change The <em>change</em> in concentration, not the new value (can be positive or negative).
     */
    private Mixture changeConcentrationOf(Molecule molecule, Float change) {
        Float currentConcentration = getConcentrationOf(molecule);
        if (molecule == DestroyMolecules.PROTON) {

        };
        if (!contents.containsKey(molecule)) {
            throw new IllegalStateException("Cannot change concentration of Molecule '"+molecule.getFullID() + "' if it is not in the Mixture");
        };
        contents.replace(molecule, Math.max(0, currentConcentration + change));
        return this;
    };

    /**
     * Get the rate (in moles of Reaction per liter per second) at which this Reaction will proceed in this Mixture.
     * @param reaction
     */
    private float calculateReactionRate(Reaction reaction) {
        float rate = reaction.getRateConstant();
        for (Molecule molecule : reaction.getOrders().keySet()) {
            rate = rate * (float)Math.pow(contents.get(molecule), reaction.getOrders().get(molecule));
        };
        return rate;
    };

    private void refreshPossibleReactions() {
        possibleReactions = new ArrayList<>();
        Set<Reaction> newPossibleReactions = new LinkedHashSet<>();
        for (Molecule possibleReactant : contents.keySet()) {
            newPossibleReactions.addAll(possibleReactant.getReactantReactions());
        };
        for (Reaction reaction : newPossibleReactions) {
            //TODO add temperature check
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

    private Boolean areVeryClose(Float f1, Float f2) {
        return Math.abs(f1 - f2) <= 0.00001f;
    };
}
