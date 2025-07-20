package com.petrolpark.destroy.chemistry.legacy.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.*;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacyReaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.legacy.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.group.SaturatedCarbonGroup;


public class AlkeneHydroborationOxidation extends SingleGroupGenericReaction<SaturatedCarbonGroup> {

    public AlkeneHydroborationOxidation() {
        super(Destroy.asResource("alkene_hydroboration_oxidation"), DestroyGroupTypes.ALKENE);
    };

    @Override
    public boolean isPossibleIn(ReadOnlyMixture mixture) {
        return mixture.getConcentrationOf(DestroyMolecules.DIBORANE) > 0f;
    };

    @Override
    public LegacyReaction generateReaction(GenericReactant<SaturatedCarbonGroup> reactant) {
        SaturatedCarbonGroup group = reactant.getGroup();
        LegacySpecies substrate = reactant.getMolecule();

        LegacySpecies product = moleculeBuilder().structure(substrate
                .shallowCopyStructure()
                .moveTo(group.highDegreeCarbon)
                .replaceBondTo(group.lowDegreeCarbon, BondType.SINGLE)
                .addAtom(LegacyElement.HYDROGEN)
                .moveTo(group.lowDegreeCarbon)
                .addGroup(LegacyMolecularStructure.alcohol(), true)
        ).build();

        ReactionBuilder builder = reactionBuilder()
                .addReactant(reactant.getMolecule(), 6)
                .addReactant(DestroyMolecules.HYDROGEN_PEROXIDE, 6)
                .addReactant(DestroyMolecules.DIBORANE)
                .addCatalyst(DestroyMolecules.HYDROXIDE, 1)
                .addProduct(product, 6)
                .addProduct(DestroyMolecules.BORIC_ACID, 2)
                .activationEnergy(25f);
        transform(builder);
        return builder.build();
    };

    public void transform(ReactionBuilder builder) {};

};
