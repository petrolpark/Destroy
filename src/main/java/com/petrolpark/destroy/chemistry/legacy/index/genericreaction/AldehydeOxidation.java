package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyAtom;
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

public class AldehydeOxidation extends SingleGroupGenericReaction<CarbonylGroup> {

    public AldehydeOxidation() {
        super(Destroy.asResource("aldehyde_oxidation"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.DICHROMATE) > 0f && mixture.getConcentrationOf(DestroyMolecules.PROTON) > 0f; // TODO check for actual oxidant once Magic Oxidant is removed
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        CarbonylGroup carbonyl = reactant.getGroup();
        if (carbonyl.isKetone) return null;
        LegacyMolecularStructure structure = reactant.getMolecule().shallowCopyStructure();
        List<LegacyAtom> hydrogens = structure.moveTo(carbonyl.carbon).getBondedAtomsOfElement(LegacyElement.HYDROGEN);
        if (hydrogens.isEmpty()) return null; // This should never be the case
        structure.remove(hydrogens.get(0))
            .addGroup(LegacyMolecularStructure.alcohol());
        return reactionBuilder()
            .addReactant(reactant.getMolecule(), 3, 1)
            .addReactant(DestroyMolecules.DICHROMATE)
            .addReactant(DestroyMolecules.PROTON, 8, 1)
            .activationEnergy(10f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .addProduct(DestroyMolecules.CHROMIUM_III, 2)
            .addProduct(DestroyMolecules.WATER, 4)
            .build();
    };

    @Override
    public LegacyReaction generateExampleReaction() {
        LegacyAtom carbon = new LegacyAtom(LegacyElement.CARBON);
        LegacyAtom oxygen = new LegacyAtom(LegacyElement.OXYGEN);
        LegacyAtom rGroup = new LegacyAtom(LegacyElement.R_GROUP);
        rGroup.rGroupNumber = 1;
        LegacySpecies exampleMolecule = moleculeBuilder().structure(
            new LegacyMolecularStructure(carbon)
            .addAtom(rGroup)
            .addAtom(oxygen, BondType.DOUBLE)
            .addAtom(LegacyElement.HYDROGEN)
        ).build();
        return generateReaction(new GenericReactant<>(exampleMolecule, new CarbonylGroup(carbon, oxygen, false)));
    };
    
};
