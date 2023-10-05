package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.DoubleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.AcylChlorideGroup;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;

public class AcylChlorideEsterification extends DoubleGroupGenericReaction<AcylChlorideGroup, AlcoholGroup> {

    public AcylChlorideEsterification() {
        super(Destroy.asResource("acyl_chloride_esterification"), DestroyGroupTypes.ACYL_CHLORIDE, DestroyGroupTypes.ALCOHOL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AcylChlorideGroup> firstReactant, GenericReactant<AlcoholGroup> secondReactant) {
        Formula acylChlorideStructureCopy = firstReactant.getMolecule().shallowCopyStructure();
        AcylChlorideGroup acylChlorideGroup = firstReactant.getGroup();
        Formula alcoholStructureCopy = secondReactant.getMolecule().shallowCopyStructure();
        AlcoholGroup alcoholGroup = secondReactant.getGroup();

        alcoholStructureCopy.moveTo(alcoholGroup.getOxygen());
        alcoholStructureCopy.remove(alcoholGroup.getHydrogen());

        acylChlorideStructureCopy.moveTo(acylChlorideGroup.getCarbon());
        acylChlorideStructureCopy.remove(acylChlorideGroup.getChlorine());
        
        Molecule ester = moleculeBuilder().structure(Formula.joinFormulae(acylChlorideStructureCopy, alcoholStructureCopy, BondType.SINGLE)).build();

        return reactionBuilder()
            .addReactant(firstReactant.getMolecule())
            .addReactant(secondReactant.getMolecule())
            .addProduct(ester)
            .addProduct(DestroyMolecules.HYDROCHLORIC_ACID)
            //TODO kinetics
            .build();
    };
    
}
