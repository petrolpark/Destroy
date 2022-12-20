package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.Reaction;

import net.minecraft.network.chat.Component;

public class DestroyReactions {

    public static final Reaction ACETIC_ANHYDRIDE_HYDROLYSIS = Reaction.builder()
        .addReactant(DestroyMolecules.ACETIC_ANHYDRIDE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.ACETIC_ACID, 2)
        .build();

    public static final Reaction METHYL_ACETATE_CARBONYLATION = Reaction.builder()
        .addReactant(DestroyMolecules.METHANOL)
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .name(Component.literal("Methyl Acetate Carboxylation"))
        .build();

    public static final Reaction METHYL_ACETATE_ACID_HYDROLYSIS = Reaction.builder()
        .addReactant(DestroyMolecules.METHYL_ACETATE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.METHANOL)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .name(Component.literal("Methyl Acetate Acid Hydrolysis"))
        .build();

    public static final Reaction HYDROXIDE_NEUTRALIZATION = Reaction.builder()
        .addReactant(DestroyMolecules.HYDROXIDE)
        .addReactant(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER)
        .activationEnergy(0f)
        .preexponentialFactor(1e14f)
        .name(Component.literal("Hydroxide Neutralization"))
        .build();

    public static void register() {};
}
