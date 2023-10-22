package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;

import net.minecraft.resources.ResourceLocation;

public abstract class HalideSubstitution extends SingleGroupGenericReaction<HalideGroup> {

    public HalideSubstitution(ResourceLocation id) {
        super(id, DestroyGroupTypes.HALIDE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<HalideGroup> reactant) {
        Molecule reactantMolecule = reactant.getMolecule();
        HalideGroup halideGroup = reactant.getGroup();
        Molecule productMolecule = moleculeBuilder().structure(reactantMolecule.shallowCopyStructure()
            .moveTo(halideGroup.carbon)
            .addGroup(getSubstitutedGroup(), true)
            .remove(halideGroup.halogen)
        )
        .build();
        ReactionBuilder builder = reactionBuilder()
            .addReactant(reactantMolecule)
            .addProduct(productMolecule)
            .addProduct(getIon(halideGroup.halogen));
        transform(builder, halideGroup);
        return builder.build();
    };

    public abstract Formula getSubstitutedGroup();

    /**
     * Add any other necessary products, reactants, catalysts and rate constants to the Reaction.
     * @param builder The builder with the Halide reactant, organic product and halide ion product already added
     */
    public abstract void transform(ReactionBuilder builder, HalideGroup group);

    public Molecule getIon(Atom atom) {
        switch (atom.getElement()) {
            case FLUORINE:
                return DestroyMolecules.FLUORIDE;
            case CHLORINE:
                return DestroyMolecules.CHLORIDE;
            case IODINE:
                return DestroyMolecules.IODIDE;
            default:
                throw new GenericReactionGenerationException(atom.getElement().toString()+" is not a halogen.");
        }
    };
    
};
