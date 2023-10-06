package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula.Topology.SideChainInformation;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.world.phys.Vec3;

/**
 * A Formula is all the {@link Atom Atoms} in a {@link Molecule}, and the {@link Bond Bonds} that those Atoms have to other Atoms - a Molecule's 'structure'.
 * For convinience, the {@link Group functional Groups} present in the Molecule are also stored in its Formula.
 * <p>Formulae are also referred to as "structures" throughout the Destroy JavaDocs.</p>
 * <p><b>Formulae must always be {@link Formula#shallowCopy copied} before modifying.</b></p>
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
    private List<Group<?>> groups;
    
    /**
     * The {@link Topology Topology} (3D structure) of this Formula if it is cyclic.
     */
    private Topology topology;

    /**
     * A list of copies of Formulas attached to {@link Formula#topology cyclic} {@link Atom Atoms} in this Formula.
     * This is just used for {@link Molecule#getRenderer rendering} and {@link Formula#serialize serialization} - originals
     * of all Atoms in the side-chains are still stored in this Formula.
     */
    private List<Pair<SideChainInformation, Formula>> sideChains;

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
        groups = new ArrayList<>();
        topology = Topology.LINEAR;
        sideChains = new ArrayList<>();
        optimumFROWNSCode = null;
    };

    /**
     * A set of {@link Atom Atoms} and the {@link Bond Bonds} that connect those Atoms.
     * @param startingAtom The Atom from which to start constructing this Formula.
     */
    public Formula(Atom startingAtom) {
        this();
        structure.put(startingAtom, new ArrayList<Bond>());
        this.startingAtom = startingAtom;
        currentAtom = startingAtom;
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
     * @param atom If this does not exist in the Formula, en error is raised
     * @return This Formula
     */
    public Formula moveTo(Atom atom) {
        if (structure.containsKey(atom)) {
            currentAtom = atom;
        } else {
            throw new IllegalArgumentException("Can't set the current Atom to an Atom not in the Formula");
        };
        return this;
    };

    /**
     * Change the {@link Formula#startingAtom starting} {@link Atom} to the one given.
     * This is useful when {@link Formula#addGroup attaching side-groups}, as they are always
     * joined by the starting Atom of the side group.
     * @param atom If this does not exist in the Formula, an error is raised
     * @return This Formula
     */
    public Formula setStartingAtom(Atom atom) {
        if (structure.containsKey(atom)) {
            startingAtom = atom;
        } else {
            throw new IllegalArgumentException("Can't set the starting Atom to an Atom not in the Formula");
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
     * Adds a singly-{@link Bond bonded} sub-Formula to the {@link Formula#currentAtom current Atom}, staying on that {@link Atom}.
     * @param group The sub-Formula to add
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     * @see Formula#addGroup(Formula, Boolean) Moving to the sub-Formula
     * @see Formula#addGroup(Formula, Boolean, BondType) Adding a non-single Bond
     * @see Formula#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public Formula addGroup(Formula group) {
        return this.addGroup(group, true);
    };

    /**
     * Adds a singly-{@link Bond bonded} sub-Formula to the {@link Formula#currentAtom current} {@link Atom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * or move to the current Atom of the sub-Formula (defaults to {@code true} if not supplied)
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     * @see Formula#addGroup(Formula, Boolean, BondType) Adding a non-single Bond
     * @see Formula#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public Formula addGroup(Formula group, boolean isSideGroup) {
        return this.addGroup(group, isSideGroup, BondType.SINGLE);
    };

    /**
     * Join two Formula between their {@link Formula#currentAtom currently-selected} {@link Atom Atoms} with the given {@link BondType type} of {@link Bond}.
     * <p><strong>This will mutate one or both of the two Formulae. </strong> You should not refer to either of them after they have been joined.
     * Instead, store the return value of this method and use that if it needs further modifying. For example:</p>
     * <p><blockquote><pre>
     * Formula joinedStructure = Formula.joinFormulae(oldFormula1, oldFormula2, BondType.SINGLE)
     * // Now I no longer refer to oldFormula1 or oldFormula2 because I'm such a well-behaved developer
     * </pre></blockquote></p>
     * @param formula1 The current Atom will move to the currently selected Atom of this Formula. Otherwise, the two are indistuinguisable
     * @param formula2
     * @param bondType
     * @return A new Formula which consists of the joined structure of both Formulae
     */
    public static Formula joinFormulae(Formula formula1, Formula formula2, BondType bondType) {
        Formula formula;
        if (formula2.isCyclic()) {
            if (formula1.isCyclic()) throw new IllegalStateException("Cannot join two cyclic structures.");
            formula1.startingAtom = formula1.currentAtom;
            formula2.addGroup(formula1, false, bondType);
            formula = formula2;
        } else {
            formula2.startingAtom = formula2.currentAtom;
            formula1.addGroup(formula2, true, bondType);
            formula = formula1;
        }
        return formula.shallowCopy();
    };

    /**
     * Adds a sub-Formula to the {@link Formula#currentAtom current} {@link Atom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * or move to the current Atom of the sub-Formula (defaults to false if not supplied)
     * @param bondType The {@link BondType type of Bond} the sub-Formula should have to the current Atom - defaults to a single {@link Bond} if not supplied
     * @return This Formula
     * @see Formula#startingAtom How sub-Formulae are added
     * @see Formula#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public Formula addGroup(Formula group, Boolean isSideGroup, BondType bondType) {
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == currentAtom)) {
            throw new IllegalStateException("Cannot modify Atoms in cycle");
        };
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
        addGroupToStructure(structure, sideChains.get(position).getFirst().atom(), group, bondType); // Add a Bond between the cyclic Atom and this Group
        sideChains.get(position).setSecond(group); // Update the group attached to this position
        currentAtom = group.currentAtom;
        return this;
    };

    /**
     * Whether this Formula is cylic (has a defined {@link Topology}), or is linear/branched.
     */
    public boolean isCyclic() {
        return topology != Topology.LINEAR;
    };

    /**
     * Get the list of {@link Topology#atomsAndLocations cyclic Atoms} in the {@link Topology} associated with this Formula.
     * @return May be empty
     * @see Molecule#getCyclicAtomsForRendering The wrapper for this method
     */
    public List<Pair<Vec3, Atom>> getCyclicAtomsForRendering() {
        return topology.atomsAndLocations;
    };

    /**
     * Get the list of {@link Topology#bonds cyclic Bonds} in the {@link Topology} associated with this Formula.
     * @return May be empty
     * @see Molecule#getCyclicBondsForRendering The wrapper for this method
     */
    public List<Bond> getCyclicBondsForRendering() {
        return topology.bonds;
    };

    /**
     * Get the list of {@link Formula#sideChains} side chains off of {@link Formula#topology cyclic} {@link Atom Atoms}
     * in this Formula.
     * @return May be empty
     * @see Molecule#getSideChainsForRendering The wrapper for this method
     */
    public List<Pair<SideChainInformation, Branch>> getSideChainsForRendering() {
        return sideChains.stream().map(pair -> {
            Formula sideChain = pair.getSecond();
            return Pair.of(pair.getFirst(), getMaximumBranch(sideChain.startingAtom, sideChain.structure));
        }).toList();
    };

    /**
     * Removes the given Atom, without moving the currently selected Atom.
     * <p><b>To modify existing Formulae, {@link Formula#shallowCopy copy} them first.</b></p>
     * @param atom If this is the currently selected Atom, an error will be raised.
     */
    public Formula remove(Atom atom) {

        if (!structure.containsKey(atom)) {
            throw new IllegalStateException("Cannot remove "+atom.getElement().getSymbol()+" atom (does not exist).");
        };
        if (atom == currentAtom) {
            throw new IllegalStateException("Cannot remove the currently selected Atom from a structure being built.");
        };
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == atom)) {
            throw new IllegalStateException("Cannot remove Atoms in the cyclic part of a cyclic Molecule ");
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
     * Replaces one {@link Atom} with another, without moving the currently selected Atom.
     * <p><b>To modify existing Formulae, {@link Formula#shallowCopy copy} them first.</b></p>
     * @param oldAtom This must exist in the structure
     * @param newAtom
     */
    public Formula replace(Atom oldAtom, Atom newAtom) {
        if (!structure.containsKey(oldAtom)) {
            throw new IllegalStateException("Cannot replace "+oldAtom.getElement().getSymbol()+" atom (does not exist).");
        };
        if (oldAtom == currentAtom) currentAtom = newAtom;
        if (oldAtom == startingAtom) startingAtom = newAtom;
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == oldAtom)) {
            throw new IllegalStateException("Cannot replace Atoms in the cyclic part of a cyclic Molecule ");
        };

        // Replace all Bonds to the old Atom with Bonds to the new Atom
        for (Bond bondToOtherAtom : structure.get(oldAtom)) {
            structure.get(bondToOtherAtom.getDestinationAtom()).replaceAll(bond -> {
                if (bond.getDestinationAtom() == oldAtom) {
                    return new Bond(bond.getSourceAtom(), newAtom, bond.getType());
                };
                return bond;
            });
        };

        // Replace Bonds from the old Atom with bonds from the new Atom
        List<Bond> oldBonds = structure.get(oldAtom);
        structure.put(newAtom, oldBonds);

        // Remove the old Atom
        structure.remove(oldAtom);

        return this;
    };

    /**
     * Exchange the {@link Bond} between the given {@link Atom} and the {@link Formula#currentAtom current} one.
     * @param otherAtom If there is not an existing Bond to this Atom, an error will be thrown
     * @param bondType
     * @return This Formula
     */
    public Formula replaceBondTo(Atom otherAtom, BondType bondType) {
        for (Bond bond : structure.get(currentAtom)) {
            if (bond.getDestinationAtom() == otherAtom) {
                bond.setType(bondType);
                for (Bond reverseBond : structure.get(otherAtom)) {
                    if (reverseBond.getDestinationAtom() == currentAtom) {
                        bond.setType(bondType);
                        return this;
                    };
                };
            };
        };
        throw new IllegalStateException("Cannot modify bond between two Atoms if they do not already have a Bond");
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
        Map<Atom, List<Bond>> newStructure = new HashMap<Atom, List<Bond>>(structure); // Create a shallow copy, as the original structure can't be modified while being iterated over

        // Replace all empty side chains with Hydrogen, if necessary
        if (topology != Topology.LINEAR) {
            for (int i = 0; i < sideChains.size(); i++) {
                Atom atom = sideChains.get(i).getFirst().atom();
                double totalBonds = getTotalBonds(newStructure.get(atom));
                if (atom.getElement().getNextLowestValency(totalBonds) - totalBonds > 0) {
                    sideChains.get(i).setSecond(Formula.atom(Element.HYDROGEN));
                };
            };
        };

        // Add all the rest of the Hydrogens
        for (Entry<Atom, List<Bond>> entry : structure.entrySet()) {
            Atom atom = entry.getKey();
            List<Bond> bonds = entry.getValue();
            double totalBonds = getTotalBonds(bonds);
            //if (totalBonds > atom.getElement().getMaxValency()) throw new IllegalStateException(atom.getElement()+" Atom has invalid number of bonds");

            for (int i = 0; i < atom.getElement().getNextLowestValency(totalBonds) - totalBonds; i++) {
                Atom hydrogen = new Atom(Element.HYDROGEN);
                addAtomToStructure(newStructure, atom, hydrogen, BondType.SINGLE);
            };
        };
        structure = newStructure;
        return this;
    };

    /**
     * Each {@link Formula#sideChains side chain} entry contains a {@link Formula} which references {@link Atom Atoms} and {@link Bond Bonds} in the
     * main {@link Formula#structure structure}. When new Atoms are added to the main Formula, the Formulae of the side chains need to be updated.
     * <p>I don't know who needs to hear this but for the record this was a really complicated thing I had to do and it worked basically first try. Well done me.</p>
     */
    public void updateSideChainStructures() {
        if (topology == Topology.LINEAR) return;
        List<Pair<SideChainInformation, Formula>> newSideChains = new ArrayList<>();
        for (Pair<SideChainInformation, Formula> sideChain : sideChains) {
            Formula sideChainFormula = sideChain.getSecond();
            SideChainInformation info = sideChain.getFirst();
            Formula newSideChainFormula = sideChainFormula.shallowCopy();
            checkAllAtomsInSideChain: for (Atom atom : sideChainFormula.structure.keySet()) { // For every Atom in the side chain Formula, update it so it has all the same Bonds it has in the main structure
                List<Bond> bonds = new ArrayList<>();
                if (structure.get(atom) == null) continue checkAllAtomsInSideChain; // If this Atom has no Bonds, don't do anything
                addAllBondsForAtom: for (Bond bond : structure.get(atom)) {
                    Atom potentialNewAtom = bond.getDestinationAtom();
                    if (topology.formula.structure.keySet().contains(potentialNewAtom)) continue addAllBondsForAtom; // Don't add Bonds to Atoms which are part of the Topology (and therefore not part of the side branch)
                    if (!sideChainFormula.structure.keySet().contains(potentialNewAtom)) newSideChainFormula.structure.put(potentialNewAtom, structure.get(potentialNewAtom)); // Add any as-of-yet unknown Atoms to the side branch's structure
                    bonds.add(bond);
                };
                newSideChainFormula.structure.put(atom, bonds);
            };
            newSideChains.add(Pair.of(info, newSideChainFormula));
        };
        sideChains = newSideChains;
    };
 
    /**
     * The Set of every {@link Atom} in this Formula - essentially its molecular formula.
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
    public double getTotalBonds(List<Bond> bonds) {
        float total = 0;
        for (Bond bond : bonds) {
            total += bond.getType().getEquivalent();
        };
        return total;
    };

    /**
     * Get all the {@link Group functional Groups} in this Formula.
     * @see Formula#groups Groups stored in Formulae
     */
    public List<Group<?>> getFunctionalGroups() {
        return groups;
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula or Group, with the given {@link Atom Atom} as the first character.
     * The {@link Topology} of this Formula is not included, and in fact this will quietly fail for cyclic {@link Molecule Molecules}.
     * @param atom
     */
    private Branch getStrippedBranchStartingWithAtom(Atom atom) {
        Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);
        if (topology == Topology.LINEAR) {
            return getMaximumBranch(atom, newStructure);
        } else {
            throw new IllegalStateException("Cannot serialize branch if it is cyclic.");
        }
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula.
     * This does not fresly calculate the FROWNS code each time, meaning Formulae should not be modifed without
     * first {@link Formula#shallowCopy copying} them.
     */
    public String serialize() {

        if (optimumFROWNSCode != null) { //in case this has already been serialized, we don't want to calculate it again
            return optimumFROWNSCode;
        };

        String body = "";
        String prefix = topology.getID();

        if (topology == Topology.LINEAR) {

            Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);

            body = getMaximumBranchWithHighestMass(newStructure).serialize();

        } else {
            updateSideChainStructures();
            List<Branch> identity = new ArrayList<>(topology.getConnections());

            for (int i = 0; i < topology.getConnections(); i++) {
                Formula sideChain = sideChains.get(i).getSecond();
                if (sideChain.getAllAtoms().size() == 0 || (sideChain.startingAtom.isHydrogen())) { // If there is nothing or just a hydrogen
                    identity.add(new Branch(new Node(new Atom(Element.HYDROGEN))));
                } else {
                    identity.add(sideChain.getStrippedBranchStartingWithAtom(sideChain.startingAtom));
                };
            };
            
            List<List<Branch>> possibleReflections = new ArrayList<>(topology.getReflections().length + 1);
            possibleReflections.add(identity);

            // Add all possible rearrangements of the same Branches that are still the same isomer
            for (int[] reflectionOrder : topology.getReflections()) {
                List<Branch> reflection = new ArrayList<>(topology.getConnections());
                for (int reflectedBranchPosition : reflectionOrder) {
                    reflection.add(identity.get(reflectedBranchPosition));
                };
                possibleReflections.add(reflection);
            };

            // Sort the possible reflections so the first element is the reflection that gives the highest mass branch in position 0, the next highest in position 1, etc.
            Collections.sort(possibleReflections, (r1, r2) -> getReflectionComparison(r1).compareTo(getReflectionComparison(r2)));

            List<Branch> bestReflection = possibleReflections.get(0);
            for (int i = 0; i < topology.getConnections(); i++) {
                Branch branch = bestReflection.get(i);
                if (!branch.getStartNode().getAtom().isHydrogen()) { // If there's actually a chain to add and not just a hydrogen
                    body += branch.serialize();
                };
                body += ",";
            };
            body = body.substring(0, body.length() - 1); // The -1 removes the final comma
        };

        optimumFROWNSCode = prefix + ":" + body;
        return optimumFROWNSCode;

    };

    private static Float getReflectionComparison(List<Branch> reflection) {
        float total = 0f;
        for (int i = 0; i < reflection.size(); i++) {
            total += i * reflection.get(i).getMassOfLongestChain();
        };
        return total;
    };

    private static Branch getMaximumBranchWithHighestMass(Map<Atom, List<Bond>> structure) {
        List<Atom> terminalAtoms = new ArrayList<>();
        for (Atom atom : structure.keySet()) {
            if (structure.get(atom).size() == 1) {
                terminalAtoms.add(atom);
            };
        };

        Collections.sort(terminalAtoms, (a1, a2) -> {
            return getMaximumBranch(a2, structure).getMassOfLongestChain().compareTo(getMaximumBranch(a1, structure).getMassOfLongestChain()); // Put in descending order of chain length
        });
        Collections.sort(terminalAtoms, (a1, a2) -> {
            return Branch.getMassForComparisonInSerialization(a1).compareTo(Branch.getMassForComparisonInSerialization(a2));
        });

        return getMaximumBranch(terminalAtoms.get(0), structure);
    };

    /**
     * Creates a Formula from a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * Hydrogens will be {@link Formula#addAllHydrogens added automatically}.
     * @param FROWNSstring
     * @return A new Formula instance
     */
    public static Formula deserialize(String FROWNSstring) {
        try {
            Formula formula;

            String[] topologyAndFormula = FROWNSstring.strip().split(":");
            Topology topology;
            String formulaString;

            if (topologyAndFormula.length == 3) {
                topology = Topology.getTopology(topologyAndFormula[0] + ":" + topologyAndFormula[1]);
                formulaString = topologyAndFormula[2];
            } else {
                throw new IllegalStateException("Badly formatted FROWNS string '"+FROWNSstring+"'. They should be in the format 'namespace:topology:chains'.");
            };

            if (topology == Topology.LINEAR) {
                List<String> symbols = Arrays.stream(formulaString.split("(?=\\p{Upper})")).toList(); //split String into sub-strings that start with a capital letter (i.e. Elements)
                formula = groupFromString(symbols);
            } else {
                if (topology.formula == null) throw new IllegalStateException("Missing base formula for Topology "+topology.getID());
                formula = topology.formula.shallowCopy(); // Gives a null warning which has been accounted for
                if (topology.getConnections() == 0) return formula.refreshFunctionalGroups();
                int i = 0;
                for (String group : formulaString.split(",")) {
                    if (i > formula.topology.connections.size()) throw new IllegalStateException("Formula '" + FROWNSstring + "' has too many groups for its Topology. There should be " + formula.topology.connections.size() + ".");
                    Formula sideChain;
                    if (group.isBlank()) {
                        sideChain = new Formula(new Atom(Element.HYDROGEN));
                    } else {
                        sideChain = groupFromString(Arrays.stream(group.split("(?=\\p{Upper})")).toList());
                    };
                    formula.addGroupToPosition(sideChain, i, formula.topology.connections.get(i).bondType());
                    i++;
                };
            };

            formula.addAllHydrogens().refreshFunctionalGroups();
            formula.updateSideChainStructures(); // Update this so the side chain structures which copy the Atoms in the overall structure also contain all newly-added Hydrogens
            return formula;

        } catch(Exception e) {
            throw new IllegalArgumentException("Could not parse FROWNS String '" + FROWNSstring + "'", e);
        }
    };

    /**
     * Checks this structure for any {@link Group functional Groups} it contains and updates the {@link Formula#groups stored Groups}.
     * @return This Formula
     */
    public Formula refreshFunctionalGroups() {
        this.groups = new ArrayList<>();
        for (GroupFinder finder : GroupFinder.allGroupFinders()) {
            //if (topology == Topology.LINEAR) {
                groups.addAll(finder.findGroups(structure));
            // } else {
            //     for (Pair<SideChainInformation, Formula> sideChain : sideChains) {
            //         groups.addAll(finder.findGroups(sideChain.getSecond().structure)); // Don't include cyclic Atoms in Groups
            //     };
            // };
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
            newFormula.optimumFROWNSCode = null; // Let the FROWNS code be reset
            newFormula.structure = new HashMap<>();
            newFormula.structure = shallowCopyStructure(structure); // Shallow copy the Structure
            newFormula.groups = new ArrayList<>(groups); // Shallow copy the Groups
            newFormula.topology = this.topology; // Shallow copy the Topology
            updateSideChainStructures();
            newFormula.sideChains = sideChains.stream().map(pair -> Pair.of(pair.getFirst(), pair.getSecond().shallowCopy())).toList();
            newFormula.optimumFROWNSCode = null; // Delete the FROWNS Code again, as copies are typically going to be modified

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

    /**
     * Get the directed form of this structure, for use in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering}.
     */
    public Branch getRenderBranch() {
        if (topology != Topology.LINEAR) throw new IllegalStateException("Cannot get a Render branch for a cyclic Molecule.");
        return getMaximumBranchWithHighestMass(structure);
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
                if (connectedNode != null && !connectedNode.visited) {
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

                    Map<Atom, List<Bond>> newStructure = shallowCopyStructure(structure); // Create a new Structure which does not include the current Node
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
        if (group.topology != Topology.LINEAR) {
            throw new IllegalArgumentException("Cannot add Cycles as side-groups - to create a Cyclic Molecule, start with the Cycle and use addGroupAtPosition(), or use Formula.joinFormulae if this is in a Generic Reaction");
        };
        for (Entry<Atom, List<Bond>> entry : group.structure.entrySet()) {
            if (structureToMutate.containsKey(entry.getKey())) throw new IllegalStateException("Cannot add a derivative of a Formula to itself.");
            structureToMutate.put(entry.getKey(), entry.getValue());
        };
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

            if (symbols.get(i).matches(".*\\)")) throw new IllegalArgumentException("Chain bond type symbols must preceed side groups; for example chloroethene must be 'destroy:linear:C=(Cl)C' and not 'destroy:linear:C(Cl)=C'.");

            String symbol;
            Map<Formula, BondType> groupsToAdd = new HashMap<>(); //a list of all the Groups to be added, and the Type of Bond by which they should be added
            BondType thisAtomBond = nextAtomBond;

            if (symbols.get(i).contains("(")) { //If this Atom marks the beginning of a side Group (i.e. the next Atom will comprise the start of the side Group)

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

            // Check if this is a numbered R-Group
            char lastChar = symbol.charAt(symbol.length() - 1);
            int rGroupNumber = 0;
            if (Character.isDigit(lastChar)) {
                symbol = symbol.substring(0, symbol.length() - 1);
                rGroupNumber = Integer.valueOf(lastChar - '0');
            };
            Atom atom = new Atom(Element.fromSymbol(symbol));
            atom.rGroupNumber = rGroupNumber;

            // Add the Atom to the Formula
            if (hasFormulaBeenInstantiated) { //if this is not the first Atom
                formula.addGroup(new Formula(atom), false, thisAtomBond);
            } else {
                formula = new Formula(atom);
                hasFormulaBeenInstantiated = true;
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
                if (!bond.getDestinationAtom().isHydrogen()) {
                    bondsToAdd.add(bond);
                };
            };
            if (!atom.isHydrogen()) {
                newStructure.put(atom, bondsToAdd);
            };
        };
        return newStructure;
    };

    /**
     * A 3D structure of a {@link Molecule} if it is cyclic.
     * This class contains {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering} information.
     */
    public static class Topology {
    
        /**
         * The register of Topologies, mapped to their IDs (e.g. {@code destroy:benzene}).
         */
        private static final Map<String, Topology> TOPOLOGIES = new HashMap<>();

        public static final Topology LINEAR = new Builder(Destroy.MOD_ID).build("linear");
    
        /**
         * The name space of the mod by which this Topology was defined.
         */
        private final String nameSpace;
        /**
         * The {@link Topology#getID ID} of this Molecule, not including its {@link Topology#nameSpace name space}.
         */
        private String id;

        /**
         * The {@link Formula structure} of this Topology, when it has no side chains attached.
         */
        private Formula formula;
        
        /**
         * Every {@link Atom} in this Topology, paired with its location relative to the first Atom in the Topology.
         */
        private final List<Pair<Vec3, Atom>> atomsAndLocations;
        /**
         * Every {@link Bond} in this Topology. Although Bonds are monodirectional, this list only contains a single
         * Bond out of the 'pair' that join two {@link Atom Atoms}.
         */
        private final List<Bond> bonds;
        /**
         * The {@link SideChainInformation side-chains} this Topology can accomodate.
         */
        private final List<SideChainInformation> connections;

        private int[][] reflections = null;
    
        private Topology(String nameSpace) {
            this.nameSpace = nameSpace;
            formula = null;
            atomsAndLocations = new ArrayList<>();
            bonds = new ArrayList<>();
            connections = new ArrayList<>();
        };
    
        /**
         * Get the Topology with the given ID.
         * @param id e.g. {@code destroy:benzene}.
         * @return A Topology, or null if the given ID has no associated Topology.
         */
        public static Topology getTopology(String id) {
            return TOPOLOGIES.get(id);
        };

        /**
         * Get the ID of this Topology in the form {@code <nameSpace>:<id>} (for
         * example {@code destroy:benzene}) for use in {@link Formula#serialize serialization} of {@link Molecule Molecules}.
         */
        public String getID() {
            return nameSpace + ":" + id;
        };

        /**
         * Get the number of side-chains this Molecule can accomodate.
         */
        public int getConnections() {
            return connections.size();
        };

        public int[][] getReflections() {
            return reflections;
        };
    
        /**
         * A class for constructing {@link Topology Topologies}.
         */
        public static class Builder {

            /**
             * The ID of the mod creating this Topology.
             */
            private final String nameSpace;
            /**
             * The Topology being built.
             */
            private final Topology topology;

            public Builder(String nameSpace) {
                this.nameSpace = nameSpace;
                topology = new Topology(nameSpace);
            };

            /**
             * Creates the first {@link Atom} in the Topology. It's position is defined as {@code (0,0,0)},
             * and the positions of all other Atoms in this Topology are given relative to it.
             * @param element The Element of the Atom to add
             * @return This Topology builder
             */
            public Builder startWith(Element element) {
                topology.formula = Formula.atom(element);
                topology.atomsAndLocations.add(Pair.of(new Vec3(0f, 0f, 0f), topology.formula.startingAtom)); // This gives a null warning which has been accounted for
                return this;
            };

            /**
             * Add a single-bonded side chain to the root {@link Atom} of this Topology.
             * @param bondDirection The direction of the next {@link Bond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This Topology builder
             */
            public Builder sideChain(Vec3 bondDirection, Vec3 branchDirection) {
                return sideChain(bondDirection, branchDirection, BondType.SINGLE);
            };

            /**
             * Add a side chain to the root {@link Atom} of this Topology.
             * @param bondDirection The direction of the next {@link Bond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This Topology builder
             */
            public Builder sideChain(Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {
                if (topology.reflections != null) throw new IllegalStateException("Cannot add more side chains once the reflections have been declared.");
                topology.connections.add(new SideChainInformation(topology.atomsAndLocations.get(0).getSecond(), bondDirection, branchDirection, bondType));
                return this;
            };

            /**
             * Adds an {@link Atom} to a Topology. Call {@link Builder#startWith startWith} first.
             * @param element The Element of the Atom to add (note that this is an Element and not an Atom because Atoms in cyclic {@link Formula structures} are not
             * allowed the same additional detail as regular Atoms, such as {@link Atom#getpKa pKa} and charge)
             * @param location The location of this Atom relative to the starting Atom, which is at {@code (0,0,0)} - for appropriate scaling, the distance between
             * bonded Atoms should be set to {@code 1.0}
             * @return The {@link AttachedAtom attached Atom, ready to be modified, such as by {@link AttachedAtom#withSideBranch adding additional side chains}.
             * @throws IllegalStateException If {@link Builder#startWith startWith} hasn't been called
             * @see AttachedAtom#attach Adding the Atom to this Topology once it has been modified
             */
            public AttachedAtom atom(Element element, Vec3 location) {
                // Check the Formula has been initialized
                if (topology.formula == null) throw new IllegalStateException("Cannot add Atoms to a Topology that hasn't been initialized with startWith(Element)");
                // Create the Atom
                Atom atom = new Atom(element);
                // Add the Atom to the Formula's structure
                topology.formula.structure.put(atom, new ArrayList<>()); // This gives a null warning which has been accounted for
                // Add the Atom to the list of Atoms to render
                topology.atomsAndLocations.add(Pair.of(location, atom));
                AttachedAtom attachedAtom = new AttachedAtom(this, atom);
                return attachedAtom;
            };

            /**
             * The array of all group orders which can be mapped from {@code [0, 1, 2, 3, 4, 5]} and will be the same isomer.
             * E.g. {@code destroy:benzene:A,B,C,D,E,F} is chemically the same isomer as {@code destroy:benzene:A,F,E,D,C,B}, so for
             * benzene this array should include the array {@code [0, 5, 4, 3, 2, 1]}.
             * @param reflections
             * @return This Topology builder
             */
            public Topology.Builder reflections(int[][] reflections) {
                int connections = topology.getConnections();
                for (int[] reflection : reflections) {
                    int sum = 0;
                    for (int i : reflection) sum += Math.pow(2, i);
                    if (sum != (int)Math.pow(2, connections) - 1 || reflection.length != connections) throw new IllegalStateException("Isomer configurations must match the number of side chains this Topology has.");
                };
                topology.reflections = reflections;
                return this;
            };

            /**
             * Add this Topology to the {@link Topology#TOPOLOGIES register}, allowing it to be referenced
             * when constructing {@link Molecules}.
             * @param id The name under which to register this Topology; it will be saved as {@code <nameSpace>:<id>}
             * @return The newly-built Topology
             */
            public Topology build(String id) {
                // Set the ID of the Topology
                topology.id = id;
                if (topology.formula != null) {
                    // Set the Topology of the Formula so any time it is copied (which is done when deserializing a cyclic Molecule) it has the right Topology
                    topology.formula.topology = topology;
                    // Tell the Formula where to bond side-chains
                    topology.connections.forEach(sideChainInfo -> topology.formula.sideChains.add(Pair.of(sideChainInfo, new Formula()))); // Gives a null warning, which has been accounted for
                }; 
                // Add the Topology to the register
                TOPOLOGIES.put(nameSpace+":"+id, topology);
                return topology;
            };
        };
    
        /**
         * An entry in a Topology, containing information about the {@link Atom} itself, connections
         * from this Atom to other Atoms in the Topology (allowing for the creation of cyclic {@link Molecule Molecules}),
         * and the geometrical data (for use in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering}) on
         * any side-chains attached to this Atom should generate.
         */
        public static class AttachedAtom {
    
            private final Builder builder;
            private final Atom atom;
    
            /**
             * Creates an attached {@link Atom}.
             * @param builder The Builder of the Topology to which to attach this Atom
             * @param atom The Atom being attached to this Topology
             */
            private AttachedAtom(Builder builder, Atom atom) {
                this.builder = builder;
                this.atom = atom;
            };

            /**
             * Adds a {@link Bond} between this Atom and the {@code n}th Atom
             * to be added to this Topology (where the first is {@code 0}).
             * @param n The index of the Atom to which to bond <em>this</em> Atom
             * @param bondType The {@link Bond.BondType type} of Bond between these two Atoms
             * @return This attached Atom
             */
            public AttachedAtom withBondTo(int n, BondType bondType) {
                if (n >= builder.topology.atomsAndLocations.size()) throw new IllegalArgumentException("Tried to Bond an Atom back to Atom "+n+" but the "+n+"th atom has not yet been added to the Topology.");
                if (builder.topology.formula == null) throw new IllegalArgumentException("Cannot add Bonds between Atoms that do not exist on the structure.");
                Atom atomToWhichToAttach = builder.topology.atomsAndLocations.get(n).getSecond();
                builder.topology.bonds.add(new Bond(atom, atomToWhichToAttach, bondType));
                addBondBetweenAtoms(builder.topology.formula.structure, atom, atomToWhichToAttach, bondType); // Gives a null warning, which is accounted for
                return this;
            };

            /**
             * Add a singly-bonded side chain to the current attached {@link Atom}.
             * @param bondDirection The direction of the next {@link Bond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering})
             * @return This attached Atom
             */
            public AttachedAtom withSideBranch(Vec3 bondDirection, Vec3 branchDirection) {
                return withSideBranch(bondDirection, branchDirection, BondType.SINGLE);
            };
    
            /**
             * Add a side chain to the current attached {@link Atom}.
             * @param bondDirection The direction of the next {@link Bond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This attached Atom
             */
            public AttachedAtom withSideBranch(Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {
                if (builder.topology.reflections != null) throw new IllegalStateException("Cannot add more side chains once the reflections have been declared.");
                builder.topology.connections.add(new SideChainInformation(atom, bondDirection, branchDirection, bondType));
                return this;
            };
    
            /**
             * Attaches this Atom with all of its information to the Topology.
             * @return The Topology Builder
             */
            public Builder attach() {
                return builder;
            };
        };

        /**
         * @param atom The Atom in the Topology to which the side-chain connects
         * @param bondDirection The direction of the first {@link Bond} in the side-chain
         * @param branchDirection The general direction of propagation of the side-chain
         * @param bondType The {@link BondType type} of {@link Bond} connecting the Atom to the first Atom in the side chain
         */
        public static record SideChainInformation(Atom atom, Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {};
    };

};
