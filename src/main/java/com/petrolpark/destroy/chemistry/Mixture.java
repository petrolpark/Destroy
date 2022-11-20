package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mixture {
    private Map<Molecule, Float> contents;
    private List<Reaction> possibleReactions;

    public Mixture() {
        contents = new HashMap<>();
        possibleReactions = new ArrayList<>();
    };

    public void pee() {
        for (Molecule molecule : contents.keySet()) {
            System.out.println(molecule.getName().getString() + molecule.getReactantReactions().size());
        };
    };

    public Mixture addMolecule(Molecule molecule, float concentration) {
        return addMolecule(molecule, concentration, true);
    };

    public float getConcentrationOf(Molecule molecule) {
        if (contents.containsKey(molecule)) {
            return contents.get(molecule);
        } else {
            return 0f;
        }
    };

    public Mixture react() {
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
                    Float currentConcentration = contents.get(reactant);
                    contents.replace(reactant, currentConcentration - (molesOfReaction * reaction.getReactantMolarRatio(reactant)));
                };

                for (Molecule product : reaction.getProducts()) {
                    Float currentConcentration = getConcentrationOf(product);
                    if (currentConcentration == 0f) {
                        shouldRefreshPossibleReactions = true;
                        addMolecule(product, molesOfReaction + reaction.getProductMolarRatio(product), false);
                    } else {
                        contents.replace(product, currentConcentration + (molesOfReaction * reaction.getProductMolarRatio(product)));
                    };
                };
            };

            for (Molecule molecule : oldContents.keySet()) {
                if (!areVeryClose(oldContents.get(molecule), contents.get(molecule))) {
                    concentrationsChanged = true;
                };
            };

            if (shouldRefreshPossibleReactions) {
                refreshPossibleReactions();
            };
            
        };

        return this;
    };

    private Mixture addMolecule(Molecule molecule, float concentration, Boolean shouldRefreshReactions) {
        contents.put(molecule, concentration);
        if (shouldRefreshReactions) refreshPossibleReactions();
        return this;
    };

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
            System.out.println(reaction.getName());
            Boolean reactionHasAllReactants = true;
            for (Molecule necessaryReactantOrCatalyst : reaction.getOrders().keySet()) {
                if (!contents.containsKey(necessaryReactantOrCatalyst)) {
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
        return Math.abs(f1 - f2) <= 0.001;
    };
}
