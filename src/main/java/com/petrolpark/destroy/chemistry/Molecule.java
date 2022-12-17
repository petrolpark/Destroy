package com.petrolpark.destroy.chemistry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.network.chat.Component;

public class Molecule {

    public static Map<String, Molecule> MOLECULES = new HashMap<>(); //Map of Molecules stored by their IDs

    private String nameSpace;
    private String id;

    private int charge;
    private float mass;
    private int boilingPoint;
    private int dipoleMoment;
    private Formula structure;

    private Boolean isHypothetical;

    private Boolean isAcutelyToxic;
    private Boolean isCarcinogen;
    private Boolean isChronicallyToxic;
    private Boolean isGreenhouse;
    private Boolean isOzoneDepleter;
    private Boolean isSmelly;
    private Boolean isSmogAmplifier;

    private List<Reaction> reactantReactions; //reactions in which this Molecule is a reactant
    private List<Reaction> productReactions; //reactions in which this Molecule is a product

    private Component displayName;

    private Molecule(String nameSpace) {
        this.nameSpace = nameSpace;
        id = null;
        structure = null;

        isHypothetical = false;

        isAcutelyToxic = false;
        isCarcinogen = false;
        isChronicallyToxic = false;
        isGreenhouse = false;
        isOzoneDepleter = false;
        isSmelly = false;
        isSmogAmplifier = false;

        reactantReactions = new ArrayList<>();
        productReactions = new ArrayList<>();
    };

    /**
     * Get the name used for storing this Molecule in NBT.
     * For known Molecules, this will be of the format [namespace]:[id].
     * For novel Molecules, this will be their <a link href="https://github.com/petrolpark/Destroy/wiki/FROWNS"> FROWNS Code</a>.
     * @return ID or FROWNS Code
     */
    //TODO calculate systematic FROWNS Code
    public String getFullID() {
        if (id == null) {
            return structure.serialize();
        } else {
            return nameSpace+":"+id;
        }
    };

    public Molecule getEquivalent() {
        for (Molecule molecule : MOLECULES.values()) {
            if (this.getMass() == molecule.getMass()) { //initially just check the masses match
                if (this.structure.serialize() == molecule.structure.serialize()) { //check the structures match
                    return molecule;
                };
            };
        };
        return this;
    };

    public float getMass() {
        return this.mass;
    };

    public Set<Atom> getAtoms() {
        return structure.getAllAtoms();
    };

    public Boolean isHypothetical() {
        return isHypothetical;
    };

    /**
     * Gives all Atoms in this Molecule, and their quantities.
     * @return Map of {@link Atom Atoms} to their quantities.
     */
    public Map<Element, Integer> getChemicalFormula() {
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

    /**
     * Gives all Atoms in this Molecule, and their quantities, in the format AaBbCc..., where a = number of Atoms of A, etc.
     * Elements are given in the order in which they are declared in {@link Element the Element Enum}.
     */
    public String getSerlializedChemicalFormula() {

        Map<Element, Integer> formulaMap = getChemicalFormula();
        List<Element> elements = new ArrayList<>(formulaMap.keySet());
        elements.sort(Comparator.naturalOrder()); //sort Elements based on their order of declaration

        String formula = "";
        for (Element element : elements) {
            String number = formulaMap.get(element) == 1 ? "" : formulaMap.get(element).toString(); //if there is only one then don't print the number
            formula += element.getSymbol() + number;
        };
        return formula;
    };

    /**
     * Mark this Molecule as being a necessary reactant in the given Reaction. There should never be any need to call this method (it is done automatically when {@link Reaction.ReactionBuilder#build building} a Reaction).
     * @param reaction
     */
    public void addReactantReaction(Reaction reaction) {
        if (reaction.containsReactant(this)) reactantReactions.add(reaction);
    };

    /**
     * Mark this Molecule as being produced in the given Reaction. There should never be any need to call this method.
     * @param reaction
     */
    public void addProductReaction(Reaction reaction) {
        if (reaction.containsProduct(this)) productReactions.add(reaction);
    };

    /**
     * Get the list of Reactions of which this Molecule is a necessary Reactant.
     * @return List of Reactions ordered by declaration
     */
    public List<Reaction> getReactantReactions() {
        return this.reactantReactions;
    };

    /**
     * Get the display name of this Molecule.
     */
    public Component getName() {
        return this.displayName;
    };

    /**
     * Get all the functional Groups contained by this Molecule.
     * @return
     */
    public List<Group> getFunctionalGroups() {
        return this.structure.getFunctionalGroups();
    };

    public static class MoleculeBuilder {

        private Molecule molecule;

        private Boolean hasForcedBoilingPoint = false; //whether this molecule has a custom BP or it should be calculated
        private Boolean hasForcedDipoleMoment = false; //whether this molecule has a forced DM or it should be calculated

        private Component displayName = Component.empty();

        public MoleculeBuilder(String nameSpace) {
            molecule = new Molecule(nameSpace);
            molecule.charge = 0; //default
        };

        /**
         * The internal ID for this Molecule.
         * By default, the translation key for this Molecule will be set to its ID (change with {@link Molecule.MoleculeBuilder#translationKey translationKey()}).
         * If a Molecule is declared without an ID it will not be added to the Molecule register.
         * @param id Must be unique.
         */
        public MoleculeBuilder id(String id) {
            molecule.id = id;
            translationKey(id);
            return this;
        };

        /**
         * Set the structure of this Molecule.
         * Hydrogens are not added by default; use {@link Formula#addAllHydrogens addAllHydrogens()} for this.
         * @param structure
         */
        public MoleculeBuilder structure(Formula structure) {
            molecule.structure = structure;
            return this;
        };

        /**
         * Set the overall charge of this Molecule.
         * @param charge
         * @return
         */
        public MoleculeBuilder charge(int charge) {
            molecule.charge = charge;
            return this;
        };

        /**
         * Set the boiling point in degrees Celsius.
         * If not supplied, the boiling point will be estimated automatically, but setting one is recommended.
         * @param boilingPoint
         */
        public MoleculeBuilder boilingPoint(int boilingPoint) {
            return boilingPointInKelvins(boilingPoint + 273);
        };

        /**
         * Set the boiling point in Kelvins.
         * @param boilingPoint
         */
        public MoleculeBuilder boilingPointInKelvins(int boilingPoint) {
            molecule.boilingPoint = boilingPoint;
            hasForcedBoilingPoint = true;
            return this;
        };

        /**
         * Set the dipole moment of this Molecule.
         * Dipole moments are used to check solubility and for determining fractions in column chromotography.
         * If not supplied, a dipole moment will be estimated automatically, but setting one is recommended.
         */
        public MoleculeBuilder dipoleMoment(int dipoleMoment) {
            molecule.dipoleMoment = dipoleMoment;
            hasForcedDipoleMoment = true;
            return this;
        };

        /**
         * Default namespace is 'destroy'; use {@link Molecule.MoleculeBuilder#displayName() displayName()} to set a custom translation.
         * By default, this is the same as the name declared in {@link Molecule.MoleculeBuilder#id() id()}. Call this <em>after</em> that method.
         * @param translationKey
         */
        public MoleculeBuilder translationKey(String translationKey) {
            this.displayName = DestroyLang.translate("chemical."+translationKey).component();
            return this;
        };

        /**
         * Mark this Molecule as being hypothetical - if this Molecule appears in a solution, an error will be raised.
         * @return
         */
        public MoleculeBuilder hypothetical() {
            molecule.isHypothetical = true;
            return this;
        };

        /**
         * Mark this Molecule as being acutely toxic (causes immediate damage to Players).
         */
        public MoleculeBuilder acutelyToxic() {
            molecule.isAcutelyToxic = true;
            return this;
        };

        /**
         * Mark this Molecule as being carcinogenic (gradually harms Players).
         */
        public MoleculeBuilder carcinogen() {
            molecule.isCarcinogen = true;
            return this;
        };

        /**
         * Mark this Molecule as being chronically toxic (gradually weathers away Players' health).
         */
        public MoleculeBuilder chronicallyToxic() {
            molecule.isChronicallyToxic = true;
            return this;
        };

        /**
         * Mark this Molecule as one which will increase the World's global warming level when released into the atmosphere.
         * @return
         */
        public MoleculeBuilder greenhouse() {
            molecule.isGreenhouse = true;
            return this;
        };

        /**
         * Mark this Molecule as one which will deplete the World's ozone level when released into the atmosphere.
         */
        public MoleculeBuilder ozoneDepleter() {
            molecule.isOzoneDepleter = true;
            return this;
        };

        /**
         * Mark this Molecule as one which will make the Player nauseated if they are not wearing perfume.
         */
        public MoleculeBuilder smelly() {
            molecule.isSmelly = true;
            return this;
        };

        /**
         * Mark this Molecule as one which will amplify the World's smog level when released into the atmosphere.
         */
        public MoleculeBuilder smogAmplifier() {
            molecule.isSmogAmplifier = true;
            return this;
        };

        /**
         * Set the name shown to the player.
         * @param displayName
         */
        public MoleculeBuilder displayName(Component displayName) {
            this.displayName = displayName;
            return this;
        };

        public Molecule build() {

            molecule.mass = calculateMass();
            molecule.displayName = displayName;

            if (molecule.structure == null) {
                throw new IllegalStateException("Molecule's structure has not been declared");
            };

            if (molecule.getChemicalFormula().containsKey(Element.R_GROUP)) {
                molecule.isHypothetical = true;
            };
            
            if (!hasForcedBoilingPoint) {
                molecule.boilingPoint = calculateBoilingPoint();
            };

            if (!hasForcedDipoleMoment) {
                molecule.dipoleMoment = calculateDipoleMoment();
            };

            molecule.refreshFunctionalGroups();

            if (molecule.id == null) {
                Destroy.LOGGER.warn("Molecule's ID has not been declared");
            } else {
                MOLECULES.put(molecule.id, molecule);
            };

            return molecule;
        };

        private float calculateMass() {
            float total = 0f;
            Set<Atom> atoms = molecule.structure.getAllAtoms();
            for (Atom atom : atoms) {
                total += atom.getElement().getMass();
            };
            return total;
        };

        private int calculateBoilingPoint() {
            //TODO calculate Boiling Point
            return 0;
        };

        private int calculateDipoleMoment() {
            //TODO calculate Dipole Moment
            return 0;
        };
    };

    private void refreshFunctionalGroups() {
        structure.refreshFunctionalGroups();
    };

};

