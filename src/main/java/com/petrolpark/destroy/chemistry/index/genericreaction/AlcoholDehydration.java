package com.petrolpark.destroy.chemistry.index.genericreaction;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;

public class AlcoholDehydration extends SingleGroupGenericReaction<AlcoholGroup> {

    public AlcoholDehydration() {
        super(Destroy.asResource("alcohol_dehydration"), DestroyGroupTypes.ALCOHOL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlcoholGroup> reactant) {
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        AlcoholGroup alcohol = reactant.getGroup();

        ReactionBuilder builder = reactionBuilder();

        int products = 0;
        for (Atom carbon : structure.moveTo(alcohol.carbon).getBondedAtomsOfElement(Element.CARBON)) {
            List<Atom> hydrogens = structure.moveTo(carbon).getBondedAtomsOfElement(Element.HYDROGEN);
            List<Atom> carbons = structure.getBondedAtomsOfElement(Element.CARBON);
            if (hydrogens.size() + carbons.size() != 4 || hydrogens.size() == 0) continue; // Don't form from non-sp3 alkyl carbons
            Formula productStructure = structure.shallowCopy();
            productStructure
                .remove(hydrogens.get(0))
                .remove(alcohol.oxygen)
                .remove(alcohol.hydrogen)
                .moveTo(carbon)
                .replaceBondTo(alcohol.carbon, BondType.SINGLE);
            builder.addProduct(moleculeBuilder().structure(productStructure).build());
            products++;
        };

        if (products == 0) return null;

        builder
            .addReactant(reactant.getMolecule(), products)
            .addCatalyst(DestroyMolecules.SULFURIC_ACID, 1)
            .addProduct(DestroyMolecules.WATER, products);

        return builder.build(); //TODO finish

    };
    
};
