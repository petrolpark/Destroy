package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;

/**
 * A Formula is all the {@link Atom Atoms} in a {@link Molecule}, and the {@link Bond Bonds} that those Atoms have to other Atoms - a Molecule's 'structure'.
 * For convinience, the {@link Group functional Groups} present in the Molecule are also stored in its Formula.
 * <p>Formulae are also referred to as "structures" throughout the Destroy JavaDocs.</p>
 */
public class Formula implements Cloneable {

    /**
     * Every {@link Atom} in this Formula, mapped to the {@link Bond Bonds} that Atom has.
     */
    private Map<Atom, List<Bond>> structure;

    /**
     * The starting {@link Atom} is used when constructing a Formula or sub-Formula.
     * When a sub-Formula is added to a Formula, the starting Atom of that sub-Formula is what gets {@link Bond bonded} to the {@link Formula#currentAtom current Atom} of the main Formula.
     * <p>For newly-created Formulae, the current Atom is also set to the starting Atom.
     * @see Formula#addGroup Adding a sub-Formula to a Formula
     */
    private Atom startingAtom;

    /**
     * The current {@link Atom} is used when constructing or modifying a Formula. It has no behavioural purpose.
     * @see Formula#addAtom Adding an Atom to the current Atom
     */
    private Atom currentAtom;

    /**
     * The {@link Group functional Groups} this Formula contains.
     * Each Group is specific to a set of {@link Atom Atoms}, so for each instance of the same type of Group there is a separate entry.
     */
    private List<Group> groups;
    
    /**
     * @deprecated The Cycle Type enum will be replaced with a class.
     */
    @Deprecated
    private CycleType cycleType;
    private List<Atom> cyclicAtoms;

    /**
     * The <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code representing this Formula.
     * This is used by novel {@link Molecule Molecules} as their {@link Molecule#getFullID ID}.
     * <p>A given Formula should always {@link Formula#serialize serialize} to the same FROWNS code (hence the moniker "optimum"), so that FROWNS codes can be used to {@link Molecule#getEquivalent compare} Formulae.
     * This is stored so Formulae only have to calculate their FROWNS code once (as this is a resource-intensive process), and can be referred to later.</p>
     */
    @Nullable
    private String optimumFROWNSCode;

    private Formula() {
        structure = new HashMap<Atom, List<Bond>>();
        cycleType = CycleType.NONE; //TODO replace
        optimumFROWNSCode = null;
    };

    /**
     * A set of {@link Atom Atoms} and the {@link Bond Bonds} that connect those Atoms.
     * @param startingAtom The Atom from which to start constructing this Formula.
     */
    public Formula(Atom startingAtom) {
        structure = new HashMap<Atom, List<Bond>>();
        structure.put(startingAtom, new ArrayList<Bond>());
        this.startingAtom = startingAtom;
        this.currentAtom = startingAtom;

        // TODO replace
        this.cycleType = CycleType.NONE;
        this.cyclicAtoms = new ArrayList<Atom>();
    };

    /**
     * An empty Formula.
     * @return A new Formula instance with no {@link Atom Atoms}
     */
    private static Formula nothing() {
        return new Formula();
    };

    /**
     * Moves the {@link Formula#currentAtom currently selected} {@link Atom Atom} to the one given.
     * @param atom If this does not exist in the Formula, the current Atom is not changed
     * @return This Formula
     */
    public Formula moveTo(Atom atom) {
        if (structure.containsKey(atom)) {
            currentAtom = atom;
        };
        return this;
    };

    /**
     * Generates an {@link Atom} of the {@link Element} as the starting point for a Formula.
     * @param element
     * @return A new Formula instance
     */
    public static Formula atom(Element element) {
        return new Formula(new Atom(element));
    };

    /**
     * A straight chain of carbon {@link Atom Atoms} with no hydrogens.
     * @param length
     * @return A new Formula instance with the {@link Formula#startingAtom starting Atom} at one end.
     */
    public static Formula carbonChain(int length) {
        Formula carbonChain = Formula.atom(Element.CARBON);
        for (int i = 0; i < length - 1; i++) {
            carbonChain.addGroup(Formula.atom(Element.CARBON));
        };
        return carbonChain;
    };

    /**
     * An -OH group.
     * @return A new Formula instance, with the oxygen as the {@link Formula#startingAtom starting} {@link Atom}.
     */
    public static Formula alcohol() {
        return Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN);
    };

    /**
     * An acidic -OH group.
     * @param pKa The {@link Atom#getpKa pKa} of the proton
     * @return A new Formula instance, with the oxygen as the {@link Formula#startingAtom starting} {@link Atom}.
     */
    public static Formula acidicAlcohol(float pKa) {
        return Formula.atom(Element.OXYGEN).addAcidicProton(pKa);
    };

    /**
     * An anthracene molecule, without any hydrogens.
     * @param isDihydro Whether this is 9,10-dihyrdoanthracene rather than anthracene.
     */
    @Deprecated
    public static Formula anthracene(boolean isDihydro) {
        //Atom 1
        Atom atom1 = new Atom(Element.CARBON);
        Formula dihydroanthracene = new Formula(atom1);
        dihydroanthracene.cyclicAtoms.add(atom1);

        //Atoms 2, 3, 4
        for (int i = 1; i <= 3; i++) {
            Atom atom = new Atom(Element.CARBON);
            dihydroanthracene.cyclicAtoms.add(atom);
            addAtomToStructure(dihydroanthracene.structure, dihydroanthracene.cyclicAtoms.get(i-1), atom, BondType.AROMATIC);
        };

        //Atom 4a
        Atom atom4a = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure, dihydroanthracene.cyclicAtoms.get(3), atom4a, BondType.AROMATIC);

        //Atom 10
        Atom atom10 = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure, atom4a, atom10, isDihydro ? BondType.SINGLE : BondType.AROMATIC);

        //Atom 10a
        Atom atom10a = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure, atom10, atom10a, isDihydro ? BondType.SINGLE : BondType.AROMATIC);

        //Atom 5
        Atom atom5 = new Atom(Element.CARBON);
        dihydroanthracene.cyclicAtoms.add(atom5);
        addAtomToStructure(dihydroanthracene.structure, atom10a, atom5, BondType.AROMATIC);
        
        //Atoms 6, 7, 8
        for (int i = 6; i <= 8; i++) {
            Atom atom = new Atom(Element.CARBON);
            dihydroanthracene.cyclicAtoms.add(atom);
            addAtomToStructure(dihydroanthracene.structure, dihydroanthracene.cyclicAtoms.get(i-1), atom, BondType.AROMATIC);
        };

        //Atom 8a
        Atom atom8a = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure,  dihydroanthracene.cyclicAtoms.get(7), atom8a, BondType.AROMATIC);

        //Atom 9
        Atom atom9 = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure, atom8a, atom9, isDihydro ? BondType.SINGLE : BondType.AROMATIC);
        dihydroanthracene.cyclicAtoms.add(atom9);

        //Atom 9a
        Atom atom9a = new Atom(Element.CARBON);
        addAtomToStructure(dihydroanthracene.structure, atom9, atom9a, isDihydro ? BondType.SINGLE : BondType.AROMATIC);

        //Atom 10 again
        dihydroanthracene.cyclicAtoms.add(atom10);

        //Remaining bonds
        addBondBetweenAtoms(dihydroanthracene.structure, atom9a, atom1, BondType.AROMATIC);
        addBondBetweenAtoms(dihydroanthracene.structure, atom4a, atom9a, BondType.AROMATIC);
        addBondBetweenAtoms(dihydroanthracene.structure, atom8a, atom10a, BondType.AROMATIC);

        dihydroanthracene.cycleType = isDihydro ? CycleType.DIHYDROANTHRACENE : CycleType.ANTHRACENE;

        return dihydroanthracene;

    };

    /**
     * A benzene ring (without hydrogens).
     */
    @Deprecated
    public static Formula benzene() {
        Atom firstAtom = new Atom(Element.CARBON);
        Formula benzeneRing = new Formula(firstAtom);
        benzeneRing.cyclicAtoms.add(firstAtom);

        for (int i = 1; i < 6; i++) {
            Atom atom = new Atom(Element.CARBON);
            benzeneRing.cyclicAtoms.add(atom);
            addAtomToStructure(benzeneRing.structure, benzeneRing.cyclicAtoms.get(i-1), atom, BondType.AROMATIC); //bond each Carbon to the previous
        };

        addBondBetweenAtoms(benzeneRing.structure, firstAtom, benzeneRing.cyclicAtoms.get(5), BondType.AROMATIC); //bond the sixth Carbon to the first
        benzeneRing.cycleType = CycleType.BENZENE;

        return benzeneRing;
    };

    /**
     * A cyclohexene ring (without hydrogens).
     */
    @Deprecated
    public static Formula cyclohexene() { //TODO account for the fact that some carbons can have more than one single bond on them
        Atom firstAtom = new Atom(Element.CARBON);
        Formula cyclohexene = new Formula(firstAtom);
        cyclohexene.cyclicAtoms.add(firstAtom);

        for (int i = 1; i < 6; i++) {
            Atom atom = new Atom(Element.CARBON);
            cyclohexene.cyclicAtoms.add(atom);
            addAtomToStructure(cyclohexene.structure, cyclohexene.cyclicAtoms.get(i-1), atom, BondType.SINGLE);
        };

        addBondBetweenAtoms(cyclohexene.structure, firstAtom, cyclohexene.cyclicAtoms.get(5), BondType.DOUBLE);
        cyclohexene.cycleType = CycleType.CYCLOHEXENE;

        return cyclohexene;
    };

    public static Formula cyclopentadienyl() {
        Atom firstAtom = new Atom(Element.CARBON);
        Formula cyclopentadienyl = new Formula(firstAtom);
        cyclopentadienyl.cyclicAtoms.add(firstAtom);

        for (int i = 1; i < 5; i++) {
            Atom atom = new Atom(Element.CARBON);
            cyclopentadienyl.cyclicAtoms.add(atom);
            addAtomToStructure(cyclopentadienyl.structure, cyclopentadienyl.cyclicAtoms.get(i-1), atom, BondType.AROMATIC);
        };

        addBondBetweenAtoms(cyclopentadienyl.structure, firstAtom, cyclopentadienyl.cyclicAtoms.get(4), BondType.AROMATIC);
        cyclopentadienyl.cycleType = CycleType.CYCLOPENTADIENYL;

        return cyclopentadienyl;
    };

    /**
     * Adds a singly-{@link Bond bonded} {@link Atom} of an {@link Element} onto the {@link Formula#currentAtom current Atom}, staying on the current Atom.
     * @param element The Element of the Atom to be added
     * @return This Formula
     * @see Formula#addAtom(Element, BondType) Adding a non-single Bond
     */
    public Formula addAtom(Element element) {
        return addAtom(element, BondType.SINGLE);
    };

    /**
     * Adds an {@link Atom} of an {@link Element} onto the {@link Formula#currentAtom current Atom}, staying on the current Atom.
     * @param element The Element of the Atom to generate to add
     * @param bondType Defaults to a single {@link Bond} if not supplied
     * @return This Formula
     */
    public Formula addAtom(Element element, BondType bondType) {
        addAtomToStructure(structure, currentAtom, new Atom(element), bondType);
        return this;
    };

    /**
     * Adds a singly-{@link Bond bonded} {@link Atom} onto the {@link Formula#currentAtom current Atom}, staying on the current Atom.
     * @param atom The Atom to be added.
     * @return This Formula
     * @see Formula#addAtom(Atom, BondType) Adding a non-single Bond
     */
    public Formula addAtom(Atom atom) {
        return addAtom(atom, BondType.SINGLE);
    };

    /**
     * Adds an {@link Atom} onto the {@link Formula#currentAtom current Atom}, staying on the current Atom.
     * @param atom The Atom to add
     * @param bondType The {@link BondType type of Bond} the new Atom should have to the current Atom - defaults to a single {@link Bond} if not supplied
     * @return This Formula
     */
    public Formula addAtom(Atom atom, BondType bondType) {
        addAtomToStructure(structure, currentAtom, atom, bondType);
        return this;
    };

    /**
     * Adds an acidic proton to the {@link Formula#currentAtom current Atom}, staying on that {@link Atom}.
     * @param pKa The {@link Atom#getpKa pKa} of the proton to add.
     * @return This Formula
     */
    public Formula addAcidicProton(float pKa) {
        return addAtom((new Atom(Element.HYDROGEN)).setpKa(pKa));
    };

    /**
     * Adds a singly-{@link Bond bonded} sub-Formula to the {@link Formula#currentAtom current Atom}, staying on that {@link Atom}.
     * @param group The sub-Formula to add
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     * @see Formula#addGroup(Formula, Boolean) Moving to the sub-Formula
     * @see Formula#addGroup(Formula, Boolean, BondType) Adding a non-single Bond
     */
    public Formula addGroup(Formula group) {
        return this.addGroup(group, false);
    };

    /**
     * Adds a singly-{@link Bond bonded} sub-Formula to the {@link Formula#currentAtom current} {@link Atom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * or move to the current Atom of the sub-Formula (defaults to false if not supplied)
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     * @see Formula#addGroup(Formula, Boolean, BondType) Adding a non-single Bond
     */
    public Formula addGroup(Formula group, Boolean isSideGroup) {
        return this.addGroup(group, isSideGroup, BondType.SINGLE);
    };

    /**
     * Adds a sub-Formula to the {@link Formula#currentAtom current} {@link Atom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * or move to the current Atom of the sub-Formula (defaults to false if not supplied)
     * @param bondType The {@link BondType type of Bond} the sub-Formula should have to the current Atom - defaults to a single {@link Bond} if not supplied
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     */
    public Formula addGroup(Formula group, Boolean isSideGroup, BondType bondType) {
        addGroupToStructure(structure, currentAtom, group, bondType);
        if (!isSideGroup) currentAtom = group.currentAtom;
        return this;
    };

    /**
     * Adds a singly-bonded group to an Atom in a Cyclic Molecule, and moves to the end of that group.
     * @param group
     * @param position Which positions correspond to which Atoms can be found <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">here</a>.
     */
    @Deprecated
    public Formula addGroupToPosition(Formula group, int position) {
        return addGroupToPosition(group, position, BondType.SINGLE);
    };

    /**
     * Adds a group to an Atom in a Cyclic Molecule, and moves to the end of that group.
     * @param group
     * @param position Which positions correspond to which Atoms can be found <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">here</a>.
     * @param bondType Defaults to SINGLE.
     */
    @Deprecated
    public Formula addGroupToPosition(Formula group, int position, BondType bondType) {
        addGroupToStructure(structure, cyclicAtoms.get(position), group, bondType);
        currentAtom = group.currentAtom;
        return this;
    };

    /**
     * Removes the given Atom, without moving the currently selected Atom.
     * @param atom If this is the currently selected Atom, an error will be raised.
     */
    public Formula remove(Atom atom) {

        if (!structure.containsKey(atom)) {
            throw new IllegalStateException("Cannot remove "+atom.getElement().getSymbol()+" atom (does not exist).");
        };
        if (atom == currentAtom) {
            throw new IllegalStateException("Cannot remove the currently selected Atom from a structure being built.");
        };
        
        for (Bond bondToOtherAtom : structure.get(atom)) {
            structure.get(bondToOtherAtom.getDestinationAtom()).removeIf(bondToThisAtom -> {
                return bondToThisAtom.getDestinationAtom() == atom;
            });
        };
        structure.remove(atom);
        return this;
    };

    /**
     * Adds an =O Group to the {@link Formula#currentAtom current Atom}, staying on the {@link Atom}.
     * @return This Formula
     */
    public Formula addCarbonyl() {
        addAtom(Element.OXYGEN, BondType.DOUBLE);
        return this;
    };

    /**
     * Adds hydrogen {@link Atom Atoms} to all Atoms which should have more if in a normal valency (as defined by their {@link Element}).
     * If an Element has multiple normal valencies, the next-lowest-possible is used.
     * <p>For example, this would turn {@code CO} into {@code CH₃OH}.
     * For something like sulfur with many normal valencies, {@code S} would turn into {@code H₂S} but {@code SH₃} into {@code SH₄}.
     * As you can see, this method does not produce chemically valid {@link Molecule Molecules}, so should generally be avoided for weird and inorganic species.</p>
     * @return This Formula
     */
    public Formula addAllHydrogens() {
        Map<Atom, List<Bond>> newStructure = new HashMap<Atom, List<Bond>>(structure); //creates a shallow copy, as the original structure can't be modified while being iterated over

        for (Entry<Atom, List<Bond>> entry : structure.entrySet()) {
            Atom atom = entry.getKey();
            List<Bond> bonds = entry.getValue();
            int totalBonds = getTotalBonds(bonds);
            //if (totalBonds > atom.getElement().getMaxValency()) throw new IllegalStateException(atom.getElement()+" Atom has invalid number of bonds");
            for (int i = 0; i < atom.getElement().getNextLowestValency(totalBonds) - totalBonds; i++) {
                addAtomToStructure(newStructure, atom, new Atom(Element.HYDROGEN), BondType.SINGLE); //I think this works 'cause it's a shallow copy and not a deep copy?
            };
        };
        structure = newStructure;
        return this;
    };

    /**
     * The Set of every {@link Atom} in this Formula - essentially its chemical formula.
     */
    public Set<Atom> getAllAtoms() {
        return structure.keySet();
    };

    /**
     * The total {@link Bond.BondType#getEquivalent single-bond equivalents} of a List of {@link Bond Bonds}.
     * <p>Note that this returns an integer - it would be possible for an {@link Atom} to have a non-integeric sum,
     * but this method is used for automatically constructing organic {@link Molecule Molecules} and their
     * {@link GenericReaction Reactions} so shouldn't be used for weirdly behaving inorganic things.
     * @param bonds
     */
    public int getTotalBonds(List<Bond> bonds) {
        float total = 0;
        for (Bond bond : bonds) {
            total += bond.getType().getEquivalent();
        };
        return (int)total;
    };

    /**
     * Get all the {@link Group functional Groups} in this Formula.
     * @see Formula#groups Groups stored in Formulae
     */
    public List<Group> getFunctionalGroups() {
        return groups;
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula or Group, with the given {@link Atom Atom} as the first character.
     * @param atom
     */
    public String serializeStartingWithAtom(Atom atom) {

        Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);

        String body = "";
        String prefix = cycleType.getName();
        if (cycleType == CycleType.NONE) {
            body = getMaximumBranch(atom, newStructure).serialize();
        };

        return prefix+":"+body;
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula.
     */
    public String serialize() {

        if (optimumFROWNSCode != null) { //in case this has already been serialized, we don't want to calculate it again
            return optimumFROWNSCode;
        };

        String body = "";
        String prefix = cycleType.getName();

        Map<Atom, List<Bond>> newStructure = stripHydrogens(shallowCopyStructure(structure));

        if (cycleType == CycleType.NONE) {

            //TODO fix

            // Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);

            // List<Atom> terminalAtoms = new ArrayList<>();
            // for (Atom atom : newStructure.keySet()) {
            //     if (newStructure.get(atom).size() == 1) {
            //         terminalAtoms.add(atom);
            //     };
            // };

            // Collections.sort(terminalAtoms, (a1, a2) -> {
            //     return a2.getElement().getMass().compareTo(a1.getElement().getMass());
            // });
            // Collections.sort(terminalAtoms, (a1, a2) -> {
            //     return getMaximumBranch(a2, newStructure).getMassOfLongestChain().compareTo(getMaximumBranch(a1, newStructure).getMassOfLongestChain()); //put in descending order of chain length
            // });

            // body = getMaximumBranch(terminalAtoms.get(0), newStructure).serialize();

            body = serializeStartingWithAtom(startingAtom);

        } else {
            for (int i = 0; i < cycleType.getPositions(); i++) {
                body += ",";
            };
            body = body.substring(0, body.length() - 1);
        };


        optimumFROWNSCode = prefix + ":" + body;
        return optimumFROWNSCode;

    };

    /**
     * Creates a Formula from a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * @param FROWNSstring
     * @return A new Formula instance
     */
    public static Formula deserialize(String FROWNSstring) {

        try {
            Formula formula;

            String[] cycleTypeAndFormula = FROWNSstring.strip().split(":");
            CycleType cycleType = CycleType.getByName(cycleTypeAndFormula[0]);
            String formulaString = cycleTypeAndFormula[1];

            if (cycleType == CycleType.NONE) {
                List<String> symbols = Arrays.stream(formulaString.split("(?=\\p{Upper})")).toList(); //split String into sub-strings that start with a capital letter (i.e. Elements)
                formula = groupFromString(symbols);
            } else {
                formula = cycleType.create();
                int i = 0;
                for (String group : formulaString.split(",")) {
                    if (group.isBlank()) continue;
                    Boolean stripBond = true; //start by assuming by a =/#/~ will have to be taken off the start of the group
                    BondType bond = BondType.SINGLE; //start by assuming the Bond will be single
                    switch (group.charAt(0)) {
                        case '=':
                            bond = BondType.DOUBLE;
                            break;
                        case '#':
                            bond = BondType.TRIPLE;
                            break;
                        case '~':
                            bond = BondType.AROMATIC;
                            break;
                        default: //if there is no =/#/~, then don't actually take anything off
                            stripBond = false;
                    };
                    if (stripBond) group = group.substring(1);
                    formula.addGroupToPosition(groupFromString(Arrays.stream(group.split("(?=\\p{Upper})")).toList()), i, bond);
                    i++;
                    if (i >= formula.cyclicAtoms.size()) throw new IllegalStateException("Formula '" + FROWNSstring + "' has too many groups (" + i + ") for its Cycle Type (" + formula.cyclicAtoms.size() + ").");
                };
            };

            return formula.addAllHydrogens().refreshFunctionalGroups();

        } catch(Exception e) {
            throw new Error("Could not parse FROWNS String '" + FROWNSstring + "': " + e);
        }
    };

    /**
     * Checks this structure for any {@link Group functional Groups} it contains and updates the {@link Formula#groups stored Groups}.
     * @return This Formula
     */
    public Formula refreshFunctionalGroups() {
        this.groups = new ArrayList<>();
        for (GroupFinder finder : GroupFinder.allGroupFinders()) {
            groups.addAll(finder.findGroups(structure));
        };
        return this;
    };

    /**
     * Creates a shallow copy of this Formula. The clone Formula contains the exact same {@link Atom Atom} and {@link Bond Bond} objects,
     * meaning an Atom in this Formula can be referenced in the clone Formula in the following ways:<ul>
     * <li>{@link Formula#moveTo Moving to an Atom}</li>
     * <li>{@link Formula#remove Removing an Atom}</li>
     * </ul>This is useful for generating specific {@link Reaction Reactions} from {@link GenericReaction Generic Reactions}.
     * @return A new Formula instance
     */
    public Formula shallowCopy() {
        try {

            Formula newFormula = (Formula) super.clone();
            newFormula.structure = new HashMap<>();

            newFormula.structure = shallowCopyStructure(structure); //shallow copy the Structure

            newFormula.groups = new ArrayList<>(groups); //shallow copy the Groups

            newFormula.optimumFROWNSCode = null; //delete the FROWNS Code, as copies are typically going to be modified

            return newFormula;

        } catch(CloneNotSupportedException e) {
            throw new Error(e);
        }
    };

    /**
     * @param carbon
     * @param isCarbanion
     * @see Molecule#getCarbocationStability The wrapper for this Method
     */
    public Float getCarbocationStability(Atom carbon, boolean isCarbanion) {
        Float totalElectronegativity = 0f;
        for (Bond bond : structure.get(carbon)) {
            totalElectronegativity += bond.getDestinationAtom().getElement().getElectronegativity() * bond.getType().getEquivalent();
        };
        Float relativeElectronegativity = totalElectronegativity - (Element.CARBON.getElectronegativity() * 4);
        Float relativeStability = 1f + ((float)Math.pow(relativeElectronegativity, 4) / (float)Math.abs(relativeElectronegativity));
        return isCarbanion ^ relativeElectronegativity < 0 ? 1f / (Float)relativeStability : (Float)relativeStability;
    };

    //INTERNAL METHODS

    /**
     * @param structureToCopy
     * @see Formula#shallowCopy The wrapper for this Method
     */
    private static Map<Atom, List<Bond>> shallowCopyStructure(Map<Atom, List<Bond>> structureToCopy) {
        Map<Atom, List<Bond>> newStructure = new HashMap<>();
        for (Atom atom : structureToCopy.keySet()) {
            newStructure.put(atom, new ArrayList<>(structureToCopy.get(atom)));
        };
        return newStructure;
    };

    /**
     * Get the biggest directed {@link Branch} of {@link Node Nodes} generated from the given structure, starting from the given {@link Atom}.
     * @param startAtom Should not be an {@link Atom#isAcidicProton() acidic proton}
     * @param structure The {@link Formula#structure structure} for which to find the branch
     */
    private static Branch getMaximumBranch(Atom startAtom, Map<Atom, List<Bond>> structure) {

        Map<Atom, Node> allNodes = new HashMap<>();

        for (Atom atom : structure.keySet()) {
            allNodes.put(atom, new Node(atom));
        };

        Node currentNode = allNodes.get(startAtom);
        currentNode.visited = true;

        Branch maximumBranch = new Branch(currentNode);

        Boolean nodesAdded = true;
        while (nodesAdded) {
            nodesAdded = false;
            Map<Node, BondType> connectedUnvisitedNodesAndTheirBondTypes = new HashMap<>();
            for (Bond bond : structure.get(currentNode.getAtom())) {
                Node connectedNode = allNodes.get(bond.getDestinationAtom());
                if (!connectedNode.visited) {
                    connectedUnvisitedNodesAndTheirBondTypes.put(connectedNode, bond.getType());
                };
            };

            if (connectedUnvisitedNodesAndTheirBondTypes.size() == 1) {
                Node onlyNode = connectedUnvisitedNodesAndTheirBondTypes.keySet().iterator().next();
                maximumBranch.add(onlyNode, connectedUnvisitedNodesAndTheirBondTypes.get(onlyNode));
                currentNode = onlyNode;
                nodesAdded = true;

            } else if (connectedUnvisitedNodesAndTheirBondTypes.size() != 0) {

                Map<Branch, BondType> connectedBranchesAndTheirBondTypes = new HashMap<>();
                for (Node node : connectedUnvisitedNodesAndTheirBondTypes.keySet()) {

                    Map<Atom, List<Bond>> newStructure = shallowCopyStructure(structure); //create a new Structure which does not include the current Node
                    Bond bondToRemove = null;
                    for (Bond bond : structure.get(node.getAtom())) {
                        if (bond.getDestinationAtom() == currentNode.getAtom()) {
                            bondToRemove = bond;
                        };
                    };

                    if (bondToRemove != null) {
                        newStructure.get(node.getAtom()).remove(bondToRemove);
                    };

                    newStructure.remove(currentNode.getAtom());
                    
                    Branch branch = getMaximumBranch(node.getAtom(), newStructure);
                    connectedBranchesAndTheirBondTypes.put(branch, connectedUnvisitedNodesAndTheirBondTypes.get(node));
                };

                List<Branch> orderedConnectedBranches = new ArrayList<>(connectedBranchesAndTheirBondTypes.keySet());
                Collections.sort(orderedConnectedBranches, (b1, b2) -> {
                    return b2.getMass().compareTo(b1.getMass());
                });

                Branch biggestBranch = orderedConnectedBranches.get(0);
                maximumBranch.add(biggestBranch, connectedBranchesAndTheirBondTypes.get(biggestBranch));

                orderedConnectedBranches.remove(0); //remove the biggest Branch

                for (Branch sideBranch : orderedConnectedBranches) {
                    currentNode.addSideBranch(sideBranch, connectedBranchesAndTheirBondTypes.get(sideBranch));
                };

            } else {

            };
        };

        return maximumBranch;
        
    };

    /**
     * The internal method for adding an {@link Atom} to a {@link Formula#structure structure}. This is mutative.
     * @param structureToMutate The structure to which to add the Atom
     * @param rootAtom The Atom to which the new Atom will be {@link Bond bonded}
     * @param newAtom The Atom to add
     * @param bondType The {@link Bond.BondType type of Bond} between the new and root Atoms
     * @return The original structure, now with the new Atom
     * @see Formula#addAtom(Atom, BondType) The wrapper for this method
     */
    private static Map<Atom, List<Bond>> addAtomToStructure(Map<Atom, List<Bond>> structureToMutate, Atom rootAtom, Atom newAtom, BondType bondType) {
        structureToMutate.put(newAtom, new ArrayList<>());
        addBondBetweenAtoms(structureToMutate, rootAtom, newAtom, bondType);
        return structureToMutate;
    };

    /**
     * The internal method for adding an sub-Formula to a {@link Formula#structure structure}. This is mutative.
     * @param structureToMutate The structure to which to add the {@link Atom}
     * @param rootAtom The Atom to which the new sub-Formula will be {@link Bond bonded}
     * @param group The sub-Formula to add
     * @param bondType The {@link Bond.BondType type of Bond} between the {@link Formula#startingAtom starting Atom} of the sub-Formula and the root Atom
     * @return The original structure, now with the new sub-Formula
     * @see Formula#addGroup(Formula, Boolean, BondType) The wrapper for this method
     */
    private static void addGroupToStructure(Map<Atom, List<Bond>> structureToMutate, Atom rootAtom, Formula group, BondType bondType) {
        if (group.cycleType != CycleType.NONE) { //TODO replace
            Destroy.LOGGER.warn("Cannot add Cycles as side-groups - to create a Cyclic Molecule, start with the Cycle and use addGroupAtPosition()");
        };
        structureToMutate.putAll(group.structure);
        addBondBetweenAtoms(structureToMutate, rootAtom, group.startingAtom, bondType);
    };

    /**
     * Creates a {@link Bond} and its {@link Bond#getMirror mirror} between two {@link Atom Atoms}.
     * @param structureToMutate The {@link Formula#structure structure in which} both these Atoms are
     * @param atom1 If not in the structure, will cause a runtime error
     * @param atom2 If not in the structure, will cause a runtime error
     * @param type The {@link Bond.BondType type of Bond} between the two Atoms
     */
    private static void addBondBetweenAtoms(Map<Atom, List<Bond>> structureToMutate, Atom atom1, Atom atom2, BondType type) {
        Bond bond = new Bond(atom1, atom2, type);
        structureToMutate.get(atom1).add(bond);
        structureToMutate.get(atom2).add(bond.getMirror());
    };

    /**
     * Recursively {@link Formula#serialize serializes} a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * @param symbols A FROWNS code split into individual Strings, each String containing a single {@link Atom}
     * @return A new Formula instance represented by the given FROWNS code
     */
    private static Formula groupFromString(List<String> symbols) {

        Formula formula = Formula.nothing();
        Boolean hasFormulaBeenInstantiated = false;

        BondType nextAtomBond = BondType.SINGLE;

        int i = 0;
        while (i < symbols.size()) {

            String symbol;
            Map<Formula, BondType> groupsToAdd = new HashMap<>(); //a list of all the Groups to be added, and the Type of Bond by which they should be added
            BondType thisAtomBond = nextAtomBond;

            if (symbols.get(i).contains("(")) { //if this Atom marks the beginning of a side Group (i.e. the next Atom will comprise the start of the side Group)

                BondType groupBond = trailingBondType(symbols.get(i)); //get the Bond Type to the Group

                symbol = symbols.get(i).substring(0, symbols.get(i).indexOf('(')); //Strip off the '(' and everything after it

                int brackets = 1;
                List<String> subSymbols = new ArrayList<>();
                while (brackets > 0) { //Keep going until the closing bracket is found
                    i++; //move to next Atom
                    Boolean added = false;
                    for (int j = 0; j < symbols.get(i).length(); j++) {
                        char c = symbols.get(i).charAt(j);
                        if (c == ')') {
                            brackets--;
                        } else if (c == '(') {
                            brackets++;
                        };
                        if (brackets == 0) { //if this is the last Atom in the side Group
                            subSymbols.add(symbols.get(i).substring(0, j)); //add this Atom, but not the closing bracket or anything afterwards
                            groupsToAdd.put(groupFromString(subSymbols), groupBond); //as this is the final Atom in the side Group, mark the Group to be added
                            subSymbols = new ArrayList<>(); //empty the list of Atoms in the side Group (as a new side Group may be about to start)
                            groupBond = trailingBondType(symbols.get(i)); //check what the Bond will be if there is to be another Group
                            added = true;
                        };
                    };
                    if (!added) {
                        subSymbols.add(symbols.get(i)); //if this was not the final Atom in the side Group, add it to the Group
                    };
                };

            } else { //if this was not an Atom in a side Group
                symbol = symbols.get(i);
            };

            Boolean stripBond = true; //start by assuming by a =/#/~ will have to be taken off the end of the Symbol
            nextAtomBond = BondType.SINGLE; //start by assuming the next Bond will be single
            switch (symbol.charAt(symbol.length() - 1)) {
                case '=':
                    nextAtomBond = BondType.DOUBLE;
                    break;
                case '#':
                    nextAtomBond = BondType.TRIPLE;
                    break;
                case '~':
                    nextAtomBond = BondType.AROMATIC;
                    break;
                default: //if there is no =/#/~, then don't actually take anything off
                    stripBond = false;
            };
            if (stripBond) symbol = symbol.substring(0, symbol.length() - 1);

            if (symbol.startsWith("H+")) { //if this is an acidic proton
                formula.addAcidicProton(Float.valueOf(symbol.substring(2)));
            } else { //if this is not an acidic proton (i.e. a normal Atom)
                Element element = Element.fromSymbol(symbol);

                if (hasFormulaBeenInstantiated) { //if this is not the first Atom
                    formula.addGroup(Formula.atom(element), false, thisAtomBond);
                } else {
                    formula = Formula.atom(element);
                    hasFormulaBeenInstantiated  = true;
                };
            };

            for (Formula group : groupsToAdd.keySet()) { //add all side Groups to the current Atom
                formula.addGroup(group, true, groupsToAdd.get(group));
            };

            i++; //move to the next Atom
        };

        return formula;
    };

    /**
     * The {@link Bond.BondType type of Bond} after an {@link Element} symbol in a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * @param symbol A String which is part of a FROWNS Code, consisting of a single {@link Atom}
     */
    private static BondType trailingBondType(String symbol) {
        return BondType.fromFROWNSCode(symbol.charAt(symbol.length() - 1));
    };

    /**
     * Removes all non-{@link Atom#isAcidicProton acidic} hydrogen {@link Atom Atoms} from a {@link Formula#startingAtom structure}. This is mutative.
     * @param structure The structure from which to remove the hydrogen Atoms
     * @return The original structure, now with its non-acidic hydrogen Atoms removed
     */
    private static Map<Atom, List<Bond>> stripHydrogens(Map<Atom, List<Bond>> structure) {
        Map<Atom, List<Bond>> newStructure = new HashMap<>();
        for (Atom atom : structure.keySet()) {
            List<Bond> bondsToAdd = new ArrayList<>();
            for (Bond bond : structure.get(atom)) {
                if (!bond.getDestinationAtom().isNonAcidicHydrogen()) {
                    bondsToAdd.add(bond);
                };
            };
            if (!atom.isNonAcidicHydrogen()) {
                newStructure.put(atom, bondsToAdd);
            };
        };
        return newStructure;
    };
    
    @Deprecated
    private enum CycleType {

        NONE("linear", 0, Formula::nothing),
        ANTHRACENE("anthracene", 10, () -> anthracene(false)),
        BENZENE("benzene", 6, Formula::benzene),
        CYCLOHEXENE("cyclohexene", 10, Formula::cyclohexene),
        CYCLOPENTADIENYL("cyclopentadienyl", 5, () -> cyclopentadienyl()),
        DIHYDROANTHRACENE("dihydroanthracene", 10, () -> anthracene(true))
        ;

        private String name; //used for de/serialization
        private Supplier<Formula> constructor;
        private int positions;

        private CycleType(String name, int positions, Supplier<Formula> constructor) {
            this.name = name;
            this.positions = positions;
            this.constructor = constructor;
            Molecule.FORBIDDEN_NAMESPACES.add(name);
        };

        public static CycleType getByName(String name) {
            for (CycleType cycleType : values()) {
                if (cycleType.name.equals(name)) return cycleType;
            };
            throw new Error("Unknown Cycle Type "+name);
        };

        public String getName() {
            return name;
        };

        public int getPositions() {
            return positions;
        };

        public Formula create() {
            return this.constructor.get();
        };
    };

};
