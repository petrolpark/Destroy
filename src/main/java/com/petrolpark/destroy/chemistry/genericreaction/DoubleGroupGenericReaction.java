package com.petrolpark.destroy.chemistry.genericreaction;

import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Reaction;

import net.minecraft.resources.ResourceLocation;

public abstract class DoubleGroupGenericReaction<FirstGroup extends Group<FirstGroup>, SecondGroup extends Group<SecondGroup>> extends GenericReaction {

    private final GroupType<FirstGroup> firstType;
    private final GroupType<SecondGroup> secondType;

    public DoubleGroupGenericReaction(ResourceLocation id, GroupType<FirstGroup> firstType, GroupType<SecondGroup> secondType) {
        super(id);
        this.firstType = firstType;
        this.secondType = secondType;
        Group.groupIDsAndReactions.get(firstType).add(this);
        Group.groupIDsAndReactions.get(secondType).add(this);
        GENERIC_REACTIONS.add(this);
    };
    
    /**
     * Generates a Reaction (with non-abstract Reactants and Products) based on the given Molecules which have these Groups.
     * @param reactant1 has the {@link FirstGroup first declared Group}.
     * @param reactant2 has the {@link SecondGroup second declared Group}.
     * @return The whole Reaction, including the defined structures of the product(s).
     */
    public abstract Reaction generateReaction(GenericReactant<FirstGroup> firstReactant, GenericReactant<SecondGroup> secondReactant);

    @Override
    public final Boolean involvesSingleGroup() {
        return false;
    };

    public final GroupType<FirstGroup> getFirstGroupType() {
        return firstType;
    };

    public final GroupType<SecondGroup> getSecondGroupType() {
        return secondType;
    };

};
