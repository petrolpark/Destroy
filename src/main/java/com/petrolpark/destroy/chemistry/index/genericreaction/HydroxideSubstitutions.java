package com.petrolpark.destroy.chemistry.index.genericreaction;

import java.util.function.Supplier;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.genericreaction.GenericReactant;
import com.petrolpark.destroy.chemistry.genericreaction.SingleGroupGenericReaction;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.group.ChlorideGroup;

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
        return reactionBuilder()
            .addReactant(reactantMolecule)
            .addReactant(DestroyMolecules.HYDROXIDE, 1, chlorideGroup.getDegree() == 3 ? 0 : 1) //if this is a tertiary chloride, the mechanism is SN1 so hydroxide does not appear in the rate equation
            .addProduct(productMolecule)
            .addProduct(DestroyMolecules.CHLORIDE)
            .preexponentialFactor(1e6f * (float)Math.pow(10, chlorideGroup.getDegree()) * reactantMolecule.getCarbocationStability(chlorideGroup.getCarbon(), false))
            .activationEnergy(100f)
            .build();
    };
    
}
