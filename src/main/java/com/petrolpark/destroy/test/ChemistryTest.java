package com.petrolpark.destroy.test;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
//import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.chemistry.index.DestroyTopologies;

public class ChemistryTest {

    public static void main(String ...args) {

        DestroyTopologies.register();
        DestroyMolecules.register();
        
        MoleculeBuilder builder = new MoleculeBuilder("test");
        Molecule HENRYANE = builder.id("henryane")
            .structure(Formula.deserialize("destroy:benzene:C,C,C,C,C,C"))
            .build();
        
        System.out.println(HENRYANE.getMass());
        System.out.println(HENRYANE.getStructuralFormula());
        
    };
};
