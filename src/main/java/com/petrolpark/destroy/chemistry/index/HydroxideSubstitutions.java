package com.petrolpark.destroy.chemistry.index;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.group.ChlorideGroup;

public class HydroxideSubstitutions extends SingleGroupGenericReaction<ChlorideGroup> {

    public HydroxideSubstitutions(Supplier<ChlorideGroup> supplier) {
        super(supplier);
    };

    @Override
    public Reaction generateReaction(GenericReactant<ChlorideGroup> reactant) {
        Molecule reactantMolecule = reactant.getMolecule();
        ChlorideGroup chlorideGroup = reactant.getGroup();
        Molecule productMolecule = moleculeBuilder().structure(reactantMolecule.shallowCopyStructure()
            .moveTo(chlorideGroup.getCarbon())
            .addGroup(Formula.alcohol())
            .remove(chlorideGroup.getChlorine())
        ).build();
        return Reaction.builder()
            .addReactant(reactantMolecule)
            .addReactant(DestroyMolecules.HYDROXIDE)
            .addProduct(productMolecule)
            .addProduct(DestroyMolecules.CHLORIDE)
            .preexponentialFactor(1e15f)
            .activationEnergy(100f)
            .build();
    };
    
}
