package com.petrolpark.destroy.chemistry.index.genericreaction;

import java.util.List;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyGroupTypes;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;
import com.petrolpark.destroy.item.DestroyItems;

public class AlcoholOxidation extends SingleGroupGenericReaction<AlcoholGroup> {

    public AlcoholOxidation() {
        super(Destroy.asResource("alcohol_oxidation"), DestroyGroupTypes.ALCOHOL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<AlcoholGroup> reactant) {
        AlcoholGroup alcohol = reactant.getGroup();
        if (alcohol.degree >= 3) return null;
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        List<Atom> hydrogens = structure.moveTo(alcohol.carbon).getBondedAtomsOfElement(Element.HYDROGEN);
        if (hydrogens.isEmpty()) return null; // This should never be the case
        structure
            .remove(hydrogens.get(0))
            .moveTo(alcohol.oxygen)
            .remove(alcohol.hydrogen)
            .replaceBondTo(alcohol.carbon, BondType.DOUBLE);
        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addSimpleItemCatalyst(DestroyItems.MAGIC_OXIDANT::get, 0.5f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .activationEnergy(25f)
            .build();
    };
    
    @Override
    public Reaction generateExampleReaction() {
        Atom hydrogen = new Atom(Element.HYDROGEN);
        Atom oxygen = new Atom(Element.OXYGEN);
        Atom carbon = new Atom(Element.CARBON);
        Atom rGroup1 = new Atom(Element.R_GROUP);
        Atom rGroup2 = new Atom(Element.R_GROUP);
        rGroup1.rGroupNumber = 1;
        rGroup2.rGroupNumber = 2;
        Molecule exampleMolecule = moleculeBuilder()
            .structure(
                new Formula(carbon)
                .addAtom(rGroup1)
                .addAtom(rGroup2)
                .addAtom(Element.HYDROGEN)
                .addGroup(new Formula(oxygen).addAtom(hydrogen))
            ).build();
        return generateReaction(new GenericReactant<>(exampleMolecule, new AlcoholGroup(carbon, oxygen, hydrogen, 2)));
    };
};
