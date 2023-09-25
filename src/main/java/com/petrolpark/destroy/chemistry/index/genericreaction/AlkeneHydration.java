package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;

public class AlkeneHydration extends SingleGroupGenericReaction<AlkeneGroup> {

    public AlkeneHydration() {
        super(Destroy.asResource("alkene_hydration"), DestroyGroupTypes.ALKENE_GROUP);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlkeneGroup> reactant) {
        AlkeneGroup alkeneGroup = reactant.getGroup();
        Molecule alkene = reactant.getMolecule();
        
        Molecule alcohol = moleculeBuilder().structure(alkene
            .shallowCopyStructure()
            .moveTo(alkeneGroup.highDegreeCarbon)
            .replaceBondTo(alkeneGroup.lowDegreeCarbon, BondType.SINGLE)
            .addGroup(Formula.alcohol(), true)
            .moveTo(alkeneGroup.lowDegreeCarbon)
            .addAtom(Element.HYDROGEN)
        ).build();

        return reactionBuilder()
            .addReactant(alkene)
            .addReactant(DestroyMolecules.WATER, 1, 0)
            .addCatalyst(DestroyMolecules.SULFURIC_ACID, 1)
            .addProduct(alcohol)
            .build();
    };

    
};
