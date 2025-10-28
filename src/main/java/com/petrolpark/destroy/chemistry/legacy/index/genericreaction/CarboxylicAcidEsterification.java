package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.AlcoholGroup;
import com.petrolpark.destroy.chemistry.legacy.index.group.CarboxylicAcidGroup;

public class CarboxylicAcidEsterification extends DoubleGroupGenericReaction<CarboxylicAcidGroup, AlcoholGroup> {

    public CarboxylicAcidEsterification() {
        super(Destroy.asResource("carboxylic_acid_esterification"), DestroyGroupTypes.CARBOXYLIC_ACID, DestroyGroupTypes.ALCOHOL);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.OLEUM) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<CarboxylicAcidGroup> firstReactant, GenericReactant<AlcoholGroup> secondReactant) {
        LegacyMolecularStructure acidStructureCopy = firstReactant.getMolecule().shallowCopyStructure();
        CarboxylicAcidGroup acidGroup = firstReactant.getGroup();
        LegacyMolecularStructure alcoholStructureCopy = secondReactant.getMolecule().shallowCopyStructure();
        AlcoholGroup alcoholGroup = secondReactant.getGroup();

        alcoholStructureCopy.moveTo(alcoholGroup.oxygen);
        alcoholStructureCopy.remove(alcoholGroup.hydrogen);

        acidStructureCopy.moveTo(acidGroup.carbon)
            .remove(acidGroup.proton)
            .remove(acidGroup.alcoholOxygen);

        LegacySpecies ester = moleculeBuilder().structure(LegacyMolecularStructure.joinFormulae(acidStructureCopy, alcoholStructureCopy, BondType.SINGLE)).build();

        return reactionBuilder()
            .addReactant(firstReactant.getMolecule())
            .addReactant(secondReactant.getMolecule(), 1, 0)
            .addCatalyst(DestroyMolecules.SULFURIC_ACID, 1)
            .addProduct(ester)
            .addProduct(DestroyMolecules.WATER)
            //TODO rate constants
            .build();
    };
    
};
