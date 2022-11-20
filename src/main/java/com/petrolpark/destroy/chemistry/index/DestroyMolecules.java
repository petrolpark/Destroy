package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Bond.BondType;

import net.minecraft.network.chat.Component;

public class DestroyMolecules {
    
    public static Molecule ACETAMIDE = Molecule.builder()
        .structure(Formula.deserialize("linear:CC(=O)N"))
        .boilingPoint(221)
        .translationKey("acetamide")
        .build();

    public static Molecule ACETIC_ACID = Molecule.builder()
        .structure(Formula.deserialize("linear:CC(=O)H+4.756"))
        .boilingPoint(118)
        .translationKey("acetic_acid")
        .build();

    public static Molecule ACETIC_ANHYDRIDE = Molecule.builder()
        .structure(Formula.deserialize("linear:CC(=O)OC(=O)C"))
        .boilingPoint(140)
        .translationKey("acetic_anhydride")
        .build();

    public static Molecule CARBON_MONOXIDE = Molecule.builder()
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-140)
        .translationKey("carbon_monoxide")
        .build();

    public static Molecule METHANOL = Molecule.builder()
        .structure(Formula.deserialize("linear:CO"))
        .boilingPoint(65)
        .translationKey("methanol")
        .build();

    public static Molecule METHYL_ACETATE = Molecule.builder()
        .structure(Formula.deserialize("linear:CC(=O)OC"))
        .boilingPoint(57)
        .translationKey("methyl_acetate")
        .build();

    public static Molecule WATER = Molecule.builder()
        .structure(Formula.deserialize("linear:O"))
        .boilingPoint(100)
        .translationKey("water")
        .build();
};
