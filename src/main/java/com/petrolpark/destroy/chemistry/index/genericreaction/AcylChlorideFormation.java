package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.CarboxylicAcidGroup;

public class AcylChlorideFormation extends SingleGroupGenericReaction<CarboxylicAcidGroup> {

    public AcylChlorideFormation() {
        super(Destroy.asResource("acyl_chloride_formation"), DestroyGroupTypes.CARBOXYLIC_ACID);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarboxylicAcidGroup> reactant) {
        Molecule reactantMolecule = reactant.getMolecule();
        CarboxylicAcidGroup acidGroup = reactant.getGroup();

        Molecule productMolecule = moleculeBuilder().structure(reactantMolecule.shallowCopyStructure()
            .moveTo(acidGroup.carbon)
            .remove(acidGroup.alcoholOxygen)
            .remove(acidGroup.proton)
            .addAtom(Element.CHLORINE)
        ).build();

        return reactionBuilder()
            .addReactant(reactantMolecule)
            .addReactant(DestroyMolecules.PHOSGENE)
            .addProduct(productMolecule)
            .addProduct(DestroyMolecules.HYDROCHLORIC_ACID)
            .addProduct(DestroyMolecules.CARBON_DIOXIDE)
            //TODO kinetic constants
            .build();
    };
    
};
