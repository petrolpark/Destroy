package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyAtom;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.CarbonylGroup;

public class BorohydrideCarbonylReduction extends SingleGroupGenericReaction<CarbonylGroup> {

    public BorohydrideCarbonylReduction() {
        super(Destroy.asResource("borohydride_carbonyl_reduction"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.BOROHYDRIDE) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        CarbonylGroup carbonyl = reactant.getGroup();

        LegacyMolecularStructure structure = reactant.getMolecule().shallowCopyStructure();
        structure.moveTo(carbonyl.carbon)
            .addAtom(LegacyElement.HYDROGEN)
            .replaceBondTo(carbonyl.oxygen, BondType.SINGLE)
            .remove(carbonyl.oxygen)
            .addGroup(LegacyMolecularStructure.alcohol());

        return reactionBuilder()
            .addReactant(reactant.getMolecule(), 4)
            .addReactant(DestroyMolecules.WATER, 4) // workup included in reaction
            .addReactant(DestroyMolecules.BOROHYDRIDE)
            .addProduct(moleculeBuilder().structure(structure).build(), 4)
            .addProduct(DestroyMolecules.TETRAHYDROXYBORATE)
            .activationEnergy(50f)
            .build();
    };
    
};
