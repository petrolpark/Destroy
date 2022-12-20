package com.petrolpark.destroy.chemistry.genericreaction;

import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Reaction;

public abstract class DoubleGroupGenericReaction<FirstGroup extends Group, SecondGroup extends Group> extends GenericReaction {
    
    /**
     * Generates a Reaction (with non-abstract Reactants and Products) based on the given Molecules which have these Groups.
     * @param reactant1 has the {@link FirstGroup first declared Group}.
     * @param reactant2 has the {@link SecondGroup second declared Group}.
     * @return The whole Reaction, including the defined structures of the product(s).
     */
    public abstract Reaction generateReaction(GenericReactant<FirstGroup> reactant1, GenericReactant<SecondGroup> reactant2);

}
