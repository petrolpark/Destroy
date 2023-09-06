package com.petrolpark.destroy.test;

import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
//import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.chemistry.index.DestroyTopologies;

public class ChemistryTest {

    public static void main(String ...args) {

        DestroyTopologies.register();
        DestroyMolecules.register();
        
        System.out.println(Formula.deserialize("destroy:linear:CCC(=C)C").serialize());
        System.out.println(Formula.deserialize("destroy:linear:O=C(C)C").serialize());
        System.out.println(Formula.deserialize("destroy:linear:C(=O)(C)C").serialize());
        
    };
};
