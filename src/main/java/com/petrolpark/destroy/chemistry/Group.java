package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;

public abstract class Group {

    public static Map<String, Set<Class<? extends GenericReaction>>> groupIDsAndReactions = new HashMap<>();

    public Group() {
        groupIDsAndReactions.putIfAbsent(getID(), new HashSet<>());
    };

    public abstract Molecule getExampleMolecule();

    public abstract String getID();

    /**
     * Get the Generic Reactions which the given functional Group participates in.
     * @param group
     */
    public static final Set<Class<? extends GenericReaction>> getReactionsOf(Group group) {
        System.out.println("This is "+group.getID());
        return groupIDsAndReactions.get(group.getID());
    };
};
