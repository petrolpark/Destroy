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
import com.petrolpark.destroy.chemistry.genericReaction.GenericReaction;
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
    private List<Group> groups;
    
    /**
     * The {@link Topology Topology} (3D structure) of this Formula if it is cyclic.
     */
    private Topology topology;
    /**
     * The ordered list of Atoms in the base {@link Topology} of this Formula, if it is cyclic.
     * This may contain repeats if multiple side-chains are bonded to the same {@link Atom}.
     * @see Formula#addGroupToPosition Adding side-chains
     */
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
        groups = new ArrayList<>();
        topology = Topology.LINEAR;
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
        currentAtom = startingAtom;
        groups = new ArrayList<>();
        topology = Topology.LINEAR;
        cyclicAtoms = new ArrayList<Atom>();
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
            throw new IllegalStateException("Cannot remove Atoms in a cyclic Molecule ");
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
     * The {@link Topology} of this Formula is not included, and in fact this will quietly fail for cyclic {@link Molecule Molecules}.
     * @param atom
     */
    public String serializeStartingWithAtom(Atom atom) {

        Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);

        String body = "";
        if (topology == Topology.LINEAR) {
            body = getMaximumBranch(atom, newStructure).serialize();
        } else {
            Destroy.LOGGER.warn("Cannot serialize branch if it is cyclic.");
        };

        return body;
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

        Map<Atom, List<Bond>> newStructure = stripHydrogens(shallowCopyStructure(structure));

        if (topology == Topology.LINEAR) {

            //TODO fix so serialized Molecules are deterministic

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
            for (int i = 0; i < topology.getConnections(); i++) {
                body += ",";
                //TODO add side chains
                //TODO ensure this gives the same thing every time for symmetrical Topologies
            };
            body = body.substring(0, body.length() - 1);
        };


        optimumFROWNSCode = prefix + ":" + body;
        return optimumFROWNSCode;

    };

    /**
     * Creates a Formula from a <a href="https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS</a> code.
     * Hydrogens will be {@link Formula#addAllHydrogens added automatically}.
     * @param FROWNSstring
     * @return A new Formula instance
     */
    @SuppressWarnings("null")
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
                topology = Topology.LINEAR;
                formulaString = topologyAndFormula[1];
            };

            if (topology == Topology.LINEAR) {
                List<String> symbols = Arrays.stream(formulaString.split("(?=\\p{Upper})")).toList(); //split String into sub-strings that start with a capital letter (i.e. Elements)
                formula = groupFromString(symbols);
            } else {
                if (topology.formula == null) throw new IllegalStateException("Missing base formula for Topology "+topology.getID());
                formula = topology.formula.shallowCopy(); // Gives a null warning which has been accounted for
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

            newFormula.structure = shallowCopyStructure(structure); // Shallow copy the Structure

            newFormula.groups = new ArrayList<>(groups); // Shallow copy the Groups

            newFormula.topology = this.topology; // Shallow copy the Topology

            newFormula.optimumFROWNSCode = null; // Delete the FROWNS Code, as copies are typically going to be modified

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
        return getMaximumBranch(startingAtom, structure);
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
        if (group.topology != Topology.LINEAR) {
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
        @Nullable
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
            @SuppressWarnings("null")
            public Builder startWith(Element element) {
                topology.formula = Formula.atom(element);
                topology.atomsAndLocations.add(Pair.of(new Vec3(0f, 0f, 0f), topology.formula.currentAtom)); // This gives a null warning which has been accounted for
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
            @SuppressWarnings("null")
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
             * Add this Topology to the {@link Topology#TOPOLOGIES register}, allowing it to be referenced
             * when constructing {@link Molecules}.
             * @param id The name under which to register this Topology; it will be saved as {@code <nameSpace>:<id>}
             * @return The newly-built Topology
             */
            @SuppressWarnings("null")
            public Topology build(String id) {
                // Set the ID of the Topology
                topology.id = id;
                if (topology.formula != null) {
                    // Set the Topology of the Formula so any time it is copied (which is done when deserializing a cyclic Molecule) it has the right Topology
                    topology.formula.topology = topology;
                    // Tell the Formula where to bond side-chains
                    topology.atomsAndLocations.forEach(pair -> topology.formula.cyclicAtoms.add(pair.getSecond())); // Gives a null warning, which has been accounted for
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
            @SuppressWarnings("null")
            public AttachedAtom withBondTo(int n, BondType bondType) {
                if (n >= builder.topology.atomsAndLocations.size() || builder.topology.formula == null) throw new IllegalArgumentException("Cannot add Bonds between Atoms that do not exist on the structure");
                Atom atomToWhichToAttach = builder.topology.atomsAndLocations.get(n).getSecond();
                builder.topology.bonds.add(new Bond(atom, atomToWhichToAttach, bondType));
                addBondBetweenAtoms(builder.topology.formula.structure, atom, atomToWhichToAttach, bondType);// Gives a null warning, which is accounted for
                return this;
            };
    
            /**
             * 
             * @param bondDirection The direction of the next {@link Bond}
             * @param branchDirection The direction in which the side-chain should continue (as chains of Atoms zig-zag,
             * this is the net direction of movement. This information is used in {@link com.petrolpark.destroy.client.gui.MoleculeRenderer rendering})
             * @return This attached Atom
             */
            public AttachedAtom withSideBranch(Vec3 bondDirection, Vec3 branchDirection) {
                builder.topology.connections.add(new SideChainInformation(atom, bondDirection, branchDirection));
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
         * @param atom The Atom in the Topology the side-chain to which the side-chain connects
         * @param bondDirection The direction of the first {@link Bond} in the side-chain
         * @param branchDirection The general direction of propagation of the side-chain
         */
        public static record SideChainInformation(Atom atom, Vec3 bondDirection, Vec3 branchDirection) {};
    };

};
