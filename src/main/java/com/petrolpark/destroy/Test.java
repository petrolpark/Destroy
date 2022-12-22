package com.petrolpark.destroy;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;

public class Test {

    public static void main(String args[]) {

        DestroyGroupFinder.register();
        DestroyMolecules.register();
        DestroyReactions.register();
        DestroyGenericReactions.register();

        Molecule myMolecule = builder()
            .structure(Formula.deserialize("linear:CCl"))
            .id("test_molecule")
            .build();

        Mixture myMixture = new Mixture();
        myMixture
            .addMolecule(myMolecule, 1f)
            .addMolecule(DestroyMolecules.HYDROXIDE, 1f)
            .react()
            ;
        myMixture.pee();
        
    };

    private static MoleculeBuilder builder() {
        return new MoleculeBuilder("henry");
    };
}
