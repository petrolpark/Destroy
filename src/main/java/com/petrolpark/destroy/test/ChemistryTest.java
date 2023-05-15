package com.petrolpark.destroy.test;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;

public class ChemistryTest {

    public static void main(String ...args) {

        DestroyReactions.register();
        Mixture mixture1 = new Mixture();
        mixture1.addMolecule(DestroyMolecules.METHANOL, 1f);
        mixture1.addMolecule(DestroyMolecules.CARBON_MONOXIDE, 1f);
        mixture1.reactInBasin();
        System.out.println(mixture1.getContentsString());
    };
};
