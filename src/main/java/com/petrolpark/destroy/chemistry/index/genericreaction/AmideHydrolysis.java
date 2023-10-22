package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.UnsubstitutedAmideGroup;

public class AmideHydrolysis extends SingleGroupGenericReaction<UnsubstitutedAmideGroup> {
    
    public AmideHydrolysis() {
        super(Destroy.asResource("amide_hydrolysis"), DestroyGroupTypes.UNSUBSTITUTED_AMIDE);
    }

    @Override
    public Reaction generateReaction(GenericReactant<UnsubstitutedAmideGroup> reactant) {
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        UnsubstitutedAmideGroup group = reactant.getGroup();

        structure.moveTo(group.carbon)
            .remove(group.hydrogen1)
            .remove(group.hydrogen2)
            .remove(group.nitrogen)
            .addGroup(Formula.alcohol());

        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addReactant(DestroyMolecules.WATER)
            .addCatalyst(DestroyMolecules.PROTON, 1)
            .addProduct(moleculeBuilder().structure(structure).build())
            .addProduct(DestroyMolecules.AMMONIA)
            //TODO kinetics
            .build();
    };
};
