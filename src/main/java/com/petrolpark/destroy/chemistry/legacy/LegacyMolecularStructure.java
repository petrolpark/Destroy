package com.petrolpark.destroy.chemistry.legacy;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.api.error.ChemistryException.FormulaSerializationException;
import com.petrolpark.destroy.chemistry.api.error.ChemistryException.MoleculeDeserializationException;
import com.petrolpark.destroy.chemistry.api.error.ChemistryException.TopologyDefinitionException;
import com.petrolpark.destroy.chemistry.api.error.ChemistryException.FormulaException.FormulaModificationException;
import com.petrolpark.destroy.chemistry.api.error.ChemistryException.FormulaException.FormulaRenderingException;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReaction;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure.Topology.SideChainInformation;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;

import net.createmod.catnip.data.Pair;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Collectors;

/**
 * A Formula is all the {@link LegacyAtom Atoms} in a {@link LegacySpecies}, and the {@link LegacyBond Bonds} that those Atoms have to other Atoms - a Molecule's 'structure'.
 * For convinience, the {@link LegacyFunctionalGroup functional Groups} present in the Molecule are also stored in its Formula.
 * <p>Formulae are also referred to as "structures" throughout the Destroy JavaDocs.</p>
 * <p><b>Formulae must always be {@link LegacyMolecularStructure#shallowCopy copied} before modifying.</b></p>
 */
public class LegacyMolecularStructure implements Cloneable {

    /**
     * Every {@link LegacyAtom} in this Formula, mapped to the {@link LegacyBond Bonds} that Atom has.
     */
    private Map<LegacyAtom, List<LegacyBond>> structure;

    /**
     * The starting {@link LegacyAtom} is used when constructing a Formula or sub-Formula.
     * When a sub-Formula is added to a Formula, the starting Atom of that sub-Formula is what gets {@link LegacyBond bonded} to the {@link LegacyMolecularStructure#currentAtom current Atom} of the main Formula.
     * <p>For newly-created Formulae, the current Atom is also set to the starting Atom.
     * @see LegacyMolecularStructure#addGroup Adding a sub-Formula to a Formula
     */
    private LegacyAtom startingAtom;

    /**
     * The current {@link LegacyAtom} is used when constructing or modifying a Formula. It has no behavioural purpose.
     * @see LegacyMolecularStructure#addAtom Adding an Atom to the current Atom
     */
    private LegacyAtom currentAtom;

    /**
     * The {@link LegacyFunctionalGroup functional Groups} this Formula contains.
     * Each Group is specific to a set of {@link LegacyAtom Atoms}, so for each instance of the same type of Group there is a separate entry.
     */
    private List<LegacyFunctionalGroup<?>> groups;
    
    /**
     * The {@link Topology Topology} (3D structure) of this Formula if it is cyclic.
     */
    private Topology topology;

    /**
     * A list of copies of Formulas attached to {@link LegacyMolecularStructure#topology cyclic} {@link LegacyAtom Atoms} in this Formula.
     * This is just used for {@link LegacySpecies#getRenderer rendering} and {@link LegacyMolecularStructure#serialize serialization} - originals
     * of all Atoms in the side-chains are still stored in this Formula.
     */
    private List<Pair<SideChainInformation, LegacyMolecularStructure>> sideChains;

    /**
     * The <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code representing this Formula.
     * This is used by novel {@link LegacySpecies Molecules} as their {@link LegacySpecies#getFullID ID}.
     * <p>A given Formula should always {@link LegacyMolecularStructure#serialize serialize} to the same FROWNS code (hence the moniker "optimum"), so that FROWNS codes can be used to {@link LegacySpecies#getEquivalent compare} Formulae.
     * This is stored so Formulae only have to calculate their FROWNS code once (as this is a resource-intensive process), and can be referred to later.</p>
     */
    @Nullable
    private String optimumFROWNSCode;

    private LegacyMolecularStructure() {
        structure = new LinkedHashMap<LegacyAtom, List<LegacyBond>>();
        groups = new ArrayList<>();
        topology = Topology.LINEAR;
        sideChains = new ArrayList<>();
        optimumFROWNSCode = null;
    };

    /**
     * A set of {@link LegacyAtom Atoms} and the {@link LegacyBond Bonds} that connect those Atoms.
     * @param startingAtom The Atom from which to start constructing this Formula.
     */
    public LegacyMolecularStructure(LegacyAtom startingAtom) {
        this();
        structure.put(startingAtom, new ArrayList<LegacyBond>());
        this.startingAtom = startingAtom;
        currentAtom = startingAtom;
    };

    /**
     * An empty Formula.
     * @return A new Formula instance with no {@link LegacyAtom Atoms}
     */
    private static LegacyMolecularStructure nothing() {
        return new LegacyMolecularStructure();
    };

    /**
     * Moves the {@link LegacyMolecularStructure#currentAtom currently selected} {@link LegacyAtom Atom} to the one given.
     * @param atom If this does not exist in the Formula, en error is raised
     * @return This Formula
     */
    public LegacyMolecularStructure moveTo(LegacyAtom atom) {
        if (structure.containsKey(atom)) {
            currentAtom = atom;
        } else {
            throw new FormulaModificationException(this, "Can't set the current Atom to an Atom not in the Formula");
        };
        return this;
    };

    /**
     * Change the {@link LegacyMolecularStructure#startingAtom starting} {@link LegacyAtom} to the one given.
     * This is useful when {@link LegacyMolecularStructure#addGroup attaching side-groups}, as they are always
     * joined by the starting Atom of the side group.
     * @param atom If this does not exist in the Formula, an error is raised
     * @return This Formula
     */
    public LegacyMolecularStructure setStartingAtom(LegacyAtom atom) {
        if (structure.containsKey(atom)) {
            startingAtom = atom;
        } else {
            throw new FormulaModificationException(this, "Can't set the starting Atom to an Atom not in the Formula");
        };
        return this;
    };

    /**
     * Generates an {@link LegacyAtom} of the {@link LegacyElement} as the starting point for a Formula.
     * @param element
     * @return A new Formula instance
     */
    public static LegacyMolecularStructure atom(LegacyElement element) {
        return LegacyMolecularStructure.atom(element, 0);
    };

    /**
     * Generates an {@link LegacyAtom} of the {@link LegacyElement} as the starting point for a Formula.
     * @param element
     * @param charge Formal charge of the starting Atom
     * @return A new Formula instance
     */
    public static LegacyMolecularStructure atom(LegacyElement element, double charge) {
        return new LegacyMolecularStructure(new LegacyAtom(element, charge));
    };

    /**
     * A straight chain of carbon {@link LegacyAtom Atoms} with no hydrogens.
     * @param length
     * @return A new Formula instance with the {@link LegacyMolecularStructure#startingAtom starting Atom} at one end.
     */
    public static LegacyMolecularStructure carbonChain(int length) {
        LegacyMolecularStructure carbonChain = LegacyMolecularStructure.atom(LegacyElement.CARBON);
        for (int i = 0; i < length - 1; i++) {
            carbonChain.addGroup(LegacyMolecularStructure.atom(LegacyElement.CARBON));
        };
        return carbonChain;
    };

    /**
     * An -OH group.
     * @return A new Formula instance, with the oxygen as the {@link LegacyMolecularStructure#startingAtom starting} {@link LegacyAtom}.
     */
    public static LegacyMolecularStructure alcohol() {
        return LegacyMolecularStructure.atom(LegacyElement.OXYGEN).addAtom(LegacyElement.HYDROGEN);
    };

    /**
     * A -BH2 group
     */
    public static LegacyMolecularStructure borane() {
        return LegacyMolecularStructure.atom(LegacyElement.BORON).addAtom(LegacyElement.HYDROGEN).addAtom(LegacyElement.HYDROGEN);
    };

    /**
     * Adds a singly-{@link LegacyBond bonded} {@link LegacyAtom} of an {@link LegacyElement} onto the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on the current Atom.
     * @param element The Element of the Atom to be added
     * @return This Formula
     * @see LegacyMolecularStructure#addAtom(LegacyElement, BondType) Adding a non-single Bond
     */
    public LegacyMolecularStructure addAtom(LegacyElement element) {
        return addAtom(element, BondType.SINGLE);
    };

    /**
     * Adds an {@link LegacyAtom} of an {@link LegacyElement} onto the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on the current Atom.
     * @param element The Element of the Atom to generate to add
     * @param bondType Defaults to a single {@link LegacyBond} if not supplied
     * @return This Formula
     */
    public LegacyMolecularStructure addAtom(LegacyElement element, BondType bondType) {
        addAtomToStructure(structure, currentAtom, new LegacyAtom(element), bondType);
        return this;
    };

    /**
     * Adds a singly-{@link LegacyBond bonded} {@link LegacyAtom} onto the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on the current Atom.
     * @param atom The Atom to be added.
     * @return This Formula
     * @see LegacyMolecularStructure#addAtom(LegacyAtom, BondType) Adding a non-single Bond
     */
    public LegacyMolecularStructure addAtom(LegacyAtom atom) {
        return addAtom(atom, BondType.SINGLE);
    };

    /**
     * Adds an {@link LegacyAtom} onto the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on the current Atom.
     * @param atom The Atom to add
     * @param bondType The {@link BondType type of Bond} the new Atom should have to the current Atom - defaults to a single {@link LegacyBond} if not supplied
     * @return This Formula
     */
    public LegacyMolecularStructure addAtom(LegacyAtom atom, BondType bondType) {
        addAtomToStructure(structure, currentAtom, atom, bondType);
        return this;
    };

    public boolean containsAtom(LegacyAtom atom) {
        return structure.containsKey(atom);
    };

    /**
     * Adds a singly-{@link LegacyBond bonded} sub-Formula to the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on that {@link LegacyAtom}.
     * @param group The sub-Formula to add
     * @return This Formula
     * @see LegacyMolecularStructure#startingAtom How sub-Formulae are added
     * @see LegacyMolecularStructure#addGroup(LegacyMolecularStructure, Boolean) Moving to the sub-Formula
     * @see LegacyMolecularStructure#addGroup(LegacyMolecularStructure, Boolean, BondType) Adding a non-single Bond
     * @see LegacyMolecularStructure#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public LegacyMolecularStructure addGroup(LegacyMolecularStructure group) {
        return this.addGroup(group, true);
    };

    /**
     * Adds a singly-{@link LegacyBond bonded} sub-Formula to the {@link LegacyMolecularStructure#currentAtom current} {@link LegacyAtom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * rather than move to the current Atom of the sub-Formula (defaults to {@code true} if not supplied)
     * @return This Formula
     * @see LegacyMolecularStructure#startingAtom How sub-Formulae are added
     * @see LegacyMolecularStructure#addGroup(LegacyMolecularStructure, Boolean, BondType) Adding a non-single Bond
     * @see LegacyMolecularStructure#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public LegacyMolecularStructure addGroup(LegacyMolecularStructure group, boolean isSideGroup) {
        return this.addGroup(group, isSideGroup, BondType.SINGLE);
    };

    /**
     * Join two Formula between their {@link LegacyMolecularStructure#currentAtom currently-selected} {@link LegacyAtom Atoms} with the given {@link BondType type} of {@link LegacyBond}.
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
    public static LegacyMolecularStructure joinFormulae(LegacyMolecularStructure formula1, LegacyMolecularStructure formula2, BondType bondType) {
        LegacyMolecularStructure formula;
        if (formula2.isCyclic()) {
            if (formula1.isCyclic()) throw new FormulaModificationException(formula1, "Cannot join two cyclic structures.");
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
     * Adds a sub-Formula to the {@link LegacyMolecularStructure#currentAtom current} {@link LegacyAtom}.
     * @param group The sub-Formula to add
     * @param isSideGroup Whether to stay on the current Atom to which the sub-Formula is being added,
     * or move to the current Atom of the sub-Formula (defaults to false if not supplied)
     * @param bondType The {@link BondType type of Bond} the sub-Formula should have to the current Atom - defaults to a single {@link LegacyBond} if not supplied
     * @return This Formula
     * @see LegacyMolecularStructure#startingAtom How sub-Formulae are added
     * @see LegacyMolecularStructure#joinFormulae Connecting Formulae of which you are unaware of the specific structure of each Formula, such as in {@link GenericReaction Generic Reactions}
     */
    public LegacyMolecularStructure addGroup(LegacyMolecularStructure group, Boolean isSideGroup, BondType bondType) {
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == currentAtom)) {
            throw new FormulaModificationException(this, "Cannot modify Atoms in cycle");
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
    public LegacyMolecularStructure addGroupToPosition(LegacyMolecularStructure group, int position) {
        return addGroupToPosition(group, position, BondType.SINGLE);
    };

    /**
     * Adds a group to an Atom in a Cyclic Molecule, and moves to the end of that group.
     * @param group
     * @param position Which positions correspond to which Atoms can be found <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">here</a>.
     * @param bondType Defaults to SINGLE.
     */
    @Deprecated
    public LegacyMolecularStructure addGroupToPosition(LegacyMolecularStructure group, int position, BondType bondType) {
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
     * @see LegacySpecies#getCyclicAtomsForRendering The wrapper for this method
     */
    public List<Pair<Vec3, LegacyAtom>> getCyclicAtomsForRendering() {
        return topology.atomsAndLocations;
    };

    /**
     * Get the list of {@link Topology#bonds cyclic Bonds} in the {@link Topology} associated with this Formula.
     * @return May be empty
     * @see LegacySpecies#getCyclicBondsForRendering The wrapper for this method
     */
    public List<LegacyBond> getCyclicBondsForRendering() {
        return topology.bonds;
    };

    /**
     * Get the list of {@link LegacyMolecularStructure#sideChains} side chains off of {@link LegacyMolecularStructure#topology cyclic} {@link LegacyAtom Atoms}
     * in this Formula.
     * @return May be empty
     * @see LegacySpecies#getSideChainsForRendering The wrapper for this method
     */
    public List<Pair<SideChainInformation, Branch>> getSideChainsForRendering() {
        return sideChains.stream().map(pair -> {
            LegacyMolecularStructure sideChain = pair.getSecond();
            return Pair.of(pair.getFirst(), getMaximumBranch(sideChain.startingAtom, sideChain.structure));
        }).toList();
    };

    /**
     * Removes the given Atom, without moving the currently selected Atom.
     * <p><b>To modify existing Formulae, {@link LegacyMolecularStructure#shallowCopy copy} them first.</b></p>
     * @param atom If this is the currently selected Atom, an error will be raised.
     */
    public LegacyMolecularStructure remove(LegacyAtom atom) {

        if (!structure.containsKey(atom)) {
            throw new FormulaModificationException(this, "Cannot remove "+atom.getElement().getSymbol()+" atom (does not exist).");
        };
        if (atom == currentAtom) {
            throw new FormulaModificationException(this, "Cannot remove the currently selected Atom from a structure being built.");
        };
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == atom)) {
            throw new FormulaModificationException(this, "Cannot remove Atoms in the cyclic part of a cyclic Molecule ");
        };
        
        for (LegacyBond bondToOtherAtom : structure.get(atom)) {
            structure.get(bondToOtherAtom.getDestinationAtom()).removeIf(bondToThisAtom -> {
                return bondToThisAtom.getDestinationAtom() == atom;
            });
        };
        structure.remove(atom);
        return this;
    };

    /**
     * Replaces one {@link LegacyAtom} with another, without moving the currently selected Atom.
     * <p><b>To modify existing Formulae, {@link LegacyMolecularStructure#shallowCopy copy} them first.</b></p>
     * @param oldAtom This must exist in the structure
     * @param newAtom
     */
    public LegacyMolecularStructure replace(LegacyAtom oldAtom, LegacyAtom newAtom) {
        if (!structure.containsKey(oldAtom)) {
            throw new FormulaModificationException(this, "Cannot replace "+oldAtom.getElement().getSymbol()+" atom (does not exist).");
        };
        if (oldAtom == currentAtom) currentAtom = newAtom;
        if (oldAtom == startingAtom) startingAtom = newAtom;
        if (topology.atomsAndLocations.stream().anyMatch(pair -> pair.getSecond() == oldAtom)) {
            throw new FormulaModificationException(this, "Cannot replace Atoms in the cyclic part of a cyclic Molecule ");
        };

        // Replace all Bonds to the old Atom with Bonds to the new Atom
        for (LegacyBond bondToOtherAtom : structure.get(oldAtom)) {
            structure.get(bondToOtherAtom.getDestinationAtom()).replaceAll(bond -> {
                if (bond.getDestinationAtom() == oldAtom) {
                    return new LegacyBond(bond.getSourceAtom(), newAtom, bond.getType());
                };
                return bond;
            });
        };

        // Replace Bonds from the old Atom with bonds from the new Atom
        List<LegacyBond> oldBonds = structure.get(oldAtom);
        structure.put(newAtom, oldBonds);

        // Remove the old Atom
        structure.remove(oldAtom);

        return this;
    };

    /**
     * Exchange the {@link LegacyBond} between the given {@link LegacyAtom} and the {@link LegacyMolecularStructure#currentAtom current} one.
     * @param otherAtom If there is not an existing Bond to this Atom, an error will be thrown
     * @param bondType
     * @return This Formula
     */
    public LegacyMolecularStructure replaceBondTo(LegacyAtom otherAtom, BondType bondType) {
        for (LegacyBond bond : structure.get(currentAtom)) {
            if (bond.getDestinationAtom() == otherAtom) {
                bond.setType(bondType);
                for (LegacyBond reverseBond : structure.get(otherAtom)) {
                    if (reverseBond.getDestinationAtom() == currentAtom) {
                        reverseBond.setType(bondType);
                        refreshFunctionalGroups();
                        return this;
                    };
                };
            };
        };
        throw new FormulaModificationException(this, "Cannot modify bond between two Atoms if they do not already have a Bond");
    };

    public LegacyMolecularStructure insertBridgingAtom(LegacyAtom atom1, LegacyAtom atom2, LegacyAtom bridgeAtom) {
        if (!structure.containsKey(atom1) || !structure.containsKey(atom2)) throw new FormulaModificationException(this, "Cannot add a bridging Atom between two Atoms not in this structure.");
        if (structure.containsKey(bridgeAtom)) throw new FormulaModificationException(this, "Bridging Atom cannot already exist in the Molecular Structure.");
        LegacyBond bridgeBond = null;
        for (LegacyBond bond : structure.get(atom1)) if (bond.getDestinationAtom() == atom2) {
            bridgeBond = bond;
            break;
        };
        if (bridgeBond == null) throw new FormulaModificationException(this, "Atoms to Bridge must be connected");
        boolean firstAtomCyclic = false, secondAtomCyclic = false;
        for (Pair<Vec3, LegacyAtom> cyclicAtom : topology.atomsAndLocations) {
            firstAtomCyclic |= cyclicAtom.getSecond() == atom1;
            secondAtomCyclic |= cyclicAtom.getSecond() == atom2;
            if (firstAtomCyclic && secondAtomCyclic) throw new FormulaModificationException(this, "Cannot add Bridging Atom between two Atoms part of the cycle in a cyclic Molecular Structure");
        };

        structure.get(atom1).remove(bridgeBond);
        structure.get(atom2).removeIf(b -> b.getDestinationAtom() == atom1);
        addAtomToStructure(structure, atom1, bridgeAtom, bridgeBond.getType());
        addBondBetweenAtoms(structure, atom2, bridgeAtom, bridgeBond.getType());

        updateSideChainStructures();

        return this;
    };

    /**
     * Break the Bond between the {@link LegacyMolecularStructure#currentAtom current} {@link LegacyAtom} and the given one.
     * This method mutates this fragment, as well as returning a <em>new</em> one (read: the return value will not necessarily be {@code this}).
     * @param otherAtom
     * @return The other half. If a ring was broken, then the return value will be {@code this}. No guarantee is made about which fragment ({@code this}, or the return value) contains the original {@link LegacyMolecularStructure#currentAtom current Atom}.
     */
    public LegacyMolecularStructure cleaveBondTo(LegacyAtom otherAtom) {
        
        // Remove the Bond
        LegacyBond bondCleaved = structure.get(currentAtom).stream().filter(b -> b.getDestinationAtom() == otherAtom).findFirst().orElseThrow(() -> new FormulaModificationException(this, "Cannot cleave Bond between two unconnected Atoms."));
        structure.get(currentAtom).remove(bondCleaved);
        structure.get(otherAtom).removeIf(b -> b.getDestinationAtom() == currentAtom);
    
        // Traverse fitst fragment
        Set<LegacyAtom> visited = new HashSet<>(structure.size());
        Set<LegacyAtom> visitedTwice = new HashSet<>(structure.size());
        List<LegacyAtom> toVisit = new ArrayList<>(structure.size());
        boolean cycleInFirstFragment = false;
        toVisit.add(currentAtom);
        while (!toVisit.isEmpty()) {
            LegacyAtom atom = toVisit.get(0);
            toVisit.remove(atom);
            visited.add(atom);
            checkConnected: for (LegacyBond bond : structure.get(atom)) {
                LegacyAtom connectedAtom = bond.getDestinationAtom();
                if (visited.contains(connectedAtom)) {
                    visitedTwice.add(connectedAtom);
                    continue checkConnected;
                };
                if (visitedTwice.contains(connectedAtom) || toVisit.contains(connectedAtom)) {
                    cycleInFirstFragment = true; // If an Atom is visited more than twice, we have a cycle
                } else {
                    toVisit.add(connectedAtom);
                };
            };
        };

        if (visited.contains(otherAtom)) { // If we reached the other Atom of the cleaved bond, thus having broken a ring
            if (!cycleInFirstFragment) { // If we are still left with a single structure
                topology = Topology.LINEAR;
                sideChains = Collections.emptyList();
                return this;
            } else { // If we have a cycle remaining but still ended up reaching the other Atom, we broke a different cycle so the Topology has changed unreconcilably
                throw new FormulaModificationException(this, "Can only break bonds in simple rings, not polycyclic compounds.");
            }
        } else { // If we made two distinct fragments

            Set<LegacyAtom> firstFragmentAtoms = new HashSet<>(visited);

            // Traverse the other fragment
            visited.clear(); // Now will contain all Atoms in the second fragment
            visitedTwice.clear();
            toVisit.clear();
            boolean cycleInSecondFragment = false;
            toVisit.add(otherAtom);
            while (!toVisit.isEmpty()) {
                LegacyAtom atom = toVisit.get(0);
                toVisit.remove(atom);
                visited.add(atom);
                checkConnected: for (LegacyBond bond : structure.get(atom)) {
                    LegacyAtom connectedAtom = bond.getDestinationAtom();
                    if (visited.contains(connectedAtom)) {
                        visitedTwice.add(connectedAtom);
                        continue checkConnected;
                    };
                    if (visitedTwice.contains(connectedAtom) || toVisit.contains(connectedAtom)) {
                        cycleInSecondFragment = true; // If an Atom is visited more than twice, we have a cycle
                    } else {
                        toVisit.add(connectedAtom);
                    };
                };
            };

            LegacyMolecularStructure fragment;

            if (cycleInSecondFragment) {
                if (cycleInFirstFragment) { // Both fragments have rings
                    throw new FormulaModificationException(this, "Cannot cleave bonds connecting two or more rings.");
                } else { // Only the second fragment has a ring
                    fragment = shallowCopy().removeAllWithoutChecks(visited);
                    removeAllWithoutChecks(firstFragmentAtoms);
                    fragment.startingAtom = fragment.currentAtom = currentAtom;
                    startingAtom = currentAtom = otherAtom;
                };
            } else { // Only the first, or neither fragments have a ring
                fragment = shallowCopy().removeAllWithoutChecks(firstFragmentAtoms);
                removeAllWithoutChecks(visited);
                fragment.startingAtom = fragment.currentAtom = otherAtom;
            };

            fragment.topology = Topology.LINEAR;
            fragment.sideChains.clear();

            return fragment;
        }
    };

    /**
     * Removes all {@link LegacyAtom}s given. This does not check for other {@link LegacyAtom}s {@link LegacyBond bonded} to the removed one, nor for the Topology of this structure. You should do that.
     * @param toRemove
     * @return This Molecular Structure
     */
    private LegacyMolecularStructure removeAllWithoutChecks(Collection<LegacyAtom> toRemove) {
        for (LegacyAtom atom : toRemove) structure.remove(atom);
        optimumFROWNSCode = null;
        return this;
    };

    /**
     * Adds an =O Group to the {@link LegacyMolecularStructure#currentAtom current Atom}, staying on the {@link LegacyAtom}.
     * @return This Formula
     */
    public LegacyMolecularStructure addCarbonyl() {
        addAtom(LegacyElement.OXYGEN, BondType.DOUBLE);
        return this;
    };

    /**
     * Adds hydrogen {@link LegacyAtom Atoms} to all Atoms which should have more if in a normal valency (as defined by their {@link LegacyElement}).
     * If an Element has multiple normal valencies, the next-lowest-possible is used.
     * <p>For example, this would turn {@code CO} into {@code CH₃OH}.
     * For something like sulfur with many normal valencies, {@code S} would turn into {@code H₂S} but {@code SH₃} into {@code SH₄}.
     * As you can see, this method does not produce chemically valid {@link LegacySpecies Molecules}, so should generally be avoided for weird and inorganic species.</p>
     * @return This Formula
     */
    public LegacyMolecularStructure addAllHydrogens() {
        Map<LegacyAtom, List<LegacyBond>> newStructure = new LinkedHashMap<LegacyAtom, List<LegacyBond>>(structure); // Create a shallow copy, as the original structure can't be modified while being iterated over

        // Replace all empty side chains with Hydrogen, if necessary
        if (topology != Topology.LINEAR) {
            for (int i = 0; i < sideChains.size(); i++) {
                LegacyAtom atom = sideChains.get(i).getFirst().atom();
                double totalBonds = getTotalBonds(newStructure.get(atom));
                if (atom.getElement().getNextLowestValency(totalBonds) - totalBonds > 0) {
                    LegacyAtom hydrogen = new LegacyAtom(LegacyElement.HYDROGEN);
                    sideChains.get(i).setSecond(new LegacyMolecularStructure(hydrogen));
                    addAtomToStructure(newStructure, atom, hydrogen, BondType.SINGLE);
                };
            };
        };

        // Add all the rest of the Hydrogens
        for (Entry<LegacyAtom, List<LegacyBond>> entry : structure.entrySet()) {
            LegacyAtom atom = entry.getKey();
            List<LegacyBond> bonds = entry.getValue();
            double totalBonds = getTotalBonds(bonds);

            for (int i = 0; i < atom.getElement().getNextLowestValency(totalBonds) - Math.abs(atom.formalCharge) - totalBonds; i++) {
                LegacyAtom hydrogen = new LegacyAtom(LegacyElement.HYDROGEN);
                addAtomToStructure(newStructure, atom, hydrogen, BondType.SINGLE);
            };
        };
        structure = newStructure;
        return this;
    };

    /**
     * Each {@link LegacyMolecularStructure#sideChains side chain} entry contains a {@link LegacyMolecularStructure} which references {@link LegacyAtom Atoms} and {@link LegacyBond Bonds} in the
     * main {@link LegacyMolecularStructure#structure structure}. When new Atoms are added to the main Formula, the Formulae of the side chains need to be updated.
     * <p>I don't know who needs to hear this but for the record this was a really complicated thing I had to do and it worked basically first try. Well done me.</p>
     */
    public void updateSideChainStructures() {
        if (topology == Topology.LINEAR) return;

        for (Pair<SideChainInformation, LegacyMolecularStructure> sideChain : sideChains) {
            LegacyMolecularStructure sideChainFormula = sideChain.getSecond();
            SideChainInformation info = sideChain.getFirst();

            if(sideChainFormula.startingAtom == null) continue;

            LegacyMolecularStructure newSideChainFormula = new LegacyMolecularStructure(sideChainFormula.startingAtom);
            Stack<LegacyAtom> frontier = new Stack<>();

            // First add the starting Atom and its associated Bonds, excluding any Bond to Atoms which are part of the Topology (and therefore not part of the side branch)
            List<LegacyBond> startingBonds = new ArrayList<>();
            for(LegacyBond bond : structure.get(sideChainFormula.startingAtom)) {
                LegacyAtom potentialNewAtom = bond.getDestinationAtom();

                if (topology.formula.structure.keySet().contains(potentialNewAtom)) continue;

                if (!newSideChainFormula.structure.keySet().contains(potentialNewAtom)) {
                    // Assume side branches will never loop back to the main Topology
                    // This means any Atom encountered can be directly added to the side branch without having to perform a full copy of its Bonds
                    newSideChainFormula.structure.put(potentialNewAtom, structure.get(potentialNewAtom));
                    frontier.push(potentialNewAtom);
                }

                startingBonds.add(bond);
            }
            newSideChainFormula.structure.put(sideChainFormula.startingAtom, startingBonds);

            // Now walk through the rest of the side branch and copy any Atom encountered
            while(!frontier.isEmpty()) {
                LegacyAtom currentAtom = frontier.pop();

                for(LegacyBond bond : structure.get(currentAtom)) {
                    LegacyAtom newAtom = bond.getDestinationAtom();

                    if (!newSideChainFormula.structure.keySet().contains(newAtom)) {
                        newSideChainFormula.structure.put(newAtom, structure.get(newAtom));
                        frontier.push(newAtom);
                    }
                }
            }

            sideChain.setSecond(newSideChainFormula);
        }
    };
 
    /**
     * The Set of every {@link LegacyAtom} in this Formula - essentially its molecular formula.
     */
    public Set<LegacyAtom> getAllAtoms() {
        return structure.keySet();
    };

    /**
     * Get all Atoms of the given Element which are bonded to the {@link LegacyMolecularStructure#currentAtom current} {@link LegacyAtom}.
     * @param element
     */
    public List<LegacyAtom> getBondedAtomsOfElement(LegacyElement element) {
        return GroupFinder.bondedAtomsOfElementTo(structure, currentAtom, element);
    };

    /**
     * The total {@link LegacyBond.BondType#getEquivalent single-bond equivalents} of a List of {@link LegacyBond Bonds}.
     * <p>Note that this returns an integer - it would be possible for an {@link LegacyAtom} to have a non-integeric sum,
     * but this method is used for automatically constructing organic {@link LegacySpecies Molecules} and their
     * {@link GenericReaction Reactions} so shouldn't be used for weirdly behaving inorganic things.
     * @param bonds
     */
    public double getTotalBonds(List<LegacyBond> bonds) {
        float total = 0;
        for (LegacyBond bond : bonds) {
            total += bond.getType().getEquivalent();
        };
        return total;
    };

    /**
     * Get all the {@link LegacyFunctionalGroup functional Groups} in this Formula.
     * @see LegacyMolecularStructure#groups Groups stored in Formulae
     */
    public List<LegacyFunctionalGroup<?>> getFunctionalGroups() {
        return groups;
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula or Group, with the given {@link LegacyAtom Atom} as the first character.
     * The {@link Topology} of this Formula is not included, and in fact this will quietly fail for cyclic {@link LegacySpecies Molecules}.
     * @param atom
     */
    private Branch getStrippedBranchStartingWithAtom(LegacyAtom atom) {
        Map<LegacyAtom, List<LegacyBond>> newStructure = stripHydrogens(structure);
        if (topology == Topology.LINEAR) {
            return getMaximumBranch(atom, newStructure);
        } else {
            throw new FormulaSerializationException("Cannot serialize branch if it is cyclic.");
        }
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code of this Formula.
     * This does not fresly calculate the FROWNS code each time, meaning Formulae should not be modifed without
     * first {@link LegacyMolecularStructure#shallowCopy copying} them.
     */
    public String serialize() {

        if (optimumFROWNSCode != null) { //in case this has already been serialized, we don't want to calculate it again
            return optimumFROWNSCode;
        };

        String body = "";
        String prefix = topology.getID();

        if (topology == Topology.LINEAR) {

            Map<LegacyAtom, List<LegacyBond>> newStructure = stripHydrogens(structure);

            body = getMaximumBranchWithHighestMass(newStructure).serialize();

        } else {
            updateSideChainStructures();
            List<Branch> identity = new ArrayList<>(topology.getConnections());

            if (topology.getConnections() > 0) for (int i = 0; i < topology.getConnections(); i++) {
                LegacyMolecularStructure sideChain = sideChains.get(i).getSecond();
                if (sideChain.getAllAtoms().size() == 0 || (sideChain.startingAtom.isNeutralHydrogen())) { // If there is nothing or just a hydrogen
                    identity.add(new Branch(new Node(new LegacyAtom(LegacyElement.HYDROGEN))));
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
            if (bestReflection.size() > 0) for (int i = 0; i < topology.getConnections(); i++) {
                Branch branch = bestReflection.get(i);
                if (!branch.getStartNode().getAtom().isNeutralHydrogen()) { // If there's actually a chain to add and not just a hydrogen
                    body += branch.serialize();
                };
                body += ",";
            };
            if (body.length() > 0) body = body.substring(0, body.length() - 1); // The -1 removes the final comma
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

    private static Branch getMaximumBranchWithHighestMass(Map<LegacyAtom, List<LegacyBond>> structure) {
        List<LegacyAtom> terminalAtoms = new ArrayList<>();
        for (LegacyAtom atom : structure.keySet()) {
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
     * Hydrogens will be {@link LegacyMolecularStructure#addAllHydrogens added automatically}.
     * @param FROWNSstring
     * @return A new Formula instance
     */
    public static LegacyMolecularStructure deserialize(String FROWNSstring) {
        try {
            LegacyMolecularStructure formula;

            String[] topologyAndFormula = FROWNSstring.strip().split(":");
            Topology topology;
            String formulaString;

            if (topologyAndFormula.length == 3) {
                topology = Topology.getTopology(topologyAndFormula[0] + ":" + topologyAndFormula[1]);
                formulaString = topologyAndFormula[2];
            } else {
                throw new MoleculeDeserializationException("Badly formatted FROWNS string '"+FROWNSstring+"'. They should be in the format 'namespace:topology:chains'.");
            };

            if (topology == Topology.LINEAR) {
                List<String> symbols = Arrays.stream(formulaString.split("(?=\\p{Upper})")).toList(); //split String into sub-strings that start with a capital letter (i.e. Elements)
                formula = groupFromString(symbols);
            } else {
                if (topology.formula == null) throw new MoleculeDeserializationException("Missing base formula for Topology "+topology.getID());
                formula = topology.formula.shallowCopy(); // Gives a null warning which has been accounted for
                if (topology.getConnections() == 0) return formula.refreshFunctionalGroups();
                int i = 0;
                for (String group : formulaString.split(",", -1)) {
                    if (i > formula.topology.connections.size()) throw new MoleculeDeserializationException("Formula '" + FROWNSstring + "' has too many groups for its Topology. There should be " + formula.topology.connections.size() + ".");
                    LegacyMolecularStructure sideChain;
                    if (group.isBlank()) {
                        sideChain = new LegacyMolecularStructure(new LegacyAtom(LegacyElement.HYDROGEN));
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

        } catch(Throwable e) {
            throw new IllegalArgumentException("Could not parse FROWNS String '" + FROWNSstring + "'", e);
        }
    };

    /**
     * Checks this structure for any {@link LegacyFunctionalGroup functional Groups} it contains and updates the {@link LegacyMolecularStructure#groups stored Groups}.
     * @return This Formula
     */
    public LegacyMolecularStructure refreshFunctionalGroups() {
        groups = new ArrayList<>();
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
     * Creates a shallow copy of this Formula. The clone Formula contains the exact same {@link LegacyAtom Atom} and {@link LegacyBond Bond} objects,
     * meaning an Atom in this Formula can be referenced in the clone Formula in the following ways:<ul>
     * <li>{@link LegacyMolecularStructure#moveTo Moving to an Atom}</li>
     * <li>{@link LegacyMolecularStructure#remove Removing an Atom}</li>
     * </ul>This is useful for generating specific {@link LegacyReaction Reactions} from {@link GenericReaction Generic Reactions}.
     * @return A new Formula instance
     */
    public LegacyMolecularStructure shallowCopy() {
        try {

            LegacyMolecularStructure newFormula = (LegacyMolecularStructure) super.clone();
            newFormula.structure = new LinkedHashMap<>(structure.size());
            newFormula.structure = shallowCopyStructure(structure); // Shallow copy the Structure
            newFormula.groups = new ArrayList<>(groups); // Shallow copy the Groups
            newFormula.topology = this.topology; // Shallow copy the Topology
            updateSideChainStructures();
            newFormula.sideChains = sideChains.stream().map(pair -> Pair.of(pair.getFirst(), pair.getSecond().shallowCopy())).collect(Collectors.toList());
            newFormula.optimumFROWNSCode = null; // Delete the FROWNS Code, as copies are typically going to be modified

            return newFormula;

        } catch(CloneNotSupportedException e) {
            throw new Error(e);
        }
    };

    /**
     * @param carbon
     * @param isCarbanion
     * @see LegacySpecies#getCarbocationStability The wrapper for this Method
     */
    public Float getCarbocationStability(LegacyAtom carbon, boolean isCarbanion) {
        Float totalElectronegativity = 0f;
        for (LegacyBond bond : structure.get(carbon)) {
            totalElectronegativity += bond.getDestinationAtom().getElement().getElectronegativity() * bond.getType().getEquivalent();
        };
        Float relativeElectronegativity = totalElectronegativity - (LegacyElement.CARBON.getElectronegativity() * 4);
        Float relativeStability = 1f + ((float)Math.pow(relativeElectronegativity, 4) / (float)Math.abs(relativeElectronegativity));
        return isCarbanion ^ relativeElectronegativity < 0 ? 1f / (Float)relativeStability : (Float)relativeStability;
    };

    /**
     * Get the directed form of this structure, for use in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering}.
     */
    public Branch getRenderBranch() {
        if (topology != Topology.LINEAR) throw new FormulaRenderingException(this, "Cannot get a Render branch for a cyclic Molecule.");
        return getMaximumBranchWithHighestMass(structure);
    };

    //INTERNAL METHODS

    /**
     * @param structureToCopy
     * @see LegacyMolecularStructure#shallowCopy The wrapper for this Method
     */
    private static Map<LegacyAtom, List<LegacyBond>> shallowCopyStructure(Map<LegacyAtom, List<LegacyBond>> structureToCopy) {
        Map<LegacyAtom, List<LegacyBond>> newStructure = new LinkedHashMap<>();
        for (LegacyAtom atom : structureToCopy.keySet()) {
            List<LegacyBond> oldBonds = structureToCopy.get(atom);
            List<LegacyBond> newBonds = new ArrayList<>();
            for (LegacyBond oldBond : oldBonds) {
                newBonds.add(new LegacyBond(atom, oldBond.getDestinationAtom(), oldBond.getType()));
            };
            newStructure.put(atom, newBonds);
        };
        return newStructure;
    };

    /**
     * Get the biggest directed {@link Branch} of {@link Node Nodes} generated from the given structure, starting from the given {@link LegacyAtom}.
     * @param startAtom Should not be an {@link LegacyAtom#isAcidicProton() acidic proton}
     * @param structure The {@link LegacyMolecularStructure#structure structure} for which to find the branch
     */
    private static Branch getMaximumBranch(LegacyAtom startAtom, Map<LegacyAtom, List<LegacyBond>> structure) {

        Map<LegacyAtom, Node> allNodes = new HashMap<>();

        for (LegacyAtom atom : structure.keySet()) {
            allNodes.put(atom, new Node(atom));
        };

        Node currentNode = allNodes.get(startAtom);
        currentNode.visited = true;

        Branch maximumBranch = new Branch(currentNode);

        Boolean nodesAdded = true;
        while (nodesAdded) {
            nodesAdded = false;
            Map<Node, BondType> connectedUnvisitedNodesAndTheirBondTypes = new HashMap<>();
            for (LegacyBond bond : structure.get(currentNode.getAtom())) {
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

                    Map<LegacyAtom, List<LegacyBond>> newStructure = shallowCopyStructure(structure); // Create a new Structure which does not include the current Node
                    LegacyBond bondToRemove = null;
                    for (LegacyBond bond : structure.get(node.getAtom())) {
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
     * The internal method for adding an {@link LegacyAtom} to a {@link LegacyMolecularStructure#structure structure}. This is mutative.
     * @param structureToMutate The structure to which to add the Atom
     * @param rootAtom The Atom to which the new Atom will be {@link LegacyBond bonded}
     * @param newAtom The Atom to add
     * @param bondType The {@link LegacyBond.BondType type of Bond} between the new and root Atoms
     * @return The original structure, now with the new Atom
     * @see LegacyMolecularStructure#addAtom(LegacyAtom, BondType) The wrapper for this method
     */
    private static Map<LegacyAtom, List<LegacyBond>> addAtomToStructure(Map<LegacyAtom, List<LegacyBond>> structureToMutate, LegacyAtom rootAtom, LegacyAtom newAtom, BondType bondType) {
        structureToMutate.put(newAtom, new ArrayList<>());
        addBondBetweenAtoms(structureToMutate, rootAtom, newAtom, bondType);
        return structureToMutate;
    };

    /**
     * The internal method for adding an sub-Formula to a {@link LegacyMolecularStructure#structure structure}. This is mutative.
     * @param structureToMutate The structure to which to add the {@link LegacyAtom}
     * @param rootAtom The Atom to which the new sub-Formula will be {@link LegacyBond bonded}
     * @param group The sub-Formula to add
     * @param bondType The {@link LegacyBond.BondType type of Bond} between the {@link LegacyMolecularStructure#startingAtom starting Atom} of the sub-Formula and the root Atom
     * @return The original structure, now with the new sub-Formula
     * @see LegacyMolecularStructure#addGroup(LegacyMolecularStructure, Boolean, BondType) The wrapper for this method
     */
    private static void addGroupToStructure(Map<LegacyAtom, List<LegacyBond>> structureToMutate, LegacyAtom rootAtom, LegacyMolecularStructure group, BondType bondType) {
        if (group.topology != Topology.LINEAR) {
            throw new FormulaModificationException(group, "Cannot add Cycles as side-groups - to create a Cyclic Molecule, start with the Cycle and use addGroupAtPosition(), or use Formula.joinFormulae if this is in a Generic Reaction");
        };
        for (Entry<LegacyAtom, List<LegacyBond>> entry : group.structure.entrySet()) {
            if (structureToMutate.containsKey(entry.getKey())) throw new FormulaModificationException(group, "Cannot add a derivative of a Formula to itself.");
            structureToMutate.put(entry.getKey(), entry.getValue());
        };
        addBondBetweenAtoms(structureToMutate, rootAtom, group.startingAtom, bondType);
    };

    /**
     * Creates a {@link LegacyBond} and its {@link LegacyBond#getMirror mirror} between two {@link LegacyAtom Atoms}.
     * @param structureToMutate The {@link LegacyMolecularStructure#structure structure in which} both these Atoms are
     * @param atom1 If not in the structure, will cause a runtime error
     * @param atom2 If not in the structure, will cause a runtime error
     * @param type The {@link LegacyBond.BondType type of Bond} between the two Atoms
     */
    private static void addBondBetweenAtoms(Map<LegacyAtom, List<LegacyBond>> structureToMutate, LegacyAtom atom1, LegacyAtom atom2, BondType type) {
        LegacyBond bond = new LegacyBond(atom1, atom2, type);
        structureToMutate.get(atom1).add(bond);
        structureToMutate.get(atom2).add(bond.getMirror());
    };

    /**
     * Recursively {@link LegacyMolecularStructure#serialize serializes} a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * @param symbols A FROWNS code split into individual Strings, each String containing a single {@link LegacyAtom}
     * @return A new Formula instance represented by the given FROWNS code
     */
    private static LegacyMolecularStructure groupFromString(List<String> symbols) {

        LegacyMolecularStructure formula = LegacyMolecularStructure.nothing();
        Boolean hasFormulaBeenInstantiated = false;

        BondType nextAtomBond = BondType.SINGLE;

        int i = 0;
        while (i < symbols.size()) {

            if (symbols.get(i).matches(".*\\)")) throw new MoleculeDeserializationException("Chain bond type symbols must preceed side groups; for example chloroethene must be 'destroy:linear:C=(Cl)C' and not 'destroy:linear:C(Cl)=C'.");

            String symbol;
            Map<LegacyMolecularStructure, BondType> groupsToAdd = new HashMap<>(); //a list of all the Groups to be added, and the Type of Bond by which they should be added
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

            // Check for charge
            double charge = 0;
            String[] symbolAndCharge = symbol.split("\\^");
            if (symbolAndCharge.length != 1) {
                symbol = symbolAndCharge[0];
                charge = Double.valueOf(symbolAndCharge[1]);
            };

            // Check if this is a numbered R-Group
            char lastChar = symbol.charAt(symbol.length() - 1);
            int rGroupNumber = 0;
            if (Character.isDigit(lastChar)) {
                symbol = symbol.substring(0, symbol.length() - 1);
                rGroupNumber = Integer.valueOf(lastChar - '0');
            };
            LegacyAtom atom = new LegacyAtom(LegacyElement.fromSymbol(symbol), charge); 
            atom.rGroupNumber = rGroupNumber;

            // Add the Atom to the Formula
            if (hasFormulaBeenInstantiated) { //if this is not the first Atom
                formula.addGroup(new LegacyMolecularStructure(atom), false, thisAtomBond);
            } else {
                formula = new LegacyMolecularStructure(atom);
                hasFormulaBeenInstantiated = true;
            };

            for (LegacyMolecularStructure group : groupsToAdd.keySet()) { //add all side Groups to the current Atom
                formula.addGroup(group, true, groupsToAdd.get(group));
            };

            i++; //move to the next Atom
        };

        return formula;
    };

    /**
     * The {@link LegacyBond.BondType type of Bond} after an {@link LegacyElement} symbol in a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * @param symbol A String which is part of a FROWNS Code, consisting of a single {@link LegacyAtom}
     */
    private static BondType trailingBondType(String symbol) {
        return BondType.fromFROWNSCode(symbol.charAt(symbol.length() - 1));
    };

    /**
     * Removes all {@link LegacyAtom#formalCharge neutral} hydrogen {@link LegacyAtom Atoms} from a {@link LegacyMolecularStructure#startingAtom structure}. This is mutative.
     * @param structure The structure from which to remove the hydrogen Atoms
     * @return The original structure, now with its non-acidic hydrogen Atoms removed
     */
    private static Map<LegacyAtom, List<LegacyBond>> stripHydrogens(Map<LegacyAtom, List<LegacyBond>> structure) {
        Map<LegacyAtom, List<LegacyBond>> newStructure = new LinkedHashMap<>();
        for (Entry<LegacyAtom, List<LegacyBond>> entry : structure.entrySet()) {
            LegacyAtom atom = entry.getKey();
            List<LegacyBond> bondsToInclude = new ArrayList<>();
            boolean includeAtom = !atom.isNeutralHydrogen(); // Include all non-hydrogen Atoms
            for (LegacyBond bond : entry.getValue()) {
                if (atom.formalCharge != 0 || bond.getDestinationAtom().formalCharge != 0 || !bond.getDestinationAtom().isNeutralHydrogen()) {
                    bondsToInclude.add(bond);
                    if (bond.getDestinationAtom().formalCharge != 0) includeAtom = true; // If we're a hydrogen bonded to a charged Atom, include
                };
            };
            if (includeAtom) {
                newStructure.put(atom, bondsToInclude);
            };
        };
        return newStructure;
    };

    /**
     * A 3D structure of a {@link LegacySpecies} if it is cyclic.
     * This class contains {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering} information.
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
         * The {@link LegacyMolecularStructure structure} of this Topology, when it has no side chains attached.
         */
        private LegacyMolecularStructure formula;
        
        /**
         * Every {@link LegacyAtom} in this Topology, paired with its location relative to the first Atom in the Topology.
         */
        private final List<Pair<Vec3, LegacyAtom>> atomsAndLocations;
        /**
         * Every {@link LegacyBond} in this Topology. Although Bonds are monodirectional, this list only contains a single
         * Bond out of the 'pair' that join two {@link LegacyAtom Atoms}.
         */
        private final List<LegacyBond> bonds;
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
         * example {@code destroy:benzene}) for use in {@link LegacyMolecularStructure#serialize serialization} of {@link LegacySpecies Molecules}.
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
             * Creates the first {@link LegacyAtom} in the Topology. It's position is defined as {@code (0,0,0)},
             * and the positions of all other Atoms in this Topology are given relative to it.
             * @param element The Element of the Atom to add
             * @return This Topology builder
             */
            public Builder startWith(LegacyElement element) {
                return startWith(element, 0d);
            };

            public Builder startWith(LegacyElement element, double charge) {
                topology.formula = LegacyMolecularStructure.atom(element, charge);
                topology.atomsAndLocations.add(Pair.of(new Vec3(0f, 0f, 0f), topology.formula.startingAtom)); // This gives a null warning which has been accounted for
                return this;
            };

            /**
             * Add a single-bonded side chain to the root {@link LegacyAtom} of this Topology.
             * @param bondDirection The direction of the next {@link LegacyBond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This Topology builder
             */
            public Builder sideChain(Vec3 bondDirection, Vec3 branchDirection) {
                return sideChain(bondDirection, branchDirection, BondType.SINGLE);
            };

            /**
             * Add a side chain to the root {@link LegacyAtom} of this Topology.
             * @param bondDirection The direction of the next {@link LegacyBond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This Topology builder
             */
            public Builder sideChain(Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {
                if (topology.reflections != null) throw new TopologyDefinitionException("Cannot add more side chains once the reflections have been declared.");
                topology.connections.add(new SideChainInformation(topology.atomsAndLocations.get(0).getSecond(), bondDirection, branchDirection, bondType));
                return this;
            };

            /**
             * Adds an {@link LegacyAtom} to a Topology. Call {@link Builder#startWith startWith} first.
             * @param element The Element of the Atom to add (note that this is an Element and not an Atom because Atoms in cyclic {@link LegacyMolecularStructure structures} are not
             * allowed the same additional detail as regular Atoms, such as {@link LegacyAtom#getpKa pKa} and charge)
             * @param location The location of this Atom relative to the starting Atom, which is at {@code (0,0,0)} - for appropriate scaling, the distance between
             * bonded Atoms should be set to {@code 1.0}
             * @return The {@link AttachedAtom attached Atom, ready to be modified, such as by {@link AttachedAtom#withSideBranch adding additional side chains}.
             * @throws IllegalStateException If {@link Builder#startWith startWith} hasn't been called
             * @see AttachedAtom#attach Adding the Atom to this Topology once it has been modified
             */
            public AttachedAtom atom(LegacyElement element, Vec3 location) {
                return atom(element, 0d, location);
            };

            public AttachedAtom atom(LegacyElement element, double charge, Vec3 location) {
                // Check the Formula has been initialized
                if (topology.formula == null) throw new TopologyDefinitionException("Cannot add Atoms to a Topology that hasn't been initialized with startWith(Element)");
                // Create the Atom
                LegacyAtom atom = new LegacyAtom(element, charge);
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
                    if (sum != (int)Math.pow(2, connections) - 1 || reflection.length != connections) throw new TopologyDefinitionException("Isomer configurations must match the number of side chains this Topology has.");
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
                    topology.connections.forEach(sideChainInfo -> topology.formula.sideChains.add(Pair.of(sideChainInfo, new LegacyMolecularStructure()))); // Gives a null warning, which has been accounted for
                };
                // Add no reflections if there are none
                if (topology.reflections == null) topology.reflections = new int[topology.connections.size()][0];
                // Add the Topology to the register
                TOPOLOGIES.put(nameSpace+":"+id, topology);
                return topology;
            };
        };
    
        /**
         * An entry in a Topology, containing information about the {@link LegacyAtom} itself, connections
         * from this Atom to other Atoms in the Topology (allowing for the creation of cyclic {@link LegacySpecies Molecules}),
         * and the geometrical data (for use in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering}) on
         * any side-chains attached to this Atom should generate.
         */
        public static class AttachedAtom {
    
            private final Builder builder;
            private final LegacyAtom atom;
    
            /**
             * Creates an attached {@link LegacyAtom}.
             * @param builder The Builder of the Topology to which to attach this Atom
             * @param atom The Atom being attached to this Topology
             */
            private AttachedAtom(Builder builder, LegacyAtom atom) {
                this.builder = builder;
                this.atom = atom;
            };

            /**
             * Adds a {@link LegacyBond} between this Atom and the {@code n}th Atom
             * to be added to this Topology (where the first is {@code 0}).
             * @param n The index of the Atom to which to bond <em>this</em> Atom
             * @param bondType The {@link LegacyBond.BondType type} of Bond between these two Atoms
             * @return This attached Atom
             */
            public AttachedAtom withBondTo(int n, BondType bondType) {
                if (n >= builder.topology.atomsAndLocations.size()) throw new TopologyDefinitionException("Tried to Bond an Atom back to Atom "+n+" but the "+n+"th atom has not yet been added to the Topology.");
                if (builder.topology.formula == null) throw new TopologyDefinitionException("Cannot add Bonds between Atoms that do not exist on the structure.");
                LegacyAtom atomToWhichToAttach = builder.topology.atomsAndLocations.get(n).getSecond();
                builder.topology.bonds.add(new LegacyBond(atom, atomToWhichToAttach, bondType));
                addBondBetweenAtoms(builder.topology.formula.structure, atom, atomToWhichToAttach, bondType); // Gives a null warning, which is accounted for
                return this;
            };

            /**
             * Add a singly-bonded side chain to the current attached {@link LegacyAtom}.
             * @param bondDirection The direction of the next {@link LegacyBond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering})
             * @return This attached Atom
             */
            public AttachedAtom withSideBranch(Vec3 bondDirection, Vec3 branchDirection) {
                return withSideBranch(bondDirection, branchDirection, BondType.SINGLE);
            };
    
            /**
             * Add a side chain to the current attached {@link LegacyAtom}.
             * @param bondDirection The direction of the next {@link LegacyBond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.core.chemistry.MoleculeRenderer rendering})
             * @param bondType The {@link BondType type} of Bond connecting the side chain to the Topology
             * @return This attached Atom
             */
            public AttachedAtom withSideBranch(Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {
                if (builder.topology.reflections != null) throw new TopologyDefinitionException("Cannot add more side chains once the reflections have been declared.");
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
         * @param bondDirection The direction of the first {@link LegacyBond} in the side-chain
         * @param branchDirection The general direction of propagation of the side-chain
         * @param bondType The {@link BondType type} of {@link LegacyBond} connecting the Atom to the first Atom in the side chain
         */
        public static record SideChainInformation(LegacyAtom atom, Vec3 bondDirection, Vec3 branchDirection, BondType bondType) {};
    };

};
