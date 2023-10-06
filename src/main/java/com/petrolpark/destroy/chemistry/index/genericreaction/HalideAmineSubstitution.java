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
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;
import com.petrolpark.destroy.chemistry.index.group.NonTertiaryAmineGroup;

public class HalideAmineSubstitution extends DoubleGroupGenericReaction<HalideGroup, NonTertiaryAmineGroup> {

    public HalideAmineSubstitution() {
        super(Destroy.asResource("halide_amine_substitution"), DestroyGroupTypes.HALIDE, DestroyGroupTypes.NON_TERTIARY_AMINE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<HalideGroup> firstReactant, GenericReactant<NonTertiaryAmineGroup> secondReactant) {
        Formula halideStructureCopy = firstReactant.getMolecule().shallowCopyStructure();
        HalideGroup halideGroup = firstReactant.getGroup();
        Formula amineStructureCopy = secondReactant.getMolecule().shallowCopyStructure();
        NonTertiaryAmineGroup amineGroup = secondReactant.getGroup();

        halideStructureCopy
            .moveTo(halideGroup.carbon)
            .remove(halideGroup.halogen);

        amineStructureCopy
            .moveTo(amineGroup.nitrogen)
            .remove(amineGroup.hydrogen);

        Molecule substitutedAmine = moleculeBuilder().structure(Formula.joinFormulae(halideStructureCopy, amineStructureCopy, BondType.SINGLE)).build();

        return reactionBuilder()
            .addReactant(firstReactant.getMolecule())
            .addReactant(secondReactant.getMolecule(), 1, 2)
            .addProduct(substitutedAmine)
            .addProduct(HalideSubstitution.getIon(halideGroup.halogen))
            .addProduct(DestroyMolecules.PROTON)
            //TODO kinetics
            .build();
    };
    
};
