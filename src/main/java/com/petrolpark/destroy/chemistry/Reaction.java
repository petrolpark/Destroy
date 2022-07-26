package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.contraptions.processing.HeatCondition;

import net.minecraft.network.chat.Component;

public class Reaction {
    private Map<Molecule, Integer> reactants, products, orders;
    private Float preexponentialFactor;
    private Float activationEnergy;
    private Component name;

    private static final Float GAS_CONSTANT = 8.3145f;

    private Reaction() {};

    /**
     * Whether this Molecule gets consumed in this Reaction (does not include Catalysts).
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
     * @param temperature (in Kelvins).
     */
    public Float getRateConstant(Float temperature) {
        return (float)(preexponentialFactor * Math.exp(-((activationEnergy * 1000) / (GAS_CONSTANT * temperature))));
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

    public Component getName() {
        return this.name;
    };
 
    public static ReactionBuilder builder() {
        return new ReactionBuilder();
    };

    public static class ReactionBuilder {

        Reaction reaction;

        public ReactionBuilder() {
            reaction = new Reaction();
            reaction.reactants = new HashMap<>();
            reaction.products = new HashMap<>();
            reaction.orders = new HashMap<>();
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

        /**
         * The pre-exponential factor in the Arrhenius equation for this Reaction.
         * @param preexponentialFactor
         */
        public ReactionBuilder preexponentialFactor(Float preexponentialFactor) {
            reaction.preexponentialFactor = preexponentialFactor;
            return this;
        };

        /**
         * The activation energy (in kJ/mol) for this Reaction.
         * If no activation energy is given, defaults to 100kJ/mol.
         * @param activationEnergy
         */
        public ReactionBuilder activationEnergy(Float activationEnergy) {
            reaction.activationEnergy = activationEnergy;
            return this;
        };

        public ReactionBuilder translationKey(String translationKey) {
            reaction.name = DestroyLang.translate(translationKey).component();
            return this;
        };

        public ReactionBuilder name(Component name) {
            reaction.name = name;
            return this;
        };

        public Reaction build() {

            if (reaction.activationEnergy == null) {
                reaction.activationEnergy = 100f;
                Destroy.LOGGER.warn("Activation energy of reaction was not supplied and had to be estimated.");
            };

            if (reaction.activationEnergy == null) {
                reaction.activationEnergy = 1e6f;
                Destroy.LOGGER.warn("Pre-exponential factor of reaction was not supplied and had to be estimated.");
            };

            for (Molecule reactant : reaction.reactants.keySet()) {
                reactant.addReactantReaction(reaction);
            };
            for (Molecule product : reaction.products.keySet()) {
                product.addProductReaction(reaction);
            };
            return reaction;
        };
    };
}
