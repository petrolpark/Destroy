package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.NitrileGroup;
import com.simibubi.create.AllTags;

public class NitrileHydrogenation extends SingleGroupGenericReaction<NitrileGroup> {

    public NitrileHydrogenation() {
        super(Destroy.asResource("nitrile_hydrogenation"), DestroyGroupTypes.NITRILE);
    };

    @Override
    public Reaction generateReaction(GenericReactant<NitrileGroup> reactant) {
        NitrileGroup group = reactant.getGroup();
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        
        structure.moveTo(group.carbon)
            .replaceBondTo(group.nitrogen, BondType.SINGLE)
            .addAtom(Element.HYDROGEN)
            .addAtom(Element.HYDROGEN)
            .moveTo(group.nitrogen)
            .addAtom(Element.HYDROGEN)
            .addAtom(Element.HYDROGEN);

        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addReactant(DestroyMolecules.HYDROGEN, 2)
            .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/nickel"), 1f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .build();
    };
    
};
