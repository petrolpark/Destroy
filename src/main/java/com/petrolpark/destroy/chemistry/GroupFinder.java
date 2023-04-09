package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.chemistry.Bond.BondType;

/**
 * A class whose purpose is to locate {@link Group functional Groups} in {@link Molecules}.
 * Group Finders must be instantiated during Mod setup.
 */
public abstract class GroupFinder {

    /**
     * All Group Finders known to Destroy.
     */
    private static Set<GroupFinder> FINDERS = new HashSet<>();

    public GroupFinder() {
        FINDERS.add(this);
    };

    /**
     * All Group Finders known to Destroy.
     * <p>This is called by a {@link Molecule Molecule} when identifying which {@link Group} Groups a Molecule contains.
     * This should be no need to call this otherwise.</p>
     */
    public static Set<GroupFinder> allGroupFinders() {
        return FINDERS;
    };

    /**
     * Given a structure, this function should return all {@link Group functional Groups} that the structure contains.
     * For more information, see the <a href="https://github.com/petrolpark/Destroy/wiki/">Destroy Wiki</a>.
     * @param structure A Map of {@link Atom Atoms} to all {@link Bond Bonds} that Atom has (see the {@code structure} property of {@link Formula})
     * @return The list of Groups which this Group Finder has identified as being contained within the given structure
     */
    public abstract List<Group> findGroups(Map<Atom, List<Bond>> structure);

    /**
     * A convenience method that gives all {@link Atom Atoms} of the given {@link Element} {@link Bonded bonded} (with any {@link Bond.BondType type}) to the given Atom in the given structure.
     * @param structure A Map of Atoms in a {@link Molecule} to all Bonds that Atom has (see the {@code structure} property of {@link Formula})
     * @param atom The Atom to which to check for Bonds
     * @param element The Element to check for
     * @see GroupFinder#bondedAtomsOfElementTo(Map, Atom, Element, BondType) Finding Atoms bonded with a specific type
     */
    public static List<Atom> bondedAtomsOfElementTo(Map<Atom, List<Bond>> structure, Atom atom, Element element) {
        List<Atom> atoms = new ArrayList<>();
        for (Bond bond : structure.get(atom)) {
            if (bond.getDestinationAtom().getElement() == element) {
                atoms.add(bond.getDestinationAtom());
            };
        };
        return atoms;
    };

    /**
     * A convenience method that gives all {@link Atom Atoms} of the given {@link Element} {@link Bonded bonded} (with the given {@link Bond.BondType type}) to the given Atom in the given structure.
     * @param structure A Map of Atoms in a {@link Molecule} to all Bonds that Atom has (see the {@code structure} property of {@link Formula})
     * @param atom The Atom to which to check for Bonds
     * @param element The Element to check for
     * @param bondType The type of Bond to check for
     * @see GroupFinder#bondedAtomsOfElementTo(Map, Atom, Element) Finding Atoms bonded with any type
     */
    public static List<Atom> bondedAtomsOfElementTo(Map<Atom, List<Bond>> structure, Atom atom, Element element, BondType bondType) {
        List<Atom> atoms = new ArrayList<>();
        for (Bond bond : structure.get(atom)) {
            if (bond.getDestinationAtom().getElement() == element && bond.getType() == bondType) {
                atoms.add(bond.getDestinationAtom());
            };
        };
        return atoms;
    };
}
