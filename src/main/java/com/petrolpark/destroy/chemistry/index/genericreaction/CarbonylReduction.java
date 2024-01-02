package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.CarbonylGroup;
import com.petrolpark.destroy.item.DestroyItems;

public class CarbonylReduction extends SingleGroupGenericReaction<CarbonylGroup> {

    public CarbonylReduction() {
        super(Destroy.asResource("carbonyl_reduction"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        CarbonylGroup carbonyl = reactant.getGroup();

        Formula structure = reactant.getMolecule().shallowCopyStructure();
        structure.moveTo(carbonyl.carbon)
            .addAtom(Element.HYDROGEN)
            .replaceBondTo(carbonyl.oxygen, BondType.SINGLE)
            .moveTo(carbonyl.oxygen)
            .addAtom(Element.HYDROGEN);

        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addSimpleItemCatalyst(DestroyItems.MAGIC_REDUCTANT::get, 1f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .activationEnergy(200f)
            .build();
    };
    
};
