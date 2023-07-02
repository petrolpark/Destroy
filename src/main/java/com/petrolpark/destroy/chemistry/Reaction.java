package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.Destroy;

public class Reaction {

    /**
     * The set of all Reactions known to Destroy.
     */
    public static final Set<Reaction> REACTIONS = new HashSet<>();

    public static ReactionBuilder generatedReactionBuilder() {
        return new ReactionBuilder(new Reaction("novel"), true);
    };

    private Map<Molecule, Integer> reactants, products, orders;

    private float preexponentialFactor;
    private float activationEnergy;

    private String nameSpace;
    private String translationKey;

    public static final Float GAS_CONSTANT = 8.3145f;

    private Reaction(String nameSpace) {
        this.nameSpace = nameSpace;
    };

    /**
     * Whether this Molecule gets consumed in this Reaction (does not include catalysts).
     */
    public Boolean containsReactant(Molecule molecule) {
        return this.reactants.keySet().contains(molecule);
    };

    /**
     * Whether this Molecule is created in this Reaction.
     */
    public Boolean containsProduct(Molecule molecule) {
        return this.products.keySet().contains(molecule);
    };

    /**
     * All Molecules which are consumed in this Reaction (but not their molar ratios).
     */
    public Set<Molecule> getReactants() {
        return this.reactants.keySet();
    };

    /**
     * All Molecules which are created in this Reaction (but not their molar ratios).
     */
    public Set<Molecule> getProducts() {
        return this.products.keySet();
    };

    /**
     * The rate constant of this Reaction at the given temperature.
     * @param temperature (in kelvins).
     */
    public float getRateConstant(float temperature) {
        return preexponentialFactor * (float)Math.exp(-((activationEnergy * 1000) / (GAS_CONSTANT * temperature)));
    };

    /**
     * The translation key for this Reaction.
     * {@code <namespace>.reaction.<translationKey>} should hold the name of this Reaction,
     * and {@code <namespace>.reaction.<translationKey>.description} should hold the
     * description of this Reaction.
     */
    public String getTranslationKey() {
        return translationKey;
    };

    /**
     * The name space of the mod by which this Reaction was defined.
     * @return {@code "novel"} if this was generated automatically by a {@link com.petrolpark.destroy.chemistry.genericReaction.GenericReaction Reaction generator}.
     */
    public String getNameSpace() {
        return nameSpace;
    };

    public Integer getReactantMolarRatio(Molecule reactant) {
        if (!reactants.keySet().contains(reactant)) {
            return 0;
        } else {
            return reactants.get(reactant);
        }
    };

    public Integer getProductMolarRatio(Molecule product) {
        if (!products.keySet().contains(product)) {
            return 0;
        } else {
            return products.get(product);
        }
    };

    public Map<Molecule, Integer> getOrders() {
        return this.orders;
    };

    public static class ReactionBuilder {

        final boolean generated;
        Reaction reaction;

        private boolean hasForcedPreExponentialFactor;
        private boolean hasForcedActivationEnergy;

        private ReactionBuilder(Reaction reaction, boolean generated) {
            this.generated = generated;
            this.reaction = reaction;
            reaction.reactants = new HashMap<>();
            reaction.products = new HashMap<>();
            reaction.orders = new HashMap<>();

            hasForcedPreExponentialFactor = false;
            hasForcedActivationEnergy = false;
        };

        public ReactionBuilder(String namespace) {
            this(new Reaction(namespace), false);
        };

        public ReactionBuilder addReactant(Molecule molecule) {
            return addReactant(molecule, 1);
        };

        public ReactionBuilder addReactant(Molecule molecule, int ratio) {
            return addReactant(molecule, ratio, ratio);
        };

        public ReactionBuilder addReactant(Molecule molecule, int ratio, int order) {
            reaction.reactants.put(molecule, ratio);
            reaction.orders.put(molecule, order);
            return this;
        };

        public ReactionBuilder addProduct(Molecule molecule) {
            return addProduct(molecule, 1);
        };

        public ReactionBuilder addProduct(Molecule molecule, int ratio) {
            reaction.products.put(molecule, ratio);
            return this;
        };

        public ReactionBuilder addCatalyst(Molecule molecule, int order) {
            reaction.orders.put(molecule, order);
            return this;
        };

        public ReactionBuilder translationKey(String translationKey) {
            reaction.translationKey = translationKey;
            return this;
        };

        /**
         * The pre-exponential factor in the Arrhenius equation for this Reaction.
         * @param preexponentialFactor
         */
        public ReactionBuilder preexponentialFactor(float preexponentialFactor) {
            reaction.preexponentialFactor = preexponentialFactor;
            hasForcedPreExponentialFactor = true;
            return this;
        };

        /**
         * The activation energy (in kJ/mol) for this Reaction.
         * If no activation energy is given, defaults to 100kJ/mol.
         * @param activationEnergy
         */
        public ReactionBuilder activationEnergy(float activationEnergy) {
            reaction.activationEnergy = activationEnergy;
            hasForcedActivationEnergy = true;
            return this;
        };

        public Reaction build() {

            if (!hasForcedActivationEnergy || reaction.activationEnergy <= 0f) {
                reaction.activationEnergy = 100f;
                Destroy.LOGGER.warn("Activation energy of reaction '"+reactionString()+"' was missing or invalid, so estimated as 100kJ.");
            };

            if (!hasForcedPreExponentialFactor || reaction.preexponentialFactor <= 0f) {
                reaction.preexponentialFactor = 1e6f;
                Destroy.LOGGER.warn("Pre-exponential factor of reaction '"+reactionString()+"' was missing or invalid, so was estimated as 1e6.");
            };

            if (!generated) {
                for (Molecule reactant : reaction.reactants.keySet()) {
                    reactant.addReactantReaction(reaction);
                };
                for (Molecule product : reaction.products.keySet()) {
                    product.addProductReaction(reaction);
                };
                REACTIONS.add(reaction);
            };
            
            return reaction;
        };

        private String reactionString() {
            String reactionString = "";
            for (Molecule reactant : reaction.reactants.keySet()) {
                reactionString += reactant.getSerlializedMolecularFormula();
                reactionString += " + ";
            };
            reactionString = reactionString.substring(0, reactionString.length() - 3) + " => ";
            for (Molecule product : reaction.products.keySet()) {
                reactionString += product.getSerlializedMolecularFormula();
                reactionString += " + ";
            };
            reactionString = reactionString.substring(0, reactionString.length() - 3);
            return reactionString;
        };
    };
}
