package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.content.contraptions.processing.HeatCondition;

import net.minecraft.network.chat.Component;

public class Reaction {
    private Map<Molecule, Integer> reactants, products, orders;
    private float rateConstant;
    private Component name;
    private HeatCondition heatRequirement;

    private Reaction(Map<Molecule, Integer> reactants, Map<Molecule, Integer> products, Map<Molecule, Integer> orders, float rateConstant, Component name, HeatCondition heatRequirement) {
        this.reactants = reactants;
        this.products = products;
        this.orders = orders;
        this.rateConstant = rateConstant;
        this.name = name;
        this.heatRequirement = heatRequirement;
    };

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

    public Float getRateConstant() {
        return this.rateConstant;
    };

    public Component getName() {
        return this.name;
    };
 
    public static ReactionBuilder builder() {
        return new ReactionBuilder();
    };

    public Boolean willTakePlaceWithHeat(HeatCondition heatCondition) {
        if (heatRequirement == HeatCondition.SUPERHEATED || heatRequirement == HeatCondition.valueOf("COOLED")) {
            return heatCondition == heatRequirement;
        } else if (heatRequirement == HeatCondition.NONE) {
            return heatCondition != HeatCondition.valueOf("COOLED");
        } else { //if Heat Requirement is "HEATED"
            return heatCondition == HeatCondition.SUPERHEATED || heatCondition == HeatCondition.HEATED;
        }
    };

    public static class ReactionBuilder {

        Map<Molecule, Integer> reactants = new HashMap<>();
        Map<Molecule, Integer> products = new HashMap<>();
        Map<Molecule, Integer> orders = new HashMap<>();
        float rateConstant;
        Component name = Component.empty();
        HeatCondition heatRequirement = HeatCondition.NONE;

        public ReactionBuilder addReactant(Molecule molecule) {
            return addReactant(molecule, 1);
        };

        public ReactionBuilder addReactant(Molecule molecule, int ratio) {
            return addReactant(molecule, ratio, ratio);
        };

        public ReactionBuilder addReactant(Molecule molecule, int ratio, int order) {
            reactants.put(molecule, ratio);
            orders.put(molecule, order);
            return this;
        };

        public ReactionBuilder addProduct(Molecule molecule) {
            return addProduct(molecule, 1);
        };

        public ReactionBuilder addProduct(Molecule molecule, int ratio) {
            products.put(molecule, ratio);
            return this;
        };

        public ReactionBuilder addCatalyst(Molecule molecule, int order) {
            orders.put(molecule, order);
            return this;
        };

        public ReactionBuilder rateConstant(float rateConstant) {
            this.rateConstant = rateConstant;
            return this;
        };

        public ReactionBuilder translationKey(String translationKey) {
            this.name = DestroyLang.translate(translationKey).component();
            return this;
        };

        public ReactionBuilder name(Component name) {
            this.name = name;
            return this;
        };

        public ReactionBuilder heatRequirement(HeatCondition heatRequirement) {
            this.heatRequirement = heatRequirement;
            return this;
        };

        public Reaction build() {
            Reaction newReaction = new Reaction(
                reactants,
                products,
                orders,
                rateConstant,
                name,
                heatRequirement);
            for (Molecule reactant : reactants.keySet()) {
                reactant.addReactantReaction(newReaction);
            };
            for (Molecule product : products.keySet()) {
                product.addProductReaction(newReaction);
            };
            return newReaction;
        };
    };
}
