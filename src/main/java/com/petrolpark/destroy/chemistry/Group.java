package com.petrolpark.destroy.chemistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.petrolpark.destroy.chemistry.genericreaction.GenericReaction;

/**
 * A functional group in a {@link Molecule}, containing the specific {@link Atom Atoms} within it.
 * Each subclass of Group represents a functional group (e.g. alkene, alcohol),
 * but instances of a Group subclass are specific to Molecules.
 * <p>Groups are identified within Molecule with {@link GroupFinder Group Finders}.</p>
 * <p>See the <a href="https://github.com/petrolpark/Destroy/wiki/Custom-Groups">Destroy Wiki</a> for more detail.</p>
 */
public abstract class Group<G extends Group<G>> {

    /**
     * All {@link GenericReaction Generic Reactions} in which {@link Molecule Molecules} with certain functional Groups can participate, indexed by the {@link Group#getType Group Types} of those functional Groups.
     */
    public static Map<GroupType<?>, Set<GenericReaction>> groupIDsAndReactions = new HashMap<>();

    public Group() {
        groupIDsAndReactions.putIfAbsent(getType(), new HashSet<>());
    };

    /**
     * A {@link Molecule} that contains nothing but this type of functional Group and {@link Element#R_GROUP R-Groups}.
     * This should be an effectively static method. It is used for conveying {@link GenericReaction Generic Reactions} in JEI.
     */
    public abstract Molecule getExampleMolecule();

    /**
     * Get the {@link GenericReaction Generic Reactions} in which the given functional Group participates.
     * @param group
     */
    public static final Set<GenericReaction> getReactionsOf(Group<?> group) {
        return groupIDsAndReactions.get(group.getType());
    };

    public abstract GroupType<G> getType();

    /**
     * Get the {@link GenericReaction Generic Reactions} which the functional Group identified by the given ID participates in.
     * @param ID The {@link Group#getID String ID} of the functional Group.
     */
    public static final Set<GenericReaction> getReactionsOfGroupByID(GroupType<?> type) {
        return groupIDsAndReactions.get(type);
    };
};
