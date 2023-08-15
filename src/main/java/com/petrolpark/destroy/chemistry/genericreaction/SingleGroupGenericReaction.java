package com.petrolpark.destroy.chemistry.genericreaction;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Reaction;

public abstract class SingleGroupGenericReaction<T extends Group> extends GenericReaction {

    public SingleGroupGenericReaction(Supplier<T> supplier) {
        Group.groupIDsAndReactions.get(supplier.get().getID()).add(this);
        GENERIC_REACTIONS.add(this);
    };

    /**
     * Generates a Reaction (with non-abstract Reactant and Products) based on the given Molecule which has this Group.
     * @return The whole Reaction, including the defined structures of the product(s).
     */
    public abstract Reaction generateReaction(GenericReactant<T> reactant);

    @Override
    public final Boolean involvesSingleGroup() {
        return true;
    };

}
