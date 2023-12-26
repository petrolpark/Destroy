package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.CarboxylicAcidGroup;
import com.petrolpark.destroy.item.DestroyItems;

public class CarboxylicAcidReduction extends SingleGroupGenericReaction<CarboxylicAcidGroup> {

    public CarboxylicAcidReduction() {
        super(Destroy.asResource("carboxylic_acid_reduction"), DestroyGroupTypes.CARBOXYLIC_ACID);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarboxylicAcidGroup> reactant) {
        CarboxylicAcidGroup acid = reactant.getGroup();
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        structure.moveTo(acid.carbon)
            .remove(acid.proton)
            .remove(acid.alcoholOxygen)
            .addAtom(Element.HYDROGEN);

        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addSimpleItemCatalyst(DestroyItems.MAGIC_REDUCTANT::get, 1f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .activationEnergy(25f)
            .build();
    };
    
};
