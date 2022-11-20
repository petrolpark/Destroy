package com.petrolpark.destroy;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;

public class Main {

    DestroyReactions pee = new DestroyReactions();    

    public static void main(String args[]) {
        Mixture myMixture = new Mixture();
        myMixture.addMolecule(DestroyMolecules.METHANOL, 1.0f).addMolecule(DestroyMolecules.CARBON_MONOXIDE, 1.0f);
        myMixture.pee();
    };
}
