package com.petrolpark.destroy;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;

/**
 * Used during development for testing aspects of the Chemistry system without having to run the whole game.
 */
public class Test {

    public static void main(String args[]) {

        DestroyGroupFinder.register();
        DestroyMolecules.register();
        DestroyReactions.register();
        DestroyGenericReactions.register();

        Molecule myMolecule = builder()
            .structure(Formula.deserialize("linear:C(C)C"))
            .build();

        //System.out.println(DestroyMolecules.ASPIRIN.getStructuralFormula());
        System.out.println(DestroyMolecules.ASPIRIN.getSerlializedChemicalFormula());
        
    };

    private static MoleculeBuilder builder() {
        return new MoleculeBuilder("novel");
    };
}
