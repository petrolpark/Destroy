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
import com.petrolpark.destroy.chemistry.index.group.CarbonylGroup;
import com.petrolpark.destroy.item.DestroyItems;

public class AldehydeOxidation extends SingleGroupGenericReaction<CarbonylGroup> {

    public AldehydeOxidation() {
        super(Destroy.asResource("aldehyde_oxidation"), DestroyGroupTypes.CARBONYL);
    };

    @Override
    public Reaction generateReaction(GenericReactant<CarbonylGroup> reactant) {
        CarbonylGroup carbonyl = reactant.getGroup();
        if (carbonyl.isKetone) return null;
        Formula structure = reactant.getMolecule().shallowCopyStructure();
        List<Atom> hydrogens = structure.moveTo(carbonyl.carbon).getBondedAtomsOfElement(Element.HYDROGEN);
        if (hydrogens.isEmpty()) return null; // This should never be the case
        structure.remove(hydrogens.get(0))
            .addGroup(Formula.alcohol());
        return reactionBuilder()
            .addReactant(reactant.getMolecule())
            .addSimpleItemCatalyst(DestroyItems.MAGIC_OXIDANT::get, 0.5f)
            .activationEnergy(200f)
            .addProduct(moleculeBuilder().structure(structure).build())
            .build();
    };

    @Override
    public Reaction generateExampleReaction() {
        Atom carbon = new Atom(Element.CARBON);
        Atom oxygen = new Atom(Element.OXYGEN);
        Atom rGroup = new Atom(Element.R_GROUP);
        rGroup.rGroupNumber = 1;
        Molecule exampleMolecule = moleculeBuilder().structure(
            new Formula(carbon)
            .addAtom(rGroup)
            .addAtom(oxygen, BondType.DOUBLE)
            .addAtom(Element.HYDROGEN)
        ).build();
        return generateReaction(new GenericReactant<>(exampleMolecule, new CarbonylGroup(carbon, oxygen, false)));
    };
    
};
