package com.petrolpark.destroy.chemistry.genericreaction;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;

import net.minecraft.resources.ResourceLocation;

public abstract class DoubleGroupGenericReaction<FirstGroup extends Group<FirstGroup>, SecondGroup extends Group<SecondGroup>> extends GenericReaction {

    private final GroupType<FirstGroup> firstType;
    private final GroupType<SecondGroup> secondType;

    /**
     * This number is used to number R Groups in example Reactions.
     */
    private int i;

    public DoubleGroupGenericReaction(ResourceLocation id, GroupType<FirstGroup> firstType, GroupType<SecondGroup> secondType) {
        super(id);
        this.firstType = firstType;
        this.secondType = secondType;
        Group.groupTypesAndReactions.get(firstType).add(this);
        Group.groupTypesAndReactions.get(secondType).add(this);
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

    @Override
    @SuppressWarnings("unchecked")
    public Reaction generateExampleReaction() {

        i = 1;
        Molecule exampleMolecule1 = copyAndNumberRGroups(getFirstGroupType().getExampleMolecule());
        Molecule exampleMolecule2 = copyAndNumberRGroups(getSecondGroupType().getExampleMolecule());
        
        GenericReactant<FirstGroup> reactant1 = null;
        GenericReactant<SecondGroup> reactant2 = null;

        for (Group<?> group : exampleMolecule1.getFunctionalGroups()) { // Just in case the example Molecule has multiple functional groups (which it shouldn't ideally)
            if (group.getType() == getFirstGroupType()) reactant1 = new GenericReactant<>(exampleMolecule1, (FirstGroup)group);
        };
        for (Group<?> group : exampleMolecule2.getFunctionalGroups()) { // Just in case the example Molecule has multiple functional groups (which it shouldn't ideally)
            if (group.getType() == getSecondGroupType()) reactant2 = new GenericReactant<>(exampleMolecule2, (SecondGroup)group);
        };

        if (reactant1 == null || reactant2 == null) throw new IllegalStateException("Couldn't generate example Reaction for Generic Reaction "+id.toString());

        return generateReaction(reactant1, reactant2); // Unchecked conversion
        
    };

    private Molecule copyAndNumberRGroups(Molecule molecule) {
        Formula copiedStructure = molecule.shallowCopyStructure();
        for (Atom atom : molecule.getAtoms()) {
            if (atom.getElement() == Element.R_GROUP) {
                Atom newAtom = new Atom(Element.R_GROUP);
                newAtom.rGroupNumber = i;
                copiedStructure.replace(atom, newAtom);
                i++;
            };
        };
        return moleculeBuilder()
            .structure(copiedStructure)
            .build();
    };

};
