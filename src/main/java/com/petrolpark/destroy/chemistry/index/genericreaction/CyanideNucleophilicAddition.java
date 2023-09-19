package com.petrolpark.destroy.chemistry.index.genericreaction;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.CarbonylGroup;

public class CyanideNucleophilicAddition extends SingleGroupGenericReaction<CarbonylGroup> {

    public CyanideNucleophilicAddition() {
        super(Destroy.asResource("cyanide_nucleophilic_addition"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        Molecule reactantMolecule = reactant.getMolecule();
        CarbonylGroup carbonyl = reactant.getGroup();
        Molecule productMolecule = moleculeBuilder().structure(reactantMolecule.shallowCopyStructure()
            .moveTo(carbonyl.getCarbon())
            .remove(carbonyl.getOxygen())
            .addGroup(Formula.alcohol())
            .addGroup(Formula.atom(Element.CARBON).addAtom(Element.NITROGEN, BondType.TRIPLE))
        ).build();
        return reactionBuilder()
            .addReactant(reactantMolecule)
            .addReactant(DestroyMolecules.CYANIDE)
            .addReactant(DestroyMolecules.PROTON)
            .addProduct(productMolecule)
            //TODO rate constants
            .build();
    };
    
};
