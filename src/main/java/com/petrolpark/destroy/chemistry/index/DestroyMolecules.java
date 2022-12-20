package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

public class DestroyMolecules {

    public static final Molecule ACETAMIDE = builder()
        .id("acetamide")
        .structure(Formula.deserialize("linear:CC(=O)N"))
        .boilingPoint(221)
        .build();

    public static final Molecule ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(Formula.deserialize("linear:CC(=O)H+4.756"))
        .boilingPoint(118)
        .build();

    public static final Molecule ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(Formula.deserialize("linear:CC(=O)OC(=O)C"))
        .boilingPoint(140)
        .build();

    public static final Molecule CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-140)
        .build();

    public static final Molecule CHLORIDE = builder()
        .id("chloride")
        .structure(Formula.atom(Element.CHLORINE))
        .charge(-1)
        .build();

    public static final Molecule ETHANOL = builder()
        .id("ethanol")
        .structure(Formula.deserialize("linear:CCO"))
        .build();

    public static final Molecule GENERIC_ACID_ANHYDRIDE = builder()
        .id("generic_acid_anhydride")
        .structure(Formula.deserialize("linear:RC(=O)OC(=O)R"))
        .hypothetical()
        .translationKey("acid_anhydride")
        .build();

    public static final Molecule GENERIC_ACYL_CHLORIDE = builder()
        .id("generic_acyl_chloride")
        .structure(Formula.deserialize("linear:RC(=O)Cl"))
        .hypothetical()
        .translationKey("acyl_chloride")
        .build();

    public static final Molecule GENERIC_ALCOHOL = builder()
        .id("generic_alcohol")
        .structure(Formula.deserialize("linear:RO"))
        .hypothetical()
        .translationKey("alcohol")
        .build();

    public static final Molecule GENERIC_ALKENE = builder()
        .id("generic_alkene")
        .structure(Formula.deserialize("linear:RC=(R)C(R)R"))
        .hypothetical()
        .translationKey("alkene")
        .build();

    public static final Molecule GENERIC_AMIDE = builder()
        .id("generic_amide")
        .structure(Formula.deserialize("linear:RC(=O)N(R)R"))
        .hypothetical()
        .translationKey("amide")
        .build();
    
    public static final Molecule GENERIC_AMINE = builder()
        .id("generic_amine")
        .structure(Formula.deserialize("linear:RN"))
        .hypothetical()
        .translationKey("amine")
        .build();

    public static final Molecule GENERIC_CARBONYL = builder()
        .id("generic_carbonyl")
        .structure(Formula.deserialize("linear:RC=(R)O"))
        .hypothetical()
        .translationKey("carbonyl")
        .build();

    public static final Molecule GENERIC_CARBOXYLIC_ACID = builder()
        .id("generic_carboxylic_acid")
        .structure(Formula.deserialize("linear:RC(=O)O"))
        .hypothetical()
        .translationKey("carboxylic_acid")
        .build();
    
    public static final Molecule GENERIC_CHLORIDE = builder()
        .id("generic_chloride")
        .structure(Formula.deserialize("linear:RCl"))
        .hypothetical()
        .translationKey("chloride")
        .build();

    public static final Molecule GENERIC_ESTER = builder()
        .id("generic_ester")
        .structure(Formula.deserialize("linear:RC(=O)OR"))
        .hypothetical()
        .translationKey("ester")
        .build();

    public static final Molecule GENERIC_NITRILE = builder()
        .id("generic_nitrile")
        .structure(Formula.deserialize("linear:R#N"))
        .hypothetical()
        .translationKey("nitrile")
        .build();

    public static final Molecule HYDROXIDE = builder()
        .id("hydroxide")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN))
        .charge(-1)
        .build();

    public static final Molecule METHANOL = builder()
        .id("methanol")
        .structure(Formula.deserialize("linear:CO"))
        .boilingPoint(65)
        .build();

    public static final Molecule METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(Formula.deserialize("linear:CC(=O)OC"))
        .boilingPoint(57)
        .build();

    public static final Molecule WATER = builder()
        .id("water")
        .structure(Formula.deserialize("linear:O"))
        .boilingPoint(100)
        .build();

    public static final Molecule PROTON = builder()
        .id("proton")
        .structure(Formula.atom(Element.HYDROGEN))
        .charge(1)
        .build();

    private static final MoleculeBuilder builder() {
        return new MoleculeBuilder(Destroy.MOD_ID);
    };

    public static void register() {};
};
