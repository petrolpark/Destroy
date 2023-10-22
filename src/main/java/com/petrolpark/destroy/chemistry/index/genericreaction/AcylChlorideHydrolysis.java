package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.AcylChlorideGroup;

public class AcylChlorideHydrolysis extends SingleGroupGenericReaction<AcylChlorideGroup> {

    public AcylChlorideHydrolysis() {
        super(Destroy.asResource("acyl_chloride_hydrolysis"), DestroyGroupTypes.ACYL_CHLORIDE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AcylChlorideGroup> reactant) {
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        AcylChlorideGroup group = reactant.getGroup();

        structure.moveTo(group.getCarbon())
            .remove(group.getChlorine())
            .addGroup(Formula.alcohol(), true);

        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addReactant(DestroyMolecules.WATER)
            .addProduct(moleculeBuilder().structure(structure).build())
            .addProduct(DestroyMolecules.HYDROCHLORIC_ACID)
            //TODO kinetics
            .build();
    };

};
