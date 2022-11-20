package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.network.chat.Component;

public class Reaction {
    private Map<Molecule, Integer> reactants, products, orders;
    private float rateConstant;
    private Component name;

    private Reaction(Map<Molecule, Integer> reactants, Map<Molecule, Integer> products, Map<Molecule, Integer> orders, float rateConstant, Component name) {
        this.reactants = reactants;
        this.products = products;
        this.orders = orders;
        this.rateConstant = rateConstant;
        this.name = name;
    };

    public Boolean containsReactant(Molecule molecule) {
        return this.reactants.keySet().contains(molecule);
    };

    public Boolean containsProduct(Molecule molecule) {
        return this.products.keySet().contains(molecule);
    };

    public Set<Molecule> getReactants() {
        return this.reactants.keySet();
    };

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

    public static class ReactionBuilder {

        Map<Molecule, Integer> reactants = new HashMap<>();
        Map<Molecule, Integer> products = new HashMap<>();
        Map<Molecule, Integer> orders = new HashMap<>();
        float rateConstant;
        Component name = Component.empty();

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

        public Reaction build() {
            System.out.println("therre are "+reactants.size()+" reactants");
            Reaction newReaction = new Reaction(reactants, products, orders, rateConstant, name);
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
