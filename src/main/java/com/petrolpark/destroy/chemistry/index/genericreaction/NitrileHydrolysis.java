package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.NitrileGroup;

public class NitrileHydrolysis extends SingleGroupGenericReaction<NitrileGroup> {

    public NitrileHydrolysis() {
        super(Destroy.asResource("nitrile_hydrolysis"), DestroyGroupTypes.NITRILE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<NitrileGroup> reactant) {
        NitrileGroup group = reactant.getGroup();
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        structure.moveTo(group.carbon)
            .remove(group.nitrogen)
            .addCarbonyl()
            .addGroup(
                Formula.atom(Element.NITROGEN)
                .addAtom(Element.HYDROGEN)
                .addAtom(Element.HYDROGEN)
            );
        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addReactant(DestroyMolecules.WATER)
            .addCatalyst(DestroyMolecules.PROTON, 1)
            .addProduct(moleculeBuilder().structure(structure).build())
            .build();
    };
    
};
