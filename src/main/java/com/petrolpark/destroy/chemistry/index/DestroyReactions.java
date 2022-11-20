package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Reaction;

import net.minecraft.network.chat.Component;

public class DestroyReactions {

    public static Reaction ACETIC_ANHYDRIDE_HYDROLYSIS = Reaction.builder()
        .addReactant(DestroyMolecules.ACETIC_ANHYDRIDE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.ACETIC_ACID, 2)
        .build();

    public static Reaction METHYL_ACETATE_CARBONYLATION = Reaction.builder()
        .addReactant(DestroyMolecules.METHANOL)
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .name(Component.literal("Methyl Acetate Carboxylation"))
        .build();

    // public static Reaction METHYL_ACETATE_HYDROLYSIS = Reaction.builder()
    //     .addReactant(DestroyMolecules.METHYL_ACETATE)
    //     .addReactant(DestroyMolecules.WATER)
    //     .name(Component.literal("Methyl Acetate Hydrolysis"))
    //     .build();

    public static void register() {};

    public static void main(String args[]) {
        Mixture myMixture = new Mixture();
        myMixture.addMolecule(DestroyMolecules.METHANOL, 1.0f).addMolecule(DestroyMolecules.CARBON_MONOXIDE, 1.0f);
        myMixture.pee();
    };
}
