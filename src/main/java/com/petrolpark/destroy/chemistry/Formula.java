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

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.serializer.Branch;
import com.petrolpark.destroy.chemistry.serializer.Node;

public class Formula {

    private Map<Atom, List<Bond>> structure;
    private Atom startingAtom;
    private Atom currentAtom;

    private List<Group> groups; //all the functional Groups contained within this Structure
    
    private CycleType cycleType;
    private List<Atom> cyclicAtoms;

    private String optimumFROWNSCode;

    private Formula() {
        structure = new HashMap<Atom, List<Bond>>();
        cycleType = CycleType.NONE;
        optimumFROWNSCode = null;
    };

    public Formula(Atom startingAtom) {
        structure = new HashMap<Atom, List<Bond>>();
        structure.put(startingAtom, new ArrayList<Bond>());
        this.startingAtom = startingAtom;
        this.currentAtom = startingAtom;

        this.cycleType = CycleType.NONE;
        this.cyclicAtoms = new ArrayList<Atom>();
    };

    private static Formula nothing() {
        return new Formula();
    };

    /**
     * Moves the currently selected Atom to the one given (if it exists in the structure).
     * @param atom
     */
    public Formula moveTo(Atom atom) {
        if (structure.containsKey(atom)) {
            currentAtom = atom;
        };
        return this;
    };

    /**
     * Generates an Atom of the Element as the starting point for a Molecule or Group.
     * @param element
     */
    public static Formula atom(Element element) {
        return new Formula(new Atom(element));
    };

    /**
     * A Cn Group (without Hydrogens).
     * @param length
     */
    public static Formula carbonChain(int length) {
        Formula carbonChain = Formula.atom(Element.CARBON);
        for (int i = 0; i < length - 1; i++) {
            carbonChain.addGroup(Formula.atom(Element.CARBON));
        };
        return carbonChain;
    };

    /**
     * An -OH Group.
     */
    public static Formula alcohol() {
        return Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN);
    };

    /**
     * An acidic -OH Group.
     */
    public static Formula acidicAlcohol(float pKa) {
        return Formula.atom(Element.OXYGEN).addAcidicProton(pKa);
    };

    /**
     * A -COOH Group.
     */
    public static Formula carboxylicAcid(float pKa) {
        return Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN)
            .addGroup(Formula.atom(Element.OXYGEN).addAcidicProton(pKa));
    };

    /**
     * A benzene ring (without Hydrogens).
     */
    public static Formula benzene() {
        Atom firstAtom = new Atom(Element.CARBON);
        Formula benzeneRing = new Formula(firstAtom);
        benzeneRing.cyclicAtoms.add(firstAtom);

        for (int i = 1; i < 6; i++) {
            Atom atom = new Atom(Element.CARBON);
            benzeneRing.cyclicAtoms.add(atom);
            benzeneRing.addAtomToStructure(benzeneRing.structure, benzeneRing.cyclicAtoms.get(i-1), atom, BondType.AROMATIC); //bond each Carbon to the previous
        };

        benzeneRing.addBondBetweenAtoms(firstAtom, benzeneRing.cyclicAtoms.get(5), BondType.AROMATIC); //bond the sixth Carbon to the first
        benzeneRing.cycleType = CycleType.BENZENE;

        return benzeneRing;
    };

    /**
     * Adds a singly-bonded Atom of an Element onto the current Atom, staying on the current Atom
     * @param element The Element of the Atom to generate to be added (in its default form).
     */
    public Formula addAtom(Element element) {
        return addAtom(element, BondType.SINGLE);
    };

    /**
     * Adds an Atom of an Element onto the current Atom, staying on the current Atom
     * @param element The Element of the Atom to generate to be added (in its default form)
     * @param bondType Defaults to SINGLE
     * @return
     */
    public Formula addAtom(Element element, BondType bondType) {
        return addAtomToStructure(structure, currentAtom, new Atom(element), bondType);
    };

    /**
     * Adds a singly-bonded Atom onto the current Atom, staying on the current Atom
     * @param atom The Atom to be added
     * @return
     */
    public Formula addAtom(Atom atom) {
        return addAtom(atom, BondType.SINGLE);
    };

    /**
     * Adds an Atom onto the current Atom, staying on the current Atom
     * @param atom The Atom to be added
     * @param bondType Defaults to SINGLE
     * @return
     */
    public Formula addAtom(Atom atom, BondType bondType) {
        return addAtomToStructure(structure, currentAtom, atom, bondType);
    };

    /**
     * Adds an acidic proton (Hydrogen Atom) to the current Atom, staying on that Atom
     * @param pKa
     * @return
     */
    public Formula addAcidicProton(float pKa) {
        return addAtom((new Atom(Element.HYDROGEN)).setpKa(pKa));
    };

    /**
     * Adds a singly-bonded Group to the current Atom, staying on that Atom.
     * @param group
     * @return
     */
    public Formula addGroup(Formula group) {
        return this.addGroup(group, false);
    };

    /**
     * Adds a singly-bonded Group to the current Atom.
     * @param group
     * @param isSideGroup Whether to stay on the Atom to which the Group is being added, or move to the end of the Group (defaults to false)
     * @return
     */
    public Formula addGroup(Formula group, Boolean isSideGroup) {
        return this.addGroup(group, isSideGroup, BondType.SINGLE);
    };

    /**
     * Adds a Group to the current Atom.
     * @param group
     * @param isSideGroup Whether to stay on the Atom to which the Group is being added, or move to the end of the Group (defaults to false)
     * @param bondType Defaults to SINGLE
     * @return
     */
    public Formula addGroup(Formula group, Boolean isSideGroup, BondType bondType) {
        addGroupToStructure(structure, currentAtom, group, bondType);
        if (!isSideGroup) currentAtom = group.currentAtom;
        return this;
    };

    /**
     * Adds a singly-bonded Group to an Atom in a Cyclic Molecule, and moves to the end of that Group.
     * @param group
     * @param position Which positions correspond to which Atoms can be found <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">here</a>.
     * @return
     */
    public Formula addGroupToPosition(Formula group, int position) {
        return addGroupToPosition(group, position, BondType.SINGLE);
    };

    /**
     * Adds a Group to an Atom in a Cyclic Molecule, and moves to the end of that Group.
     * @param group
     * @param position Which positions correspond to which Atoms can be found <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">here</a>.
     * @param bondType Defaults to SINGLE
     * @return
     */
    public Formula addGroupToPosition(Formula group, int position, BondType bondType) {
        addGroupToStructure(structure, cyclicAtoms.get(position), group, bondType);
        currentAtom = group.currentAtom;
        return this;
    };

    public Formula remove(Atom atom) {
        if (atom == currentAtom) {
            throw new IllegalStateException("Cannot remove the currently selected Atom from a structure being built.");
        };
        
        for (Bond bond : structure.get(atom)) {
            structure.get(bond.getSourceAtom()).remove(bond);
        };
        structure.remove(atom);
        return this;
    };

    /**
     * Adds a =O Group to the current Atom, staying on the Atom.
     * @return
     */
    public Formula addCarbonyl() {
        addAtom(Element.OXYGEN, BondType.DOUBLE);
        return this;
    };

    /**
     * Adds Hydrogens to all Atoms which are missing Hydrogens in their lowest oxidation state.
     * @param throwError Whether this should throw an error if an invalid Valency is encountered.
     * @return
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
     * The Set of every Atom in the Formula.
     */
    public Set<Atom> getAllAtoms() {
        return structure.keySet();
    };

    /**
     * The total single-bond equivalent of a List of Bonds.
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
     * Get all the functional Groups in a Molecule.
     * @return
     */
    public List<Group> getFunctionalGroups() {
        return groups;
    };

    /**
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS Code</a> of this Formula or Group, with the given Atom as the first character.
     * @param atom The Atom to start with
     * @return FROWNS Code
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
     * Get the <a href = "https://github.com/petrolpark/Destroy/wiki/FROWNS">FROWNS Code</a> of this Formula or Group.
     */
    public String serialize() {

        if (optimumFROWNSCode != null) { //in case this has already been serialized, we don't want to calculate it again
            return optimumFROWNSCode;
        };

        String body = "";
        String prefix = cycleType.getName();

        if (cycleType == CycleType.NONE) {

            Map<Atom, List<Bond>> newStructure = stripHydrogens(structure);

            List<Atom> terminalAtoms = new ArrayList<>();
            for (Atom atom : newStructure.keySet()) {
                if (newStructure.get(atom).size() == 1) {
                    terminalAtoms.add(atom);
                };
            };
            Collections.sort(terminalAtoms, (a1, a2) -> {
                return getMaximumBranch(a2, structure).getMassOfLongestChain().compareTo(getMaximumBranch(a1, newStructure).getMassOfLongestChain()); //put in descending order of chain length
            });

            body = getMaximumBranch(terminalAtoms.get(0), newStructure).serialize();

        } else {
            //TODO serialize cyclic molecules
            throw new IllegalStateException("Can't do this yet");
        };

        return prefix + ":" + body;

    };

    /**
     * See https://github.com/petrolpark/Destroy/wiki/FROWNS
     * @param string
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
            };

            return formula.addAllHydrogens().refreshFunctionalGroups();

        } catch(Exception e) {
            throw new Error("Could not parse FROWNS String '" + FROWNSstring + "': " + e);
        }
    };

    /**
     * Checks this structure for any Groups it contains.
     * @return This Formula.
     */
    public Formula refreshFunctionalGroups() {
        this.groups = new ArrayList<>();
        for (GroupFinder finder : GroupFinder.allGroupFinders()) {
            groups.addAll(finder.findGroups(structure));
        };
        return this;
    };

    //INTERNAL METHODS

    private Branch getMaximumBranch(Atom startAtom, Map<Atom, List<Bond>> structure) {

        Map<Atom, Node> allNodes = new HashMap<Atom, Node>();

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

                    Map<Atom, List<Bond>> newStructure = structure; //create a new Structure which does not include the current Node
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

    private Formula addAtomToStructure(Map<Atom, List<Bond>> structureToMutate, Atom rootAtom, Atom newAtom, BondType bondType) {
        Bond newBond = new Bond(rootAtom, newAtom, bondType);
        structureToMutate.put(newAtom, new ArrayList<>(Arrays.asList(newBond)));
        structureToMutate.get(rootAtom).add(newBond);
        return this;
    };

    private Formula addGroupToStructure(Map<Atom, List<Bond>> structureToMutate, Atom rootAtom, Formula group, BondType bondType) {
        if (group.cycleType != CycleType.NONE) {
            Destroy.LOGGER.warn("Cannot add Cycles as side-groups - to create a Cyclic Molecule, start with the Cycle and use addGroupAtPosition()");
            return this;
        };
        structureToMutate.putAll(group.structure);
        addBondBetweenAtoms(rootAtom, group.startingAtom, bondType);
        return this;
    };

    private void addBondBetweenAtoms(Atom atom1, Atom atom2, BondType type) {
        Bond bond = new Bond(atom1, atom2, type);
        structure.get(atom1).add(bond);
        structure.get(atom2).add(bond.getMirror());
    };

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

    private static BondType trailingBondType(String symbol) {
        BondType bondType = BondType.SINGLE; //initially assume the Group will be singly bonded
        switch (symbol.charAt(symbol.length() - 1)) { //get last character
            case '=':
                bondType = BondType.DOUBLE;
                break;
            case '#':
                bondType = BondType.TRIPLE;
                break;
            case '~':
                bondType = BondType.AROMATIC;
                break;
        };
        return bondType;
    };

    private Map<Atom, List<Bond>> stripHydrogens(Map<Atom, List<Bond>> structure) {
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
    
    private enum CycleType {

        NONE("linear", () -> Formula.nothing()),
        BENZENE("benzene", () -> benzene());

        private String name; //used for de/serialization
        private Supplier<Formula> constructor;

        private CycleType(String name, Supplier<Formula> constructor) {
            this.name = name;
            this.constructor = constructor;
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

        public Formula create() {
            return this.constructor.get();
        };
    };

};
