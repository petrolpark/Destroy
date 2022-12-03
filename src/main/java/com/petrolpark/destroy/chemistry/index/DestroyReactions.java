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
        .rateConstant(0.01f)
        .name(Component.literal("Methyl Acetate Carboxylation"))
        .build();

    public static Reaction METHYL_ACETATE_ACID_HYDROLYSIS = Reaction.builder()
        .addReactant(DestroyMolecules.METHYL_ACETATE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.METHANOL)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .name(Component.literal("Methyl Acetate Acid Hydrolysis"))
        .build();

    public static void register() {};
}
