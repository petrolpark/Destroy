package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.CarbonylGroup;

public class CyanideNucleophilicAddition extends SingleGroupGenericReaction<CarbonylGroup> {

    public CyanideNucleophilicAddition() {
        super(Destroy.asResource("cyanide_nucleophilic_addition"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.CYANIDE) > 0f && mixture.getConcentrationOf(DestroyMolecules.PROTON) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        LegacySpecies reactantMolecule = reactant.getMolecule();
        CarbonylGroup carbonyl = reactant.getGroup();
        LegacySpecies productMolecule = moleculeBuilder().structure(reactantMolecule.shallowCopyStructure()
            .moveTo(carbonyl.carbon)
            .remove(carbonyl.oxygen)
            .addGroup(LegacyMolecularStructure.alcohol())
            .addGroup(LegacyMolecularStructure.atom(LegacyElement.CARBON).addAtom(LegacyElement.NITROGEN, BondType.TRIPLE))
        ).build();
        return reactionBuilder()
            .addReactant(reactantMolecule)
            .addReactant(DestroyMolecules.HYDROGEN_CYANIDE)
            .addCatalyst(DestroyMolecules.CYANIDE, 1)
            .addProduct(productMolecule)
            //TODO rate constants
            .build();
    };
    
};
