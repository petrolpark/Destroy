package com.petrolpark.destroy.chemistry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.network.chat.Component;

public class Molecule {

    private int charge;

    private float mass;

    private int boilingPoint;
    private int dipoleMoment;
    
    private Formula structure;

    private List<Reaction> reactantReactions; //reactions in which this Molecule is a reactant
    private List<Reaction> productReactions; //reactions in which this Molecule is a product

    private Component name;

    private Molecule(int charge, float mass, int boilingPoint, int dipoleMoment, Formula structure, Component name) {
        this.charge = charge;
        this.mass = mass;
        this.boilingPoint = boilingPoint;
        this.dipoleMoment = dipoleMoment;
        this.structure = structure;
        this.reactantReactions = new ArrayList<>();
        this.productReactions = new ArrayList<>();
        this.name = name;
    };

    public static MoleculeBuilder builder() {
        return new MoleculeBuilder();
    };

    public float getMass() {
        return this.mass;
    };

    public Set<Atom> getAtoms() {
        return structure.getAllAtoms();
    };

    public Map<Element, Integer> getEmpiricalFormula() {
        Map<Element, Integer> empiricalFormula = new HashMap<Element, Integer>();
        for (Atom atom : structure.getAllAtoms()) {
            Element element = atom.getElement();
            if (empiricalFormula.containsKey(element)) {
                int count = empiricalFormula.get(element);
                empiricalFormula.replace(element, count + 1);
            } else {
                empiricalFormula.put(element, 1);
            };
        };
        return empiricalFormula;
    };

    public String getSerlializedEmpiricalFormula() {

        Map<Element, Integer> formulaMap = getEmpiricalFormula();
        List<Element> elements = new ArrayList<>(formulaMap.keySet());
        elements.sort(Comparator.naturalOrder()); //sort Elements based on their order of declaration

        String formula = "";
        for (Element element : elements) {
            formula += element.getSymbol() + formulaMap.get(element).toString();
        };
        return formula;
    };

    public void addReactantReaction(Reaction reaction) {
        if (reaction.containsReactant(this)) reactantReactions.add(reaction);
    };

    public void addProductReaction(Reaction reaction) {
        if (reaction.containsProduct(this)) productReactions.add(reaction);
    };

    public List<Reaction> getReactantReactions() {
        return this.reactantReactions;
    };

    public Component getName() {
        return this.name;
    };

    public static class MoleculeBuilder {

        private Boolean hasForcedBoilingPoint = false; //whether this molecule has a custom BP or it should be calculated
        private Boolean hasForcedDipoleMoment = false; //whether this molecule has a forced DM or it should be calculated

        private int charge = 0;
        private int boilingPoint;
        private int dipoleMoment;
        private Formula structure;

        private Component name = Component.empty();

        public MoleculeBuilder() {};

        public MoleculeBuilder structure(Formula structure) {
            this.structure = structure;
            return this;
        };

        public MoleculeBuilder charge(int charge) {
            this.charge = charge;
            return this;
        };

        public MoleculeBuilder boilingPoint(int boilingPoint) {
            this.boilingPoint = boilingPoint;
            hasForcedBoilingPoint = true;
            return this;
        };

        public MoleculeBuilder dipoleMoment(int dipoleMoment) {
            this.dipoleMoment = dipoleMoment;
            hasForcedDipoleMoment = true;
            return this;
        };

        public MoleculeBuilder translationKey(String translationKey) {
            this.name = DestroyLang.translate("chemical."+translationKey).component();
            return this;
        };

        public MoleculeBuilder name(Component name) {
            this.name = name;
            return this;
        };

        public Molecule build() {
            
            return new Molecule(
                charge,
                calculateMass(), //calculate mass
                hasForcedBoilingPoint ? boilingPoint : calculateBoilingPoint(), //set the BP if there is one, otherwise calculate one
                hasForcedDipoleMoment ? dipoleMoment : calculateDipoleMoment(), //set the DM if there is one, otherwise calculate one
                structure,
                name
            );
        };

        private float calculateMass() {
            float total = 0f;
            Set<Atom> atoms = structure.getAllAtoms();
            for (Atom atom : atoms) {
                total += atom.getElement().getMass();
            };
            return total;
        };

        private int calculateBoilingPoint() {
            return 0;
        };

        private int calculateDipoleMoment() {
            return 0;
        };
    };

    

};

