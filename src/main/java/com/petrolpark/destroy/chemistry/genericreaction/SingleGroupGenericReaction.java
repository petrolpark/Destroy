package com.petrolpark.destroy.chemistry.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;

import net.minecraft.resources.ResourceLocation;

public abstract class SingleGroupGenericReaction<G extends Group<G>> extends GenericReaction {

    protected final GroupType<G> type;

    public SingleGroupGenericReaction(ResourceLocation id, GroupType<G> type) {
        super(id);
        this.type = type;;
        Group.groupTypesAndReactions.get(type).add(this);
        GENERIC_REACTIONS.add(this); // Add this to the list of all known Generic Reactions
    };

    /**
     * Generates a Reaction (with non-abstract Reactant and Products) based on the given Molecule which has this Group.
     * @return The whole Reaction, including the defined structures of the product(s). Return {@code null} if the Reaction is impossible.
     */
    public abstract Reaction generateReaction(GenericReactant<G> reactant);

    public final GroupType<G> getGroupType() {
        return type;
    };

    @Override
    public final boolean involvesSingleGroup() {
        return true;
    };

    /**
     * Get the Reaction of the {@link GroupType#getExampleMolecule example Molecule} of the {@link SingleGroupGenericReaction#getGroupType Group Type} of this 
     * generic Reaction. This re-generates the Reaction each time, so use this method minimally or cache the result.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Reaction generateExampleReaction() {
        Molecule exampleMolecule = getGroupType().getExampleMolecule();
        int i = 1;
        Formula copiedStructure = exampleMolecule.shallowCopyStructure();
        for (Atom atom : exampleMolecule.getAtoms()) {
            if (atom.getElement() == Element.R_GROUP) {
                Atom newAtom = new Atom(Element.R_GROUP);
                newAtom.rGroupNumber = i;
                copiedStructure.replace(atom, newAtom);
                i++;
            };
        };
        copiedStructure.refreshFunctionalGroups();
        Molecule copiedExampleMolecule = moleculeBuilder()
            .structure(copiedStructure)
            .build();
        if (getGroupType().equals(DestroyGroupTypes.CARBONYL)) {
            Destroy.LOGGER.info("Hello");
        };
        for (Group<?> group : copiedExampleMolecule.getFunctionalGroups()) { // Just in case the example Molecule has multiple functional groups (which it shouldn't ideally)
            if (group.getType().equals(getGroupType())) {
                Reaction reaction = generateReaction(new GenericReactant<>(copiedExampleMolecule, (G)group)); // Unchecked conversion
                return reaction;
            };
        };
        //TODO cache these generated Molecules as currently they get regenerated every frame
        throw new RuntimeException("Couldn't generate example Reaction for Generic Reaction "+id.toString());
    };

};
