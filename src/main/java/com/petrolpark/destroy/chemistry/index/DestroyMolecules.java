package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Formula;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.MoleculeTag;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.Molecule.MoleculeBuilder;

import net.minecraft.ChatFormatting;

public final class DestroyMolecules {

    public static final Molecule 
    
    ACETAMIDE = builder()
        .id("acetamide")
        .structure(Formula.deserialize("destroy:linear:CC(=O)N"))
        .boilingPoint(221.2f)
        .density(1159f)
        .molarHeatCapacity(91.3f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.PLASTICIZER)
        .tag(Tags.SMOG)
        .build(),

    ACETATE = builder()
        .id("acetate")
        .structure(Formula.atom(Element.CARBON)
            .addAtom(Element.HYDROGEN)
            .addAtom(Element.HYDROGEN)
            .addAtom(Element.HYDROGEN)
            .addGroup(Formula.atom(Element.CARBON)
                .addGroup(Formula.atom(Element.OXYGEN), true, BondType.DOUBLE)
                .addAtom(Element.OXYGEN)
            , false)
        ).charge(-1)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OH"))
        .boilingPoint(118.5f)
        .density(1049f)
        .molarHeatCapacity(123.1f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC(=O)C"))
        .boilingPoint(140f)
        .density(1082f)
        // Molar heat capacity unknown
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETONE = builder()
        .id("acetone")
        .structure(Formula.deserialize("destroy:linear:CC(=O)C"))
        .boilingPoint(56.08f)
        .density(784.5f)
        .molarHeatCapacity(126.3f)
        .tag(Tags.SOLVENT)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETONE_CYANOHYDRIN = builder()
        .id("acetone_cyanohydrin")
        .structure(Formula.deserialize("destroy:linear:CC(OH)(C#N)C"))
        .boilingPoint(95f)
        .density(932f)
        .molarHeatCapacity(160f) // Estimate based on similar compounds
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    ACRYLONITRILE = builder()
        .id("acrylonitrile")
        .structure(Formula.deserialize("destroy:linear:C=CC#N"))
        .boilingPoint(77f)
        .density(810f)
        .molarHeatCapacity(113f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ADIPIC_ACID = builder()
        .id("adipic_acid")
        .structure(Formula.deserialize("destroy:linear:O=C(OH)CCCCC(=O)OH"))
        .boilingPoint(337.5f)
        .density(1360f)
        .molarHeatCapacity(196.5f)
        .tag(Tags.SMOG)
        .build(),

    ADIPONITRILE = builder()
        .id("adiponitrile")
        .structure(Formula.deserialize("destroy:linear:N#CCCCCC#N"))
        .boilingPoint(295.1f)
        .density(951f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    AIBN = builder()
        .id("aibn")
        .structure(Formula.deserialize("destroy:linear:CC(C)(C#N)N=NC(C)(C#N)C"))
        .boilingPoint(10000) // Doesn't boil - decomposes
        .density(1100f)
        .tag(Tags.SMOG)
        .build(),

    AMMONIA = builder()
        .id("ammonia")
        .structure(Formula.deserialize("destroy:linear:N"))
        .boilingPoint(-33.34f)
        .density(900f) // Ammonium hydroxide has a density of ~0.9gcm^-3
        .molarHeatCapacity(80f)
        .tag(Tags.REFRIGERANT)
        .tag(Tags.SMELLY)
        .build(),

    AMMONIUM = builder()
        .id("ammonium")
        .structure(Formula.atom(Element.NITROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .charge(1)
        .build(),

    ASPIRIN = builder()
        .id("aspirin")
        .structure(Formula.deserialize("destroy:benzene:OC(=O)C,C(=O)O,,,,"))
        .boilingPoint(140f)
        .density(1400f)
        // Couldn't find heat capacity
        .tag(Tags.SMOG)
        .build(),

    BENZENE = builder()
        .id("benzene")
        .structure(Formula.deserialize("destroy:benzene:,,,,"))
        .boilingPoint(80.1f)
        .density(876.5f)
        .molarHeatCapacity(134.8f)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    BENZYL_CHLORIDE = builder()
        .id("benzyl_chloride")
        .structure(Formula.deserialize("destroy:benzene:CCl,,,,,"))
        .boilingPoint(179f)
        .density(1100f)
        .molarHeatCapacity(182.4f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMOG)
        .build(),

    BUTADIENE = builder()
        .id("butadiene")
        .structure(Formula.deserialize("destroy:linear:C=CC=C"))
        .boilingPoint(-4.41f)
        .density(614.9f)
        .molarHeatCapacity(123.65f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    CALCIUM_ION = builder()
        .id("calcium_ion")
        .translationKey("calcium")
        .structure(Formula.atom(Element.CALCIUM))
        .charge(2)
        .build(),

    CARBON_DIOXIDE = builder()
        .id("carbon_dioxide")
        .structure(Formula.deserialize("destroy:linear:O=C=O"))
        .boilingPoint(-78.4645f) // Sublimes, doesn't "boil"
        .density(1.977f) // Gas density
        .molarHeatCapacity(37.135f)
        .tag(Tags.GREENHOUSE)
        .build(),

    CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-191.5f)
        .density(789f) // Liquid density; gas density is the same order of magnitude
        .molarHeatCapacity(29.1f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .build(),

    CARBON_TETRACHLORIDE = builder()
        .id("carbon_tetrachloride")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)(Cl)Cl"))
        .boilingPoint(76.72f)
        .density(1586.7f)
        .molarHeatCapacity(132.6f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    CHLORIDE = builder()
        .id("chloride")
        .structure(Formula.atom(Element.CHLORINE))
        .charge(-1)
        .build(),

    CHLORINE = builder()
        .id("chlorine")
        .structure(Formula.deserialize("destroy:linear:ClCl"))
        .color(0x20F9FCC2)
        .boilingPoint(-34.04f)
        .density(3.2f) // Gas density
        .molarHeatCapacity(33.949f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMELLY)
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
        .boilingPoint(-40.7f)
        .density(3.66f) // Gas density
        .molarHeatCapacity(112.6f)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.REFRIGERANT)
        .build(),

    CHLOROFORM = builder()
        .id("chloroform")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)Cl"))
        .boilingPoint(61.15f)
        .density(1489f)
        .molarHeatCapacity(114.25f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SOLVENT)
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
        .boilingPoint(270f)
        .density(3740f)
        .build(),

    COPPER_I = builder()
        .id("copper_i")
        .structure(Formula.atom(Element.COPPER))
        .color(0xE0D30823)
        .charge(1)
        .build(),

    COPPER_II = builder()
        .id("copper_ii")
        .structure(Formula.atom(Element.COPPER))
        .color(0xE00FFCA1)
        .charge(2)
        .build(),

    CUBANE = builder()
        .id("cubane")
        .structure(Formula.deserialize("destroy:cubane:,,,,,,"))
        .boilingPoint(161.6f)
        .density(1290f)
        // Specific heat capacity unknown
        .tag(Tags.SMOG)
        .build(),

    CUBANEDICARBOXYLIC_ACID = builder()
        .id("cubanedicarboxylic_acid")
        .structure(Formula.deserialize("destroy:cubane:C(=O)OH,,,,,,C(=O)OH"))
        .boilingPoint(457.4f)
        .density(2400f)
        .tag(Tags.SMOG)
        .build(),

    CYANIDE = builder()
        .id("cyanide")
        .structure(Formula.atom(Element.CARBON)
            .addAtom(Element.NITROGEN, BondType.TRIPLE)
        ).charge(-1)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    CYCLOHEXENE = builder()
        .id("cyclohexene")
        .structure(Formula.deserialize("destroy:cyclohexene:,,,,,,,,,"))
        .boilingPoint(82.98f)
        .density(811f)
        .molarHeatCapacity(152.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    // TODO cyclopentadiene

    CYCLOPENTADIENIDE = builder()
        .id("cyclopentadienide")
        .structure(Formula.deserialize("destroy:cyclopentadienide:,,,,"))
        .charge(-1)
        .tag(Tags.SMOG)
        .build(),

    DICHLORODIFLUOROMETHANE = builder()
        .id("dichlorodifluoromethane")
        .structure(Formula.deserialize("destroy:linear:ClC(F)(F)Cl"))
        .boilingPoint(-28.9f)
        .density(1486f)
        .molarHeatCapacity(126.8f) //TODO possibly increase this to make R-12 a more viable refrigerant
        .tag(Tags.GREENHOUSE)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.REFRIGERANT)
        .build(),

    ETHANOL = builder()
        .id("ethanol")
        .structure(Formula.deserialize("destroy:linear:CCO"))
        .boilingPoint(78.23f)
        .density(789.45f)
        .molarHeatCapacity(109f)
        .tag(Tags.SOLVENT)
        .tag(Tags.SMOG)
        .build(),

    ETHENE = builder()
        .id("ethene")
        .structure(Formula.deserialize("destroy:linear:C=C"))
        .boilingPoint(-103.7f)
        .density(567.9f)
        .molarHeatCapacity(67.3f)
        .tag(Tags.SMOG)
        .build(),

    ETHYLANTHRAQUINONE = builder()
        .id("ethylanthraquinone")
        .structure(Formula.deserialize("destroy:anthraquinone:,,,O,,,CC,,O,"))
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f) // Not accurate, just based off an isomer
        .tag(Tags.SMOG)
        .build(),

    ETHYLANTHRAHYDROQUINONE = builder()
        .id("ethylanthrahydroquinone")
        .structure(Formula.deserialize("destroy:anthracene:,,,O,,,CC,,O,"))
        // No data found so using same as ethylanthraquinone
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f)
        .tag(Tags.SMOG)
        .build(),
    
    ETHYLBENZENE = builder()
        .id("ethylbenzene")
        .structure(Formula.deserialize("destroy:benzene:CC,,,,,"))
        .boilingPoint(136f)
        .density(866.5f)
        .specificHeatCapacity(1726f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),
    
    FLUORIDE = builder()
        .id("fluoride")
        .structure(Formula.atom(Element.FLUORINE))
        .charge(-1)
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
        .structure(Formula.deserialize("destroy:linear:RC(R)(R)O"))
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
        .structure(Formula.deserialize("destroy:linear:RC(=O)N"))
        .hypothetical()
        .translationKey("amide")
        .build(),
    
    GENERIC_AMINE = builder()
        .id("generic_amine")
        .structure(Formula.deserialize("destroy:linear:RC(R)(R)N"))
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
        .structure(Formula.deserialize("destroy:linear:RC(R)(R)Cl"))
        .hypothetical()
        .translationKey("organic_chloride")
        .build(),

    GENERIC_ESTER = builder()
        .id("generic_ester")
        .structure(Formula.deserialize("destroy:linear:RC(=O)OR"))
        .hypothetical()
        .translationKey("ester")
        .build(),

    GENERIC_NITRILE = builder()
        .id("generic_nitrile")
        .structure(Formula.deserialize("destroy:linear:RC(R)(R)C#N"))
        .hypothetical()
        .translationKey("nitrile")
        .build(),

    GENERIC_PRIMARY_AMINE = builder()
        .id("generic_primary_amine")
        .structure(Formula.deserialize("destroy:linear:RC(R)(R)NR"))
        .hypothetical()
        .translationKey("non_tertiary_amine")
        .build(),

    GLYCEROL = builder()
        .id("glycerol")
        .structure(Formula.deserialize("destroy:linear:OCC(O)CO"))
        .boilingPoint(290f)
        .density(1261f)
        .molarHeatCapacity(213.8f)
        .tag(Tags.SMOG)
        .build(),

    HEXANEDIAMINE = builder()
        .id("hexanediamine")
        .structure(Formula.deserialize("destroy:linear:NCCCCCCN"))
        .boilingPoint(204.6f)
        .density(840f)
        .molarHeatCapacity(250f) // Estimate: couldn't find heat capacity
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .build(),

    HYDRAZINE = builder()
        .id("hydrazine")
        .structure(Formula.deserialize("destroy:linear:NN"))
        .boilingPoint(114f)
        .density(1021f)
        .molarHeatCapacity(70f) // Estimate: couldn't find heat capacity
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    HYDROCHLORIC_ACID = builder()
        .id("hydrochloric_acid")
        .structure(Formula.deserialize("destroy:linear:ClH"))
        .boilingPoint(-85.05f)
        .density(1490f)
        .specificHeatCapacity(798.1f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    HYDROFLUORIC_ACID = builder()
        .id("hydrofluoric_acid")
        .structure(Formula.deserialize("destroy:linear:FH"))
        .density(1.15f)
        .boilingPoint(19.5f)
        // Heat capacity unknown
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .build(),

    HYDROGEN = builder()
        .id("hydrogen")
        .structure(Formula.atom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .boilingPointInKelvins(20.271f)
        .density(70.85f)
        .molarHeatCapacity(28.84f)
        .build(),

    HYDROGEN_CYANIDE = builder()
        .id("hydrogen_cyanide")
        .structure(Formula.deserialize("destroy:linear:N#C"))
        .boilingPoint(26f)
        .density(687.6f)
        .molarHeatCapacity(35.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    HYDROGEN_IODIDE = builder()
        .id("hydrogen_iodide")
        .structure(Formula.atom(Element.IODINE).addAtom(Element.HYDROGEN))
        .boilingPoint(-35.36f)
        .density(2850)
        .molarHeatCapacity(29.2f)
        .build(),
    
    HYDROGEN_PEROXIDE = builder()
        .id("hydrogen_peroxide")
        .structure(Formula.deserialize("destroy:linear:OO"))
        .color(0x40C7F4FC)
        .boilingPoint(150.2f)
        .density(1110f)
        .specificHeatCapacity(2619f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.BLEACH)
        .build(),

    HYDROGENSULFATE = builder()
        .id("hydrogensulfate")
        .structure(Formula.atom(Element.SULFUR)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.SINGLE)
            .addGroup(Formula.atom(Element.OXYGEN)
                .addAtom(Element.HYDROGEN)
            )
        ).charge(-1)
        .tag(Tags.ACID_RAIN)
        .build(),

    HYDROXIDE = builder()
        .id("hydroxide")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN))
        .charge(-1)
        .build(),

    HYPOCHLOROUS_ACID = builder()
        .id("hypochlorous_acid")
        .structure(Formula.deserialize("destroy:linear:OCl"))
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.BLEACH)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.BLEACH)
        .build(),

    HYPOCHLORITE = builder()
        .id("hypochlorite")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.CHLORINE))
        .charge(-1)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    IODIDE = builder()
        .id("iodide")
        .structure(Formula.atom(Element.IODINE))
        .charge(-1)
        .build(),

    IODINE = builder()
        .id("iodine")
        .structure(Formula.deserialize("destroy:linear:II"))
        .color(0x80AA16A5)
        .boilingPoint(184.3f)
        .density(4933f)
        .molarHeatCapacity(54.44f)
        .build(),

    IODOMETHANE = builder()
        .id("iodomethane")
        .structure(Formula.deserialize("destroy:linear:CI"))
        .boilingPoint(42.6f)
        .density(2280f)
        .molarHeatCapacity(82.75f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    IRON_II = builder()
        .id("iron_ii")
        .structure(Formula.atom(Element.IRON))
        .color(0x80A9C92A)
        .charge(2)
        .build(),

    IRON_III = builder()
        .id("iron_iii")
        .structure(Formula.atom(Element.IRON))
        .color(0x80F94939)
        .charge(3)
        .build(),

    MERCURY = builder()
        .id("mercury")
        .structure(Formula.atom(Element.MERCURY))
        .boilingPoint(356.73f)
        .density(13534f)
        .molarHeatCapacity(27.98f)
        .color(0xFFB3B3B3)
        .build(),

    METAXYLENE = builder()
        .id("metaxylene")
        .structure(Formula.deserialize("destroy:benzene:C,,C,,,"))
        .boilingPoint(139f)
        .density(860f)
        .molarHeatCapacity(181.5f)
        .tag(Tags.SMOG)
        .build(),

    METHANE = builder()
        .id("methane")
        .structure(Formula.deserialize("destroy:linear:C"))
        .boilingPoint(-161.5f)
        .density(424f)
        .molarHeatCapacity(35.7f)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SMOG)
        .build(),

    METHANOL = builder()
        .id("methanol")
        .structure(Formula.deserialize("destroy:linear:CO"))
        .boilingPoint(65)
        .density(792f)
        .molarHeatCapacity(68.62f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    METHYLAMINE = builder()
        .id("methylamine")
        .structure(Formula.deserialize("destroy:linear:CN"))
        .boilingPoint(-6.3f)
        .density(656.2f)
        .molarHeatCapacity(101.8f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC"))
        .boilingPoint(56.9f)
        .density(932f)
        .molarHeatCapacity(140.2f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    METHYL_SALICYLATE = builder()
        .id("methyl_salicylate")
        .structure(Formula.deserialize("destroy:benzene:C(=O)OC,O,,,,"))
        .boilingPoint(222f)
        .density(1174f)
        .molarHeatCapacity(248.9f)
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),
    
    NICKEL_ION = builder()
        .id("nickel_ion")
        .translationKey("nickel")
        .structure(Formula.atom(Element.NICKEL))
        .color(0xE09BEAAB)
        .charge(2)
        .tag(Tags.CARCINOGEN)
        .build(),

    NITRATE = builder()
        .id("nitrate")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
        ).charge(-1)
        .tag(Tags.ACID_RAIN)
        .build(),

    NITRIC_ACID = builder()
        .id("nitric_acid")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addGroup(Formula.atom(Element.OXYGEN)
                .addAtom(Element.HYDROGEN)
            ) //TODO maybe add color (though this should come from a decomposition)
        ).boilingPoint(83f)
        .density(1510f)
        .molarHeatCapacity(53.29f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    NITROGEN = builder()
        .id("nitrogen")
        .structure(Formula.deserialize("destroy:linear:N#N"))
        .boilingPointInKelvins(77.355f)
        .density(807f)
        .molarHeatCapacity(29.12f)
        .tag(Tags.ABUNDANT_IN_AIR)
        .build(),

    NITROGEN_DIOXIDE = builder()
        .id("nitrogen_dioxide")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
        ).color(0xD089011A)
        .boilingPoint(21.15f)
        .density(1880f)
        .molarHeatCapacity(37.2f)
        .tag(Tags.ACID_RAIN)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .build(),

    NITROGLYCERINE = builder()
        .id("nitroglycerine")
        .structure(Formula.deserialize("destroy:linear:C(ON(~O)(~O))C(ON(~O)(~O))CON(~O)(~O)"))
        .boilingPoint(50f)
        .density(1600f)
        // Heat capacity unknown
        .tag(Tags.SMOG)
        .build(),

    NITRONIUM = builder()
        .id("nitronium")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
        ).charge(1)
        .build(),

    OCTASULFUR = builder()
        .id("octasulfur")
        .translationKey("sulfur")
        .structure(Formula.deserialize("destroy:octasulfur:hello"))
        .color(0xFFD00000)
        .boilingPoint(444.6f) //TODO melting point is 119C
        .density(2070f)
        .molarHeatCapacity(21.64f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    OLEUM = builder()
        .id("oleum")
        .structure(Formula.deserialize("destroy:linear:HOS(=O)(=O)OS(=O)(=O)OH"))
        .boilingPoint(10f)
        .density(1820f) // Estimate
        .specificHeatCapacity(2600f) // Estimate
        .tag(Tags.ACID_RAIN)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    ORTHOXYLENE = builder()
        .id("orthoxylene")
        .structure(Formula.deserialize("destroy:benzene:C,C,,,,"))
        .boilingPoint(144.4f)
        .density(880f)
        .molarHeatCapacity(187.1f)
        .tag(Tags.SMOG)
        .build(),

    OXIDE = builder()
        .id("oxide")
        .structure(Formula.atom(Element.OXYGEN))
        .charge(-2)
        .build(),

    OXYGEN = builder()
        .id("oxygen")
        .structure(Formula.deserialize("destroy:linear:O=O"))
        .boilingPointInKelvins(90.188f)
        .density(1141f)
        .molarHeatCapacity(29.378f)
        .tag(Tags.ABUNDANT_IN_AIR)
        .build(),

    PARAXYLENE = builder()
        .id("paraxylene")
        .structure(Formula.deserialize("destroy:benzene:C,,,C,,"))
        .boilingPoint(138.35f)
        .density(861f)
        .molarHeatCapacity(182f)
        .tag(Tags.SMOG)
        .build(),

    PHENOL = builder()
        .id("phenol")
        .structure(Formula.deserialize("destroy:benzene:O,,,,,"))
        .boilingPoint(181.7f)
        .density(1070f)
        .molarHeatCapacity(220.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    PHENYLACETIC_ACID = builder()
        .id("phenylacetic_acid")
        .structure(Formula.deserialize("destroy:benzene:CC(=O)O"))
        .boilingPoint(265.5f)
        .density(1080.9f)
        .molarHeatCapacity(170f) // Estimate based on isomers
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),

    PHENYLACETONE = builder()
        .id("phenylacetone")
        .structure(Formula.deserialize("destroy:benzene:CC(=O)C"))
        .boilingPoint(215f)
        .density(1006f)
        .molarHeatCapacity(250f) // Estimate based on isomers
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    PHOSGENE = builder()
        .id("phosgene")
        .structure(Formula.deserialize("destroy:linear:ClC(=O)Cl"))
        .boilingPoint(8.3f)
        .density(1432f)
        .molarHeatCapacity(101f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMELLY)
        .build(),
    
    PHTHALIC_ANHYDRIDE = builder()
        .id("phthalic_anhydride")
        .structure(Formula.deserialize("destroy:isohydrobenzofuran:,,,O,O,"))
        .boilingPoint(295f)
        .density(1530f)
        .molarHeatCapacity(161.8f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    PICRIC_ACID = builder()
        .id("picric_acid")
        .structure(Formula.deserialize("destroy:benzene:O,N~(~O)O,C,N~(~O)O,,N~(~O)O"))
        .color(0xC0ED7417)
        .boilingPoint(300f) // Detonates before this
        .density(1763f)
        .molarHeatCapacity(200f) // Estimate, couldn't find heat capacity
        .tag(Tags.SMOG)
        .build(),

    POTASSIUM_ION = builder()
        .id("potassium_ion")
        .translationKey("potassium")
        .structure(Formula.atom(Element.POTASSIUM))
        .charge(1)
        .build(),

    PROPENE = builder()
        .id("propene")
        .structure(Formula.deserialize("destroy:linear:C=CC"))
        .boilingPoint(-47.6f)
        .density(1810f)
        .molarHeatCapacity(102f)
        .tag(Tags.SMOG)
        .build(),

    SALICYLIC_ACID = builder()
        .id("salicylic_acid")
        .structure(Formula.deserialize("destroy:benzene:C(=O)O,O,,,,"))
        .boilingPoint(200f)
        .density(1443f)
        .molarHeatCapacity(159.4f)
        .tag(Tags.SMOG)
        .build(),

    SODIUM_METAL = builder()
        .id("sodium_metal")
        .translationKey("sodium")
        .structure(Formula.atom(Element.SODIUM))
        .color(0xFFB3B3B3)
        .boilingPoint(882.94f)
        .density(968f)
        .molarHeatCapacity(28.23f)
        .build(),

    SODIUM_ION = builder()
        .id("sodium_ion")
        .structure(Formula.atom(Element.SODIUM))
        .charge(1)
        .build(),

    STYRENE = builder()
        .id("styrene")
        .structure(Formula.deserialize("destroy:benzene:C=C,,,,,"))
        .boilingPoint(145f)
        .density(909f)
        .molarHeatCapacity(183.2f)
        .tag(Tags.SMOG)
        .build(),

    SULFATE = builder()
        .id("sulfate")
        .structure(Formula.atom(Element.SULFUR)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.SINGLE)
            .addAtom(Element.OXYGEN, BondType.SINGLE)
        ).charge(-2)
        .tag(Tags.ACID_RAIN)
        .build(),

    SULFIDE = builder()
        .id("sulfide")
        .structure(Formula.atom(Element.SULFUR))
        .charge(-2)
        .build(),

    SULFUR_DIOXIDE = builder()
        .id("sulfur_dioxide")
        .structure(Formula.deserialize("destroy:linear:O=S=O"))
        .boilingPoint(-10f)
        .density(2628.8f)
        .molarHeatCapacity(39.87f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    SULFURIC_ACID = builder()
        .id("sulfuric_acid")
        .structure(Formula.deserialize("destroy:linear:OS(=O)(=O)O"))
        .boilingPoint(337f)
        .density(1830.2f)
        .molarHeatCapacity(83.68f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    SULFUR_TRIOXIDE = builder()
        .id("sulfur_trioxide")
        .structure(Formula.deserialize("destroy:linear:S=(=O)(=O)O"))
        .boilingPoint(45f)
        .density(1920f)
        .specificHeatCapacity(50.6f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    TETRAFLUOROETHENE = builder()
        .id("tetrafluoroethene")
        .structure(Formula.deserialize("destroy:linear:FC=(F)C(F)F"))
        .boilingPoint(-76.3f)
        .density(1519f)
        // Couldn't find heat capacity
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SMOG)
        .build(),
    
    TOLUENE = builder()
        .id("toluene")
        .structure(Formula.deserialize("destroy:benzene:C,,,,,"))
        .boilingPoint(110.6f)
        .density(862.3f)
        .molarHeatCapacity(157.3f)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    TRICHLOROFLUOROMETHANE = builder()
        .id("trichlorofluoromethane")
        .structure(Formula.deserialize("destroy:linear:FC(Cl)(Cl)Cl"))
        .boilingPoint(23.77f)
        .density(1494f)
        .molarHeatCapacity(122.5f)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.REFRIGERANT)
        .tag(Tags.SMOG)
        .build(),

    TRIMETHYLAMINE = builder()
        .id("trimethylamine")
        .structure(Formula.deserialize("destroy:linear:CN(C)C"))
        .boilingPoint(5f)
        .density(670f)
        .molarHeatCapacity(132.55f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),
    
    TRINITROTOLUENE = builder()
        .id("tnt")
        .structure(Formula.deserialize("destroy:benzene:C,N~(~O)O,,N~(~O)O,,N~(~O)O"))
        .color(0xD0FCF1E8)
        .boilingPoint(240f) // Decomposes
        .density(1654f)
        .molarHeatCapacity(243.3f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMOG)
        .build(),

    VINYL_ACETATE = builder()
        .id("vinyl_acetate")
        .structure(Formula.deserialize("destroy:linear:C=COC(=O)C"))
        .boilingPoint(72.7f)
        .density(934f)
        .molarHeatCapacity(169.5f)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),

    WATER = builder()
        .id("water")
        .structure(Formula.deserialize("destroy:linear:O"))
        .boilingPoint(100f)
        .density(1000f)
        .specificHeatCapacity(4160f)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SOLVENT)
        .build(),

    ZINC_ION = builder()
        .id("zinc_ion")
        .translationKey("zinc")
        .structure(Formula.atom(Element.ZINC))
        .charge(2)
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

        
        @SuppressWarnings("null")
        public static final MoleculeTag

        /**
         * This Molecule automatically has a very high 'concentration' when in open Vats and Basins, and can be used up as much as necessary without depleting.
         */
        ABUNDANT_IN_AIR = new MoleculeTag("destroy", "abundant_in_air")
            .withColor(0xFFFFFF),

        /**
         * This Molecule will cause damage to Players exposed to it.
         */
        ACUTELY_TOXIC = new MoleculeTag("destroy", "acutely_toxic")
            .withColor(ChatFormatting.RED.getColor()),

        /**
         * This Molecule will increase the world's acid rain levels if released into the environment.
         */
        ACID_RAIN = new MoleculeTag("destroy", "acid_rain")
            .withColor(ChatFormatting.DARK_GREEN.getColor()),

        /**
         * This Molecule can be used as a bleach in recipes.
         */
        BLEACH = new MoleculeTag("destroy", "bleach")
            .withColor(0x8CFFC6),

        /**
         * This Molecule is a carcinogen.
         */
        CARCINOGEN = new MoleculeTag("destroy", "carcinogen")
            .withColor(0xFF85EB),

        /**
         * This Molecule can be used to make perfume.
         */
        FRAGRANT = new MoleculeTag("destroy", "fragrant")
            .withColor(0xF01D63),
    
        /**
         * This Molecule will increase the world's greenhouse gas level if released into the atmosphere.
         */
        GREENHOUSE = new MoleculeTag("destroy", "greenhouse")
            .withColor(0x00AD34),

        /**
         * This Molecule cannot partake in Reactions.
         */
        HYPOTHETICAL = new MoleculeTag("destroy", "hypothetical")
            .withColor(0xFAFFC4),

        /**
         * This Molecule will increase the world's ozone depletion level if released into the atmosphere.
         */
        OZONE_DEPLETER = new MoleculeTag("destroy", "ozone_depleter")
            .withColor(0xC7FFFD),

        /**
         * This Molecule can be used as a plasticizer for making plastics.
         */
        PLASTICIZER = new MoleculeTag("destroy", "plasticizer")
            .withColor(ChatFormatting.YELLOW.getColor()),

        /**
         * This Molecule can be used as 'fuel' for the Cooler.
         */
        REFRIGERANT = new MoleculeTag("destroy", "refrigerant")
            .withColor(ChatFormatting.AQUA.getColor()),

        /**
         * This Molecule is nauseous if the Player is not wearing perfume.
         */
        SMELLY = new MoleculeTag("destroy", "smelly")
            .withColor(ChatFormatting.GREEN.getColor()),

        /**
         * This Molecule will increase the world's smog levels if released into the atmosphere.
         */
        SMOG = new MoleculeTag("destroy", "smog")
            .withColor(ChatFormatting.GOLD.getColor()),

        /**
         * This Molecule is ignored when displaying the written contents of a Mixture, and ignored when used in Recipes.
         */
        SOLVENT = new MoleculeTag("destroy", "solvent")
            .withColor(ChatFormatting.BLUE.getColor());
    };

    public static void register() {};
};
