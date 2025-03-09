package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.NitrileGroup;

public class NitrileHydrolysis extends SingleGroupGenericReaction<NitrileGroup> {

    public NitrileHydrolysis() {
        super(Destroy.asResource("nitrile_hydrolysis"), DestroyGroupTypes.NITRILE);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.WATER) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<NitrileGroup> reactant) {
        NitrileGroup group = reactant.getGroup();
        LegacyMolecularStructure structure = reactant.getMolecule().shallowCopyStructure();
        structure.moveTo(group.carbon)
                .remove(group.nitrogen)
                .addCarbonyl()
                .addGroup(
                        LegacyMolecularStructure.atom(LegacyElement.NITROGEN)
                                .addAtom(LegacyElement.HYDROGEN)
                                .addAtom(LegacyElement.HYDROGEN)
                );
        return reactionBuilder()
                .addReactant(reactant.getMolecule())
                .addReactant(DestroyMolecules.WATER)
                .addCatalyst(DestroyMolecules.PROTON, 1)
                .addProduct(moleculeBuilder().structure(structure).build())
                .build();
    };

};