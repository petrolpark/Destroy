package com.petrolpark.destroy;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;

public class Test {

    public static void main(String args[]) {

        DestroyGroupFinder.register();
        DestroyMolecules.register();
        //DestroyReactions.register();

        Molecule myMolecule = builder().structure(Formula.deserialize("linear:O=CC(=O)Cl")).build();
        System.out.println(myMolecule.getSerlializedChemicalFormula());
        for (Group group : myMolecule.getFunctionalGroups()) {
            System.out.println(group.getExampleMolecule().getName());
        };
        
    };

    private static MoleculeBuilder builder() {
        return new MoleculeBuilder("henry");
    };
}
