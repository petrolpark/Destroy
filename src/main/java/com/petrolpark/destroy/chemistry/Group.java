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
    public static Map<GroupType<?>, Set<GenericReaction>> groupTypesAndReactions = new HashMap<>();

    public Group() {
        groupTypesAndReactions.putIfAbsent(getType(), new HashSet<>());
    };

    /**
     * Get the {@link GenericReaction Generic Reactions} in which the given functional Group participates.
     * @param group
     */
    public static Set<GenericReaction> getReactionsOf(Group<?> group) {
        return groupTypesAndReactions.get(group.getType());
    };

    public abstract GroupType<G> getType();

    /**
     * Get the {@link GenericReaction Generic Reactions} which the functional Group identified by the given ID participates in.
     * @param type ID of the group
     */
    public static Set<GenericReaction> getReactionsOfGroupByID(GroupType<?> type) {
        return groupTypesAndReactions.get(type);
    };

    @Override
    public String toString() {
        return getType().toString();
    };
};
