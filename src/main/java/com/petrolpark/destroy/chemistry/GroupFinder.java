package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class GroupFinder {

    private static List<GroupFinder> FINDERS = new ArrayList<>();

    public GroupFinder() {
        FINDERS.add(this);
    };

    /**
     * Called by {@link Molecule Molecule} when identifying which Groups the Molecule contains. This should never be called otherwise.
     */
    public static List<GroupFinder> allGroupFinders() {
        return FINDERS;
    };

    /**
     * Given a structure, this function should return all Groups that the structure contains.
     * A 'Group' is a chemical functional group such as a carbonyl, alcohol, alkene, etc.
     * For detailed information on how to use Group Finders, see the <a href="https://github.com/petrolpark/Destroy/wiki/">Wiki</a>.
     * @param structure The Map of Atoms to all Bonds that Atom has - the {@link Formula Formula class} is essentially a wrapper for this Map.
     * @return The list of Groups which this Group Finder has identified as being contained within the given structure.
     */
    public abstract List<Group> findGroups(Map<Atom, Bond> structure);
}
