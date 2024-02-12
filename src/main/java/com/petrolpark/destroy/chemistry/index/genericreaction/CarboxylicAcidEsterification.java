package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;
import com.petrolpark.destroy.chemistry.index.group.CarboxylicAcidGroup;

public class CarboxylicAcidEsterification extends DoubleGroupGenericReaction<CarboxylicAcidGroup, AlcoholGroup> {

    public CarboxylicAcidEsterification() {
        super(Destroy.asResource("carboxylic_acid_esterification"), DestroyGroupTypes.CARBOXYLIC_ACID, DestroyGroupTypes.ALCOHOL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarboxylicAcidGroup> firstReactant, GenericReactant<AlcoholGroup> secondReactant) {
        Formula acidStructureCopy = firstReactant.getMolecule().shallowCopyStructure();
        CarboxylicAcidGroup acidGroup = firstReactant.getGroup();
        Formula alcoholStructureCopy = secondReactant.getMolecule().shallowCopyStructure();
        AlcoholGroup alcoholGroup = secondReactant.getGroup();

        alcoholStructureCopy.moveTo(alcoholGroup.oxygen);
        alcoholStructureCopy.remove(alcoholGroup.hydrogen);

        acidStructureCopy.moveTo(acidGroup.carbon)
            .remove(acidGroup.proton)
            .remove(acidGroup.alcoholOxygen);

        Molecule ester = moleculeBuilder().structure(Formula.joinFormulae(acidStructureCopy, alcoholStructureCopy, BondType.SINGLE)).build();

        return reactionBuilder()
            .addReactant(firstReactant.getMolecule())
            .addReactant(secondReactant.getMolecule(), 1, 0)
            .addReactant(DestroyMolecules.OLEUM, 1)
            .addProduct(ester)
            .addProduct(DestroyMolecules.SULFURIC_ACID, 2)
            //TODO rate constants
            .build();
    };
    
};
