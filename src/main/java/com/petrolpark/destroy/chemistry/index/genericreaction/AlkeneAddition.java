package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;

import net.minecraft.resources.ResourceLocation;

public abstract class AlkeneAddition extends SingleGroupGenericReaction<AlkeneGroup> {

    public AlkeneAddition(ResourceLocation id) {
        super(id, DestroyGroupTypes.ALKENE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlkeneGroup> reactant) {
        AlkeneGroup alkeneGroup = reactant.getGroup();
        Molecule alkene = reactant.getMolecule();
        
        Molecule product = moleculeBuilder().structure(alkene
            .shallowCopyStructure()
            .moveTo(alkeneGroup.highDegreeCarbon)
            .replaceBondTo(alkeneGroup.lowDegreeCarbon, BondType.SINGLE)
            .addGroup(getHighDegreeGroup(), true)
            .moveTo(alkeneGroup.lowDegreeCarbon)
            .addGroup(getLowDegreeGroup(), true)
        ).build();

        ReactionBuilder builder = reactionBuilder()
            .addReactant(alkene)
            .addProduct(product);
        transform(builder);
        return builder.build();
    };

    /**
     * The group to be added to the carbon with the lower degree.
     */
    public abstract Formula getLowDegreeGroup();
    
    /**
     * The group to be added to the carbon with the lower degree.
     */
    public abstract Formula getHighDegreeGroup();

    /**
     * Add any other necessary products, reactants, catalysts and rate constants to the Reaction.
     * @param builder The builder with the Alkene reactant and product already added
     */
    public abstract void transform(ReactionBuilder builder);
    
};
