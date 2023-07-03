package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

public final class DestroyMolecules {

    public static final Molecule 
    
    ACETAMIDE = builder()
        .id("acetamide")
        .structure(Formula.deserialize("destroy:linear:CC(=O)N"))
        .boilingPoint(221)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OH+4.756"))
        .boilingPoint(118)
        .build(),

    ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC(=O)C"))
        .boilingPoint(140)
        .build(),

    ACETONE = builder()
        .id("acetone")
        .structure(Formula.deserialize("destroy:linear:CC(=O)C"))
        .boilingPoint(56)
        .build(),

    ACRYLONITRILE = builder()
        .id("acrylonitrile")
        .structure(Formula.deserialize("destroy:linear:C=CC#N"))
        .boilingPoint(77)
        .build(),

    ADIPIC_ACID = builder()
        .id("adipic_acid")
        .structure(Formula.deserialize("destroy:linear:O=C(OH+4.43f)CCCCC(=O)OH+5.41f"))
        .boilingPoint(338)
        .build(),

    ADIPONITRILE = builder()
        .id("adiponitrile")
        .structure(Formula.deserialize("destroy:linear:N#CCCCCC#N"))
        .boilingPoint(295)
        .build(),

    AIBN = builder()
        .id("aibn")
        .structure(Formula.deserialize("destroy:linear:CC(C)(C#N)N=NC(C)(C#N)C"))
        .boilingPoint(10000)
        .build(),

    AMMONIA = builder()
        .id("ammonia")
        .structure(Formula.deserialize("destroy:linear:N"))
        .boilingPoint(-33)
        .build(),

    AMMONIUM = builder()
        .id("ammonium")
        .structure(Formula.atom(Element.NITROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .charge(1)
        .build(),

    // ASPIRIN = builder()
    //     .id("aspirin")
    //     .structure(Formula.deserialize("destroy:benzene:OC(=O)C,C(=O)OH+3.5f,,,,"))
    //     .boilingPoint(140)
    //     .build(),

    // BENZYL_CHLORIDE = builder()
    //     .id("benzyl_chloride")
    //     .structure(Formula.deserialize("destroy:benzene:CCl,,,,,"))
    //     .boilingPoint(179)
    //     .build(),

    CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-140)
        .build(),

    CARBON_TETRACHLORIDE = builder()
        .id("carbon_tetrachloride")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)(Cl)Cl"))
        .boilingPoint(77)
        .build(),

    CHLORIDE = builder()
        .id("chloride")
        .structure(Formula.atom(Element.CHLORINE))
        .charge(-1)
        .build(),

    CHLORINE = builder()
        .id("chlorine")
        .structure(Formula.deserialize("destroy:linear:ClCl"))
        .boilingPoint(-34)
        .build(),

    CHLOROAURATE = builder()
        .id("chloroaurate")
        .structure(Formula.deserialize("destroy:linear:ClAu(Cl)(Cl)Cl"))
        .color(0x7FEDCA4A)
        .charge(-1)
        .build(),

    CHLORODIFLUOROMETHANE = builder()
        .id("chlorodifluoromethane")
        .structure(Formula.deserialize("destroy:linear:ClC(F)F"))
        .boilingPoint(-41)
        .build(),

    CHLOROFORM = builder()
        .id("chloroform")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)Cl"))
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

    CUBANE = builder()
        .id("cubane")
        .structure(Formula.deserialize("destroy:cubane:,"))
        .boilingPoint(134)
        .density(1290f)
        .build(),

    ETHANOL = builder()
        .id("ethanol")
        .structure(Formula.deserialize("destroy:linear:CCO"))
        .boilingPoint(78)
        .build(),

    GENERIC_ACID_ANHYDRIDE = builder()
        .id("generic_acid_anhydride")
        .structure(Formula.deserialize("destroy:linear:RC(=O)OC(=O)R"))
        .hypothetical()
        .translationKey("acid_anhydride")
        .build(),

    GENERIC_ACYL_CHLORIDE = builder()
        .id("generic_acyl_chloride")
        .structure(Formula.deserialize("destroy:linear:RC(=O)Cl"))
        .hypothetical()
        .translationKey("acyl_chloride")
        .build(),

    GENERIC_ALCOHOL = builder()
        .id("generic_alcohol")
        .structure(Formula.deserialize("destroy:linear:RO"))
        .hypothetical()
        .translationKey("alcohol")
        .build(),

    GENERIC_ALKENE = builder()
        .id("generic_alkene")
        .structure(Formula.deserialize("destroy:linear:RC=(R)C(R)R"))
        .hypothetical()
        .translationKey("alkene")
        .build(),

    GENERIC_AMIDE = builder()
        .id("generic_amide")
        .structure(Formula.deserialize("destroy:linear:RC(=O)N(R)R"))
        .hypothetical()
        .translationKey("amide")
        .build(),
    
    GENERIC_AMINE = builder()
        .id("generic_amine")
        .structure(Formula.deserialize("destroy:linear:RN"))
        .hypothetical()
        .translationKey("amine")
        .build(),

    GENERIC_CARBONYL = builder()
        .id("generic_carbonyl")
        .structure(Formula.deserialize("destroy:linear:RC=(R)O"))
        .hypothetical()
        .translationKey("carbonyl")
        .build(),

    GENERIC_CARBOXYLIC_ACID = builder()
        .id("generic_carboxylic_acid")
        .structure(Formula.deserialize("destroy:linear:RC(=O)O"))
        .hypothetical()
        .translationKey("carboxylic_acid")
        .build(),
    
    GENERIC_CHLORIDE = builder()
        .id("generic_chloride")
        .structure(Formula.deserialize("destroy:linear:RCl"))
        .hypothetical()
        .translationKey("chloride")
        .build(),

    GENERIC_ESTER = builder()
        .id("generic_ester")
        .structure(Formula.deserialize("destroy:linear:RC(=O)OR"))
        .hypothetical()
        .translationKey("ester")
        .build(),

    GENERIC_NITRILE = builder()
        .id("generic_nitrile")
        .structure(Formula.deserialize("destroy:linear:R#N"))
        .hypothetical()
        .translationKey("nitrile")
        .build(),

    GLYCEROL = builder()
        .id("glycerol")
        .structure(Formula.deserialize("destroy:linear:OCC(O)CO"))
        .build(),

    HYDROCHLORIC_ACID = builder()
        .id("hydrochloric_acid")
        .structure(Formula.deserialize("destroy:linear:ClH+-5.9"))
        .build(), // TODO physiucal properties

    HYDROFLUORIC_ACID = builder()
        .id("hydrofluoric_acid")
        .structure(Formula.deserialize("destroy:linear:FH+3.17"))
        .density(1.15f)
        .boilingPoint(19.5f)
        .build(),

    HYDROXIDE = builder()
        .id("hydroxide")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN))
        .charge(-1)
        .build(),

    MERCURY = builder()
        .id("mercury")
        .structure(Formula.atom(Element.MERCURY))
        .density(13534f)
        .color(0xFFB3B3B3)
        .build(),

    METHANOL = builder()
        .id("methanol")
        .structure(Formula.deserialize("destroy:linear:CO"))
        .boilingPoint(65)
        .build(),

    METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC"))
        .boilingPoint(57)
        .build(),

    NITRATE = builder()
        .id("nitrate")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
        ).charge(-1)
        .build(),

    SODIUM_ION = builder()
        .id("sodium_ion")
        .translationKey("sodium")
        .structure(Formula.atom(Element.SODIUM))
        .charge(1)
        .build(),
    
    TRINITROTOLUENE = builder()
        .id("tnt")
        .structure(Formula.atom(Element.CARBON)) //TODO actual structure
        .charge(1)
        .build(),

    TETRAFLUOROETHENE = builder()
        .id("tetrafluoroethene")
        .structure(Formula.deserialize("destroy:linear:FC=(F)C(F)F"))
        .build(),

    WATER = builder()
        .id("water")
        .structure(Formula.deserialize("destroy:linear:O"))
        .tag(Tags.SOLVENT)
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

    public static class Tags {

        /**
         * This Molecule will cause damage to Players exposed to it.
         */
        public static final MoleculeTag ACUTELY_TOXIC = new MoleculeTag();

        /**
         * This Molecule cannot partake in Reactions.
         */
        public static final MoleculeTag HYPOTHETICAL = new MoleculeTag();

        /**
         * This Molecule is ignored when displaying the written contents of a Mixture, and ignored when used in Recipes.
         */
        public static final MoleculeTag SOLVENT = new MoleculeTag();
    };

    public static void register() {};
};
