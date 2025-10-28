package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.NonTertiaryAmineGroup;

public class CyanamideAddition extends SingleGroupGenericReaction<NonTertiaryAmineGroup> {

    public CyanamideAddition() {
        super(Destroy.asResource("cyanamide_addition"), DestroyGroupTypes.NON_TERTIARY_AMINE);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.CYANAMIDE) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<NonTertiaryAmineGroup> reactant) {
        LegacyMolecularStructure structure = reactant.molecule.shallowCopyStructure();
        structure.moveTo(reactant.group.nitrogen)
            .remove(reactant.group.hydrogen)
            .addGroup(LegacyMolecularStructure.atom(LegacyElement.CARBON), false)
            .addAtom(LegacyElement.NITROGEN)
            .addAtom(LegacyElement.NITROGEN, BondType.DOUBLE);

        if(reactant.molecule.isHypothetical())
            structure.addAllHydrogens();

        return reactionBuilder()
            .addReactant(reactant.molecule)
            .addReactant(DestroyMolecules.CYANAMIDE)
            .addProduct(moleculeBuilder().structure(structure).build())
            .build();
    };
    
};
