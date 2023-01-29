package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

public class DestroyMolecules {

    public static final Molecule 
    
    ACETAMIDE = builder()
        .id("acetamide")
        .structure(Formula.deserialize("linear:CC(=O)N"))
        .boilingPoint(221)
        .tag("AcutelyToxic")
        .build(),

    ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(Formula.deserialize("linear:CC(=O)H+4.756"))
        .boilingPoint(118)
        .build(),

    ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(Formula.deserialize("linear:CC(=O)OC(=O)C"))
        .boilingPoint(140)
        .build(),

    ACETONE = builder()
        .id("acetone")
        .structure(Formula.deserialize("linear:CC(=O)C"))
        .boilingPoint(56)
        .build(),

    ACRYLONITRILE = builder()
        .id("acrylonitrile")
        .structure(Formula.deserialize("linear:C=CC#N"))
        .boilingPoint(77)
        .build(),

    ADIPIC_ACID = builder()
        .id("adipic_acid")
        .structure(Formula.deserialize("linear:O=C(OH+4.43f)CCCCC(=O)OH+5.41f"))
        .boilingPoint(338)
        .build(),

    ADIPONITRILE = builder()
        .id("adiponitrile")
        .structure(Formula.deserialize("linear:N#CCCCCC#N"))
        .boilingPoint(295)
        .build(),

    AIBN = builder()
        .id("aibn")
        .structure(Formula.deserialize("linear:CC(C)(C#N)N=NC(C)(C#N)C"))
        .boilingPoint(10000)
        .build(),

    AMMONIA = builder()
        .id("ammonia")
        .structure(Formula.deserialize("linear:N"))
        .boilingPoint(-33)
        .build(),

    AMMONIUM = builder()
        .id("ammonium")
        .structure(Formula.atom(Element.NITROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .charge(1)
        .build(),

    ASPIRIN = builder()
        .id("aspirin")
        .structure(Formula.deserialize("benzene:OC(=O)C,C(=O)OH+3.5f,,,,"))
        .boilingPoint(140)
        .build(),

    BENZYL_CHLORIDE = builder()
        .id("benzyl_chloride")
        .structure(Formula.deserialize("benzene:CCl,,,,,"))
        .boilingPoint(179)
        .build(),

    CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-140)
        .build(),

    CARBON_TETRACHLORIDE = builder()
        .id("carbon_tetrachloride")
        .structure(Formula.deserialize("linear:ClC(Cl)(Cl)Cl"))
        .boilingPoint(77)
        .build(),

    CHLORIDE = builder()
        .id("chloride")
        .structure(Formula.atom(Element.CHLORINE))
        .charge(-1)
        .build(),

    CHLORINE = builder()
        .id("chlorine")
        .structure(Formula.deserialize("linear:ClCl"))
        .boilingPoint(-34)
        .build(),

    CHLOROAURATE = builder()
        .id("chloroaurate")
        .structure(Formula.deserialize("linear:ClAu(Cl)(Cl)Cl"))
        .charge(-1)
        .build(),

    CHLORODIFLUOROMETHANE = builder()
        .id("chlorodifluoromethane")
        .structure(Formula.deserialize("linear:ClC(F)F"))
        .boilingPoint(-41)
        .build(),

    CHLOROFORM = builder()
        .id("chloroform")
        .structure(Formula.deserialize("linear:ClC(Cl)Cl"))
        .boilingPoint(61)
        .build(),

    CISPLATIN = builder()
        .id("cisplatin")
        .structure(Formula.atom(
            Element.PLATINUM)
            .addAtom(Element.CHLORINE)
            .addAtom(Element.CHLORINE)
            .addGroup(
                Formula.atom(Element.NITROGEN)
                .addAtom(Element.HYDROGEN)
                .addAtom(Element.HYDROGEN)
                .addAtom(Element.HYDROGEN), true
            ).addGroup(
                Formula.atom(Element.NITROGEN)
                .addAtom(Element.HYDROGEN)
                .addAtom(Element.HYDROGEN)
                .addAtom(Element.HYDROGEN), true
            ))
        .boilingPoint(270)
        .build(),

    CYCLOHEXENE = builder()
        .id("cyclohexene")
        .structure(Formula.deserialize("cyclohexene:,,,,,"))
        .boilingPoint(83)
        .build(),

    CYCLOPENTADIENYL = builder()
        .id("cyclopentadienyl")
        .structure(Formula.deserialize("cyclopentadienyl:,,,,"))
        .charge(-1)
        .build(),

    ETHANOL = builder()
        .id("ethanol")
        .structure(Formula.deserialize("linear:CCO"))
        .boilingPoint(78)
        .build(),

    ETHYLANTHRAQUINONE = builder()
        .id("ethylanthraquinone")
        .structure(Formula.deserialize("dihydroanthracene:,CC,,,,,,,=O,=O"))
        .boilingPoint(415)
        .build(),

    GENERIC_ACID_ANHYDRIDE = builder()
        .id("generic_acid_anhydride")
        .structure(Formula.deserialize("linear:RC(=O)OC(=O)R"))
        .hypothetical()
        .translationKey("acid_anhydride")
        .build(),

    GENERIC_ACYL_CHLORIDE = builder()
        .id("generic_acyl_chloride")
        .structure(Formula.deserialize("linear:RC(=O)Cl"))
        .hypothetical()
        .translationKey("acyl_chloride")
        .build(),

    GENERIC_ALCOHOL = builder()
        .id("generic_alcohol")
        .structure(Formula.deserialize("linear:RO"))
        .hypothetical()
        .translationKey("alcohol")
        .build(),

    GENERIC_ALKENE = builder()
        .id("generic_alkene")
        .structure(Formula.deserialize("linear:RC=(R)C(R)R"))
        .hypothetical()
        .translationKey("alkene")
        .build(),

    GENERIC_AMIDE = builder()
        .id("generic_amide")
        .structure(Formula.deserialize("linear:RC(=O)N(R)R"))
        .hypothetical()
        .translationKey("amide")
        .build(),
    
    GENERIC_AMINE = builder()
        .id("generic_amine")
        .structure(Formula.deserialize("linear:RN"))
        .hypothetical()
        .translationKey("amine")
        .build(),

    GENERIC_CARBONYL = builder()
        .id("generic_carbonyl")
        .structure(Formula.deserialize("linear:RC=(R)O"))
        .hypothetical()
        .translationKey("carbonyl")
        .build(),

    GENERIC_CARBOXYLIC_ACID = builder()
        .id("generic_carboxylic_acid")
        .structure(Formula.deserialize("linear:RC(=O)O"))
        .hypothetical()
        .translationKey("carboxylic_acid")
        .build(),
    
    GENERIC_CHLORIDE = builder()
        .id("generic_chloride")
        .structure(Formula.deserialize("linear:RCl"))
        .hypothetical()
        .translationKey("chloride")
        .build(),

    GENERIC_ESTER = builder()
        .id("generic_ester")
        .structure(Formula.deserialize("linear:RC(=O)OR"))
        .hypothetical()
        .translationKey("ester")
        .build(),

    GENERIC_NITRILE = builder()
        .id("generic_nitrile")
        .structure(Formula.deserialize("linear:R#N"))
        .hypothetical()
        .translationKey("nitrile")
        .build(),

    HYDROXIDE = builder()
        .id("hydroxide")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN))
        .charge(-1)
        .build(),

    METHANOL = builder()
        .id("methanol")
        .structure(Formula.deserialize("linear:CO"))
        .boilingPoint(65)
        .build(),

    METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(Formula.deserialize("linear:CC(=O)OC"))
        .boilingPoint(57)
        .build(),

    NITRATE = builder()
        .id("nitrate")
        .structure(Formula.atom(Element.NITROGEN).addAtom(Element.OXYGEN, BondType.DOUBLE).addAtom(Element.OXYGEN, BondType.AROMATIC).addAtom(Element.OXYGEN, BondType.AROMATIC))
        .charge(-1)
        .build(),

    WATER = builder()
        .id("water")
        .structure(Formula.deserialize("linear:O"))
        .boilingPoint(100)
        .build(),

    PROTON = builder()
        .id("proton")
        .structure(Formula.atom(Element.HYDROGEN))
        .charge(1)
        .build();

    private static final MoleculeBuilder builder() {
        return new MoleculeBuilder(Destroy.MOD_ID);
    };

    public static void register() {};
};
