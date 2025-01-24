package com.petrolpark.destroy.chemistry.legacy.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.legacy.LegacyAtom;
import com.petrolpark.destroy.chemistry.legacy.LegacyElement;
import com.petrolpark.destroy.chemistry.legacy.LegacyMolecularStructure;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.LegacySpeciesTag;
import com.petrolpark.destroy.chemistry.legacy.LegacyBond.BondType;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies.MoleculeBuilder;

import net.minecraft.ChatFormatting;

public final class DestroyMolecules {

    public static final LegacySpecies 
    
    ACETAMIDE = builder()
        .id("acetamide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=O)N"))
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
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC~(~O^-0.5)O^-0.5"))
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=O)OH"))
        .boilingPoint(118.5f)
        .density(1049f)
        .molarHeatCapacity(123.1f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=O)OC(=O)C"))
        .boilingPoint(140f)
        .density(1082f)
        // Molar heat capacity unknown
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETONE = builder()
        .id("acetone")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=O)C"))
        .boilingPoint(56.08f)
        .density(784.5f)
        .molarHeatCapacity(126.3f)
        .tag(Tags.SOLVENT)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ACETONE_CYANOHYDRIN = builder()
        .id("acetone_cyanohydrin")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(OH)(C#N)C"))
        .boilingPoint(95f)
        .density(932f)
        .molarHeatCapacity(160f) // Estimate based on similar compounds
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    ACETYLENE = builder()
        .id("acetylene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C#C"))
        .boilingPoint(-75f)
        .density(613f)
        .molarHeatCapacity(44.036f)
        .build(),

    ACRYLONITRILE = builder()
        .id("acrylonitrile")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=CC#N"))
        .boilingPoint(77f)
        .density(810f)
        .molarHeatCapacity(113f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ADIPIC_ACID = builder()
        .id("adipic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=C(OH)CCCCC(=O)OH"))
        .boilingPoint(337.5f)
        .density(1360f)
        .molarHeatCapacity(196.5f)
        .tag(Tags.SMOG)
        .build(),

    ADIPONITRILE = builder()
        .id("adiponitrile")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N#CCCCCC#N"))
        .boilingPoint(295.1f)
        .density(951f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    AIBN = builder()
        .id("aibn")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(C)(C#N)N=NC(C)(C#N)C"))
        .boilingPoint(10000) // Doesn't boil - decomposes
        .density(1100f)
        .tag(Tags.SMOG)
        .build(),

    AMMONIA = builder()
        .id("ammonia")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N"))
        .boilingPoint(-33.34f)
        .density(900f) // Ammonium hydroxide has a density of ~0.9gcm^-3
        .molarHeatCapacity(80f)
        .tag(Tags.REFRIGERANT)
        .tag(Tags.SMELLY)
        .build(),

    AMMONIUM = builder()
        .id("ammonium")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NITROGEN, 1).addAtom(LegacyElement.HYDROGEN).addAtom(LegacyElement.HYDROGEN).addAtom(LegacyElement.HYDROGEN).addAtom(LegacyElement.HYDROGEN))
        .build(),

    ARGON = builder()
        .id("argon")
        .structure(LegacyMolecularStructure.atom(LegacyElement.ARGON))
        .boilingPointInKelvins(87.302f)
        .density(1395.4f)
        .molarHeatCapacity(20.85f)
        .build(),

    ASPIRIN = builder()
        .id("aspirin")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:OC(=O)C,C(=O)O,,,,"))
        .boilingPoint(140f)
        .density(1400f)
        // Couldn't find heat capacity
        .tag(Tags.SMOG)
        .build(),

    BENZENE = builder()
        .id("benzene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:,,,,"))
        .boilingPoint(80.1f)
        .density(876.5f)
        .molarHeatCapacity(134.8f)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    BENZYL_CHLORIDE = builder()
        .id("benzyl_chloride")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:CCl,,,,,"))
        .boilingPoint(179f)
        .density(1100f)
        .molarHeatCapacity(182.4f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMOG)
        .build(),
    
    BORIC_ACID = builder()
        .id("boric_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OB(O)O"))
        .boilingPoint(300f)
        .density(1435f)
        .molarHeatCapacity(81.3f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.FLAME_RETARDANT)
        .build(),

    BOROHYDRIDE = builder()
        .id("borohydride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:HB^-1(H)(H)H"))
        .build(),

    BUTADIENE = builder()
        .id("butadiene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=CC=C"))
        .boilingPoint(-4.41f)
        .density(614.9f)
        .molarHeatCapacity(123.65f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    CALCIUM_ION = builder()
        .id("calcium_ion")
        .translationKey("calcium")
        .structure(LegacyMolecularStructure.atom(LegacyElement.CALCIUM, 2))
        .build(),

    CARBON_DIOXIDE = builder()
        .id("carbon_dioxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=C=O"))
        .boilingPoint(-78.4645f) // Sublimes, doesn't "boil"
        .density(827.3f)
        .molarHeatCapacity(37.135f)
        .tag(Tags.GREENHOUSE)
        .build(),

    CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            LegacyMolecularStructure.atom(LegacyElement.CARBON)
            .addAtom(LegacyElement.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-191.5f)
        .density(789f) // Liquid density; gas density is the same order of magnitude
        .molarHeatCapacity(29.1f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .build(),

    CARBON_TETRACHLORIDE = builder()
        .id("carbon_tetrachloride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClC(Cl)(Cl)Cl"))
        .boilingPoint(76.72f)
        .density(1586.7f)
        .molarHeatCapacity(132.6f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    CHLORIDE = builder()
        .id("chloride")
        .structure(LegacyMolecularStructure.atom(LegacyElement.CHLORINE, -1))
        .build(),

    CHLORINE = builder()
        .id("chlorine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClCl"))
        .color(0x20F9FCC2)
        .boilingPoint(-34.04f)
        .density(1562.5f) 
        .molarHeatCapacity(33.949f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMELLY)
        .build(),

    CHLOROAURATE = builder()
        .id("chloroaurate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClAu^-1(Cl)(Cl)Cl"))
        .color(0x7FEDCA4A)
        .build(),

    CHLORODIFLUOROMETHANE = builder()
        .id("chlorodifluoromethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClC(F)F"))
        .boilingPoint(-40.7f)
        .density(1186.8f)
        .molarHeatCapacity(112.6f)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.REFRIGERANT)
        .build(),

    CHLOROETHANE = builder()
        .id("chloroethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CCCl"))
        .boilingPoint(12.27f)
        .density(889.8f)
        .molarHeatCapacity(40.7f)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.CARCINOGEN)
        .build(),

    CHLOROETHENE = builder()
        .id("chloroethene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=CCl"))
        .boilingPoint(-13.4f)
        .density(911f)
        .molarHeatCapacity(85.92f)
        .tag(Tags.CARCINOGEN)
        .build(),

    CHLOROMETHANE = builder()
        .id("chloromethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CCl"))
        .boilingPoint(-23.8f)
        .density(1.003f)
        .molarHeatCapacity(81.2f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.REFRIGERANT)
        .build(),

    CHLOROFORM = builder()
        .id("chloroform")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClC(Cl)Cl"))
        .boilingPoint(61.15f)
        .density(1489f)
        .molarHeatCapacity(114.25f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SOLVENT)
        .build(),

    CHROMATE = builder()
        .id("chromate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=Cr=(-O^-1)(-O^-1)O"))
        .color(0xD0F7ED2C)
        .build(),

    CHROMIUM_III = builder()
        .id("chromium_iii")
        .structure(LegacyMolecularStructure.atom(LegacyElement.CHROMIUM, 3))
        .color(0xD00D9614)
        .build(),

    CISPLATIN = builder()
        .id("cisplatin")
        .structure(LegacyMolecularStructure.atom(
            LegacyElement.PLATINUM)
            .addAtom(LegacyElement.CHLORINE)
            .addAtom(LegacyElement.CHLORINE)
            .addGroup(
                LegacyMolecularStructure.atom(LegacyElement.NITROGEN)
                .addAtom(LegacyElement.HYDROGEN)
                .addAtom(LegacyElement.HYDROGEN)
                .addAtom(LegacyElement.HYDROGEN), true
            ).addGroup(
                LegacyMolecularStructure.atom(LegacyElement.NITROGEN)
                .addAtom(LegacyElement.HYDROGEN)
                .addAtom(LegacyElement.HYDROGEN)
                .addAtom(LegacyElement.HYDROGEN), true
            ))
        .boilingPoint(270f)
        .density(3740f)
        .build(),

    COPPER_I = builder()
        .id("copper_i")
        .structure(LegacyMolecularStructure.atom(LegacyElement.COPPER, 1))
        .color(0xE0D30823)
        .build(),

    COPPER_II = builder()
        .id("copper_ii")
        .structure(LegacyMolecularStructure.atom(LegacyElement.COPPER, 2))
        .color(0xE00FFCA1)
        .build(),

    CREATINE = builder()
        .id("creatine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:NC(=N)N(C)CC(=O)O"))
        // Couldn't find BP or density
        .molarHeatCapacity(171.1f)
        .build(),

    CUBANE = builder()
        .id("cubane")
        .structure(LegacyMolecularStructure.deserialize("destroy:cubane:,,,,,,"))
        .boilingPoint(161.6f)
        .density(1290f)
        // Specific heat capacity unknown
        .tag(Tags.SMOG)
        .build(),

    CUBANEDICARBOXYLIC_ACID = builder()
        .id("cubanedicarboxylic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:cubane:C(=O)OH,,,,,,C(=O)OH"))
        .boilingPoint(457.4f)
        .density(2400f)
        .tag(Tags.SMOG)
        .build(),

    CYANAMIDE = builder()
        .id("cyanamide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N#CN"))
        .boilingPoint(260f)
        .density(1280f)
        .molarHeatCapacity(78.2f)
        .tag(Tags.SMOG)
        .tag(Tags.CARCINOGEN)
        .build(),

    CYANAMIDE_ION = builder()
        .id("cyanamide_ion")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N^-1=C=N^-1"))
        .build(),

    CYANIDE = builder()
        .id("cyanide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.CARBON, -1)
            .addAtom(LegacyElement.NITROGEN, BondType.TRIPLE)
        ).tag(Tags.ACUTELY_TOXIC)
        .build(),

    CYCLOHEXENE = builder()
        .id("cyclohexene")
        .structure(LegacyMolecularStructure.deserialize("destroy:cyclohexene:,,,,,,,,,"))
        .boilingPoint(82.98f)
        .density(811f)
        .molarHeatCapacity(152.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    // TODO cyclopentadiene

    // CYCLOPENTADIENIDE = builder()
    //     .id("cyclopentadienide")
    //     .structure(Formula.deserialize("destroy:cyclopentadienide:,,,,"))
    //     .charge(-1)
    //     .tag(Tags.SMOG)
    //     .build(),

    DIBORANE = builder()
        .id("diborane")
        .structure(LegacyMolecularStructure.deserialize("destroy:diborane:,,,"))
        .boilingPoint(-92.49f)
        .density(1131f)
        .molarHeatCapacity(56.7f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .build(),

    DICHLORODIFLUOROMETHANE = builder()
        .id("dichlorodifluoromethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClC(F)(F)Cl"))
        .boilingPoint(-28.9f)
        .density(1486f)
        .molarHeatCapacity(126.8f) //TODO possibly increase this to make R-12 a more viable refrigerant
        .tag(Tags.GREENHOUSE)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.REFRIGERANT)
        .build(),

    DICHLOROMETHANE = builder()
        .id("dichloromethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClCCl"))
        .boilingPoint(39.6f)
        .density(1.3266f)
        .molarHeatCapacity(81.2f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SOLVENT)
        .build(),

    DICHROMATE = builder()
        .id("dichromate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=Cr(=O)(O^-1)OCr=(=O)(O^-1)O"))
        .color(0xD0DB3D0D)
        .build(),

    DINITROTOLUENE = builder()
        .id("dinitrotoluene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,N~(~O)O,,N~(~O)O,,"))
        .boilingPoint(250f)
        .density(1520f)
        .molarHeatCapacity(243.3f) // Couldn't find data so assume same as TNT
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.PLASTICIZER)
        .build(),

    ETHANOL = builder()
        .id("ethanol")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CCO"))
        .boilingPoint(78.23f)
        .density(789.45f)
        .molarHeatCapacity(109f)
        .tag(Tags.SOLVENT)
        .tag(Tags.SMOG)
        .build(),

    ETHENE = builder()
        .id("ethene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=C"))
        .boilingPoint(-103.7f)
        .density(567.9f)
        .molarHeatCapacity(67.3f)
        .tag(Tags.SMOG)
        .build(),

    ETHYLANTHRAQUINONE = builder()
        .id("ethylanthraquinone")
        .structure(LegacyMolecularStructure.deserialize("destroy:anthraquinone:,,,O,,,CC,,O,"))
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f) // Not accurate, just based off an isomer
        .tag(Tags.SMOG)
        .build(),

    ETHYLANTHRAHYDROQUINONE = builder()
        .id("ethylanthrahydroquinone")
        .structure(LegacyMolecularStructure.deserialize("destroy:anthracene:,,,O,,,CC,,O,"))
        // No data found so using same as ethylanthraquinone
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f)
        .tag(Tags.SMOG)
        .build(),
    
    ETHYLBENZENE = builder()
        .id("ethylbenzene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:CC,,,,,"))
        .boilingPoint(136f)
        .density(866.5f)
        .specificHeatCapacity(1726f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    ETHOXIDE = builder()
        .id("ethoxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CCO^-1"))
        .build(),
    
    FLUORIDE = builder()
        .id("fluoride")
        .structure(LegacyMolecularStructure.atom(LegacyElement.FLUORINE, -1))
        .build(),

    GENERIC_ACID_ANHYDRIDE = builder()
        .id("generic_acid_anhydride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(=O)OC(=O)R"))
        .hypothetical()
        .translationKey("acid_anhydride")
        .build(),

    GENERIC_ACYL_CHLORIDE = builder()
        .id("generic_acyl_chloride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(=O)Cl"))
        .hypothetical()
        .translationKey("acyl_chloride")
        .build(),

    GENERIC_ALCOHOL = builder()
        .id("generic_alcohol")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)O"))
        .hypothetical()
        .translationKey("alcohol")
        .build(),

    GENERIC_ALKENE = builder()
        .id("generic_alkene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC=(R)C(R)R"))
        .hypothetical()
        .translationKey("alkene")
        .build(),

    GENERIC_ALKOXIDE = builder()
        .id("generic_alkoxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)O^-1"))
        .hypothetical()
        .translationKey("alkoxide")
        .build(),

    GENERIC_ALKYNE = builder()
        .id("generic_alkyne")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC#CR"))
        .hypothetical()
        .translationKey("alkyne")
        .build(),

    GENERIC_AMIDE = builder()
        .id("generic_amide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(=O)N"))
        .hypothetical()
        .translationKey("amide")
        .build(),
    
    GENERIC_AMINE = builder()
        .id("generic_amine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)N"))
        .hypothetical()
        .translationKey("amine")
        .build(),

    GENERIC_BORANE = builder()
        .id("generic_borane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)B(R)R"))
        .hypothetical()
        .translationKey("organic_borane")
        .build(),

    GENERIC_BORATE_ESTER = builder()
        .id("generic_borate_ester")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)OB(R)R"))
        .hypothetical()
        .translationKey("borate_ester")
        .build(),

    GENERIC_CARBONYL = builder()
        .id("generic_carbonyl")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC=(R)O"))
        .hypothetical()
        .translationKey("carbonyl")
        .build(),

    GENERIC_CARBOXYLIC_ACID = builder()
        .id("generic_carboxylic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(=O)O"))
        .hypothetical()
        .translationKey("carboxylic_acid")
        .build(),
    
    GENERIC_CHLORIDE = builder()
        .id("generic_chloride")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)Cl"))
        .hypothetical()
        .translationKey("organic_chloride")
        .build(),

    GENERIC_ESTER = builder()
        .id("generic_ester")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(=O)OR"))
        .hypothetical()
        .translationKey("ester")
        .build(),

    GENERIC_ISOCYANATE = builder()
        .id("generic_isocyanate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)N=C=O"))
        .hypothetical()
        .translationKey("isocyanate")
        .build(),

    GENERIC_NITRILE = builder()
        .id("generic_nitrile")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)C#N"))
        .hypothetical()
        .translationKey("nitrile")
        .build(),

    GENERIC_NITRO = builder()
        .id("generic_nitro")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)N~(~O)O"))
        .hypothetical()
        .translationKey("nitro")
        .build(),
    
    GENERIC_ORGANIC_BORIC_ACID = builder()
        .id("generic_organic_boric_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RB(R)OH"))
        .hypothetical()
        .translationKey("organic_boric_acid")
        .build(),

    GENERIC_PRIMARY_AMINE = builder()
        .id("generic_primary_amine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)N"))
        .hypothetical()
        .translationKey("non_tertiary_amine")
        .build(),

    GENERIC_PRIMARY_BORANE = builder()
        .id("generic_primary_borane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:RC(R)(R)B"))
        .hypothetical()
        .translationKey("non_tertiary_borane")
        .build(),

    GLYCEROL = builder()
        .id("glycerol")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OCC(O)CO"))
        .boilingPoint(290f)
        .density(1261f)
        .molarHeatCapacity(213.8f)
        .tag(Tags.SMOG)
        .build(),

    HEXANE_DIISOCYANATE = builder()
        .id("hexane_diisocyanate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=C=NCCCCCCN=C=O"))
        .boilingPoint(255f)
        .density(1047f)
        .molarHeatCapacity(222.7f)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    HEXANEDIAMINE = builder()
        .id("hexanediamine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:NCCCCCCN"))
        .boilingPoint(204.6f)
        .density(840f)
        .molarHeatCapacity(250f) // Estimate: couldn't find heat capacity
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .build(),

    HYDRAZINE = builder()
        .id("hydrazine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:NN"))
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
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClH"))
        .boilingPoint(-85.05f)
        .density(1490f)
        .specificHeatCapacity(798.1f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    HYDROFLUORIC_ACID = builder()
        .id("hydrofluoric_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:FH"))
        .density(990f)
        .boilingPoint(19.5f)
        // Heat capacity unknown
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .build(),

    HYDROGEN = builder()
        .id("hydrogen")
        .structure(LegacyMolecularStructure.atom(LegacyElement.HYDROGEN).addAtom(LegacyElement.HYDROGEN))
        .boilingPointInKelvins(20.271f)
        .density(70.85f)
        .molarHeatCapacity(28.84f)
        .build(),

    HYDROGEN_CYANIDE = builder()
        .id("hydrogen_cyanide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N#C"))
        .boilingPoint(26f)
        .density(687.6f)
        .molarHeatCapacity(35.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    HYDROGEN_IODIDE = builder()
        .id("hydrogen_iodide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.IODINE).addAtom(LegacyElement.HYDROGEN))
        .boilingPoint(-35.36f)
        .density(2850)
        .molarHeatCapacity(29.2f)
        .build(),
    
    HYDROGEN_PEROXIDE = builder()
        .id("hydrogen_peroxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OO"))
        .color(0x40C7F4FC)
        .boilingPoint(150.2f)
        .density(1110f)
        .specificHeatCapacity(2619f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.BLEACH)
        .build(),

    HYDROGENSULFATE = builder()
        .id("hydrogensulfate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=S(=O)(OH)O^-1"))
        .tag(Tags.ACID_RAIN)
        .build(),

    HYDROXIDE = builder()
        .id("hydroxide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.HYDROGEN).addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1)))
        .density(900f) // Not accurate but allows separation of mercury and sodium hydroxide solution by Centrifugation
        .build(),

    HYPOCHLOROUS_ACID = builder()
        .id("hypochlorous_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OCl"))
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.BLEACH)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.BLEACH)
        .build(),

    HYPOCHLORITE = builder()
        .id("hypochlorite")
        .structure(LegacyMolecularStructure.atom(LegacyElement.OXYGEN, -1).addAtom(LegacyElement.CHLORINE))
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .build(),

    ISOPRENE = builder()
        .id("isoprene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=C(C)C=C"))
        .boilingPoint(34f)
        .density(681f)
        .molarHeatCapacity(102.69f)
        .build(),

    IODIDE = builder()
        .id("iodide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.IODINE, -1))
        .build(),

    IODINE = builder()
        .id("iodine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:II"))
        .color(0x80AA16A5)
        .boilingPoint(184.3f)
        .density(4933f)
        .molarHeatCapacity(54.44f)
        .build(),

    IODOMETHANE = builder()
        .id("iodomethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CI"))
        .boilingPoint(42.6f)
        .density(2280f)
        .molarHeatCapacity(82.75f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    IRON_II = builder()
        .id("iron_ii")
        .structure(LegacyMolecularStructure.atom(LegacyElement.IRON, 2))
        .color(0x80A9C92A)
        .build(),

    IRON_III = builder()
        .id("iron_iii")
        .structure(LegacyMolecularStructure.atom(LegacyElement.IRON, 3))
        .color(0x80F94939)
        .build(),

    LEAD_II = builder()
        .id("lead_ii")
        .structure(LegacyMolecularStructure.atom(LegacyElement.LEAD, 2))
        .build(),

    MERCURY = builder()
        .id("mercury")
        .structure(LegacyMolecularStructure.atom(LegacyElement.MERCURY))
        .boilingPoint(356.73f)
        .density(13534f)
        .molarHeatCapacity(27.98f)
        .tag(Tags.ACUTELY_TOXIC)
        .color(0xFFB3B3B3)
        .build(),

    METAXYLENE = builder()
        .id("metaxylene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,,C,,,"))
        .boilingPoint(139f)
        .density(860f)
        .molarHeatCapacity(181.5f)
        .tag(Tags.SMOG)
        .build(),

    METHANE = builder()
        .id("methane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C"))
        .boilingPoint(-161.5f)
        .density(424f)
        .molarHeatCapacity(35.7f)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SMOG)
        .build(),

    METHANOL = builder()
        .id("methanol")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CO"))
        .boilingPoint(65)
        .density(792f)
        .molarHeatCapacity(68.62f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    METHYLAMINE = builder()
        .id("methylamine")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CN"))
        .boilingPoint(-6.3f)
        .density(656.2f)
        .molarHeatCapacity(101.8f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=O)OC"))
        .boilingPoint(56.9f)
        .density(932f)
        .molarHeatCapacity(140.2f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    METHYL_METHACRYLATE = builder()
        .id("methyl_methacrylate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CC(=C)C(=O)OC"))
        .boilingPoint(101f)
        .density(940f)
        .molarHeatCapacity(191f)
        .tag(Tags.SMOG)
        .build(),

    METHYL_SALICYLATE = builder()
        .id("methyl_salicylate")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C(=O)OC,O,,,,"))
        .boilingPoint(222f)
        .density(1174f)
        .molarHeatCapacity(248.9f)
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),
    
    NICKEL_ION = builder()
        .id("nickel_ion")
        .translationKey("nickel")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NICKEL, 2))
        .color(0xE09BEAAB)
        .tag(Tags.CARCINOGEN)
        .build(),

    NITRATE = builder()
        .id("nitrate")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NITROGEN, 1)
            .addAtom(LegacyElement.OXYGEN, BondType.DOUBLE)
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
        )
        .tag(Tags.ACID_RAIN)
        .build(),

    NITRIC_ACID = builder()
        .id("nitric_acid")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NITROGEN)
            .addAtom(LegacyElement.OXYGEN, BondType.AROMATIC)
            .addAtom(LegacyElement.OXYGEN, BondType.AROMATIC)
            .addGroup(LegacyMolecularStructure.atom(LegacyElement.OXYGEN)
                .addAtom(LegacyElement.HYDROGEN)
            ) //TODO maybe add color (though this should come from a decomposition)
        ).boilingPoint(83f)
        .density(1510f)
        .molarHeatCapacity(53.29f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    NITROGEN = builder()
        .id("nitrogen")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:N#N"))
        .boilingPointInKelvins(77.355f)
        .density(807f)
        .molarHeatCapacity(29.12f)
        .tag(Tags.ABUNDANT_IN_AIR)
        .build(),

    NITROGEN_DIOXIDE = builder()
        .id("nitrogen_dioxide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NITROGEN)
            .addAtom(LegacyElement.OXYGEN, BondType.AROMATIC)
            .addAtom(LegacyElement.OXYGEN, BondType.AROMATIC)
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
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C(ON(~O)(~O))C(ON(~O)(~O))CON(~O)(~O)"))
        .boilingPoint(50f)
        .density(1600f)
        // Heat capacity unknown
        .tag(Tags.SMOG)
        .build(),

    NITRONIUM = builder()
        .id("nitronium")
        .structure(LegacyMolecularStructure.atom(LegacyElement.NITROGEN, 1)
            .addAtom(LegacyElement.OXYGEN, BondType.DOUBLE)
            .addAtom(LegacyElement.OXYGEN, BondType.DOUBLE)
        ).build(),

    OCTASULFUR = builder()
        .id("octasulfur")
        .translationKey("sulfur")
        .structure(LegacyMolecularStructure.deserialize("destroy:octasulfur:hello"))
        .color(0xFFD00000)
        .boilingPoint(444.6f) //TODO melting point is 119C
        .density(2070f)
        .molarHeatCapacity(21.64f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    OLEUM = builder()
        .id("oleum")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:HOS(=O)(=O)OS(=O)(=O)OH"))
        .boilingPoint(10f)
        .density(1820f) // Estimate
        .specificHeatCapacity(2600f) // Estimate
        .tag(Tags.ACID_RAIN)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    ORTHOXYLENE = builder()
        .id("orthoxylene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,C,,,,"))
        .boilingPoint(144.4f)
        .density(880f)
        .molarHeatCapacity(187.1f)
        .tag(Tags.SMOG)
        .build(),

    OXIDE = builder()
        .id("oxide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.OXYGEN, -2))
        .build(),

    OXYGEN = builder()
        .id("oxygen")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=O"))
        .boilingPointInKelvins(90.188f)
        .density(1141f)
        .molarHeatCapacity(29.378f)
        .tag(Tags.ABUNDANT_IN_AIR)
        .build(),

    PARAXYLENE = builder()
        .id("paraxylene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,,,C,,"))
        .boilingPoint(138.35f)
        .density(861f)
        .molarHeatCapacity(182f)
        .tag(Tags.SMOG)
        .build(),

    PHENOL = builder()
        .id("phenol")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:O,,,,,"))
        .boilingPoint(181.7f)
        .density(1070f)
        .molarHeatCapacity(220.9f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    PHENYLACETIC_ACID = builder()
        .id("phenylacetic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:CC(=O)O"))
        .boilingPoint(265.5f)
        .density(1080.9f)
        .molarHeatCapacity(170f) // Estimate based on isomers
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),

    PHENYLACETONE = builder()
        .id("phenylacetone")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:CC(=O)C"))
        .boilingPoint(215f)
        .density(1006f)
        .molarHeatCapacity(250f) // Estimate based on isomers
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.SMOG)
        .build(),

    PHOSGENE = builder()
        .id("phosgene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:ClC(=O)Cl"))
        .boilingPoint(8.3f)
        .density(1432f)
        .molarHeatCapacity(101f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.OZONE_DEPLETER)
        .tag(Tags.SMELLY)
        .build(),
    
    PHTHALIC_ANHYDRIDE = builder()
        .id("phthalic_anhydride")
        .structure(LegacyMolecularStructure.deserialize("destroy:isohydrobenzofuran:,,,O,O,"))
        .boilingPoint(295f)
        .density(1530f)
        .molarHeatCapacity(161.8f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    PICRIC_ACID = builder()
        .id("picric_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:O,N~(~O)O,,N~(~O)O,,N~(~O)O"))
        .color(0xC0ED7417)
        .boilingPoint(300f) // Detonates before this
        .density(1763f)
        .molarHeatCapacity(200f) // Estimate, couldn't find heat capacity
        .tag(Tags.SMOG)
        .build(),

    POTASSIUM_ION = builder()
        .id("potassium_ion")
        .translationKey("potassium")
        .structure(LegacyMolecularStructure.atom(LegacyElement.POTASSIUM, 1))
        .build(),

    PROPENE = builder()
        .id("propene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=CC"))
        .boilingPoint(-47.6f)
        .density(1810f)
        .molarHeatCapacity(102f)
        .tag(Tags.SMOG)
        .build(),

    SALICYLIC_ACID = builder()
        .id("salicylic_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C(=O)O,O,,,,"))
        .boilingPoint(200f)
        .density(1443f)
        .molarHeatCapacity(159.4f)
        .tag(Tags.SMOG)
        .build(),

    SODIUM_METAL = builder()
        .id("sodium_metal")
        .translationKey("sodium")
        .structure(LegacyMolecularStructure.atom(LegacyElement.SODIUM))
        .color(0xFFB3B3B3)
        .boilingPoint(882.94f)
        .density(968f)
        .molarHeatCapacity(28.23f)
        .build(),

    SODIUM_ION = builder()
        .id("sodium_ion")
        .structure(LegacyMolecularStructure.atom(LegacyElement.SODIUM, 1))
        .density(900f) // Not accurate but allows separation of Mercury and sodium hydroxide solution by Centrifugation
        .build(),

    STYRENE = builder()
        .id("styrene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C=C,,,,,"))
        .boilingPoint(145f)
        .density(909f)
        .molarHeatCapacity(183.2f)
        .tag(Tags.SMOG)
        .build(),

    SULFATE = builder()
        .id("sulfate")
        .structure(LegacyMolecularStructure.atom(LegacyElement.SULFUR, 2)
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
            .addAtom(new LegacyAtom(LegacyElement.OXYGEN, -1))
        ).tag(Tags.ACID_RAIN)
        .build(),

    SULFIDE = builder()
        .id("sulfide")
        .structure(LegacyMolecularStructure.atom(LegacyElement.SULFUR, -2))
        .build(),

    SULFUR_DIOXIDE = builder()
        .id("sulfur_dioxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:O=S=O"))
        .boilingPoint(-10f)
        .density(2628.8f)
        .molarHeatCapacity(39.87f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    SULFURIC_ACID = builder()
        .id("sulfuric_acid")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OS(=O)(=O)O"))
        .boilingPoint(337f)
        .density(1830.2f)
        .molarHeatCapacity(83.68f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    SULFUR_TRIOXIDE = builder()
        .id("sulfur_trioxide")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:S=(=O)(=O)O"))
        .boilingPoint(45f)
        .density(1920f)
        .specificHeatCapacity(50.6f)
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.ACID_RAIN)
        .build(),

    TETRAETHYLLEAD = builder()
        .id("tetraethyllead")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CCPb(CC)(CC)CC"))
        .boilingPoint(84.5f)
        .density(1653f)
        .molarHeatCapacity(307.4f)
        .tag(Tags.FUEL_ADDITIVE)
        .tag(Tags.SMOG)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    TETRAFLUOROETHENE = builder()
        .id("tetrafluoroethene")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:FC=(F)C(F)F"))
        .boilingPoint(-76.3f)
        .density(1519f)
        // Couldn't find heat capacity
        .tag(Tags.ACUTELY_TOXIC)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SMOG)
        .build(),

    TETRAHYDROXYBORATE = builder()
        .id("tetrahydroxyborate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:OB^-1(O)(O)O"))
        .build(),

    TETRAHYDROXY_TETRABORATE = builder()
        .id("tetrahydroxy_tetraborate")
        .structure(LegacyMolecularStructure.deserialize("destroy:tetraborate:O,O,O,O"))
        .build(),
    
    TOLUENE = builder()
        .id("toluene")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,,,,,"))
        .boilingPoint(110.6f)
        .density(862.3f)
        .molarHeatCapacity(157.3f)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.SMOG)
        .tag(Tags.SOLVENT)
        .build(),

    TOLUENE_DIISOCYANATE = builder()
        .id("toluene_diisocyanate")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,N=C=O,,N=C=O,,"))
        .boilingPoint(251f)
        .density(1214f)
        .molarHeatCapacity(222.7f) // Couldn't find data, assume same as hexane diisocyanate
        .tag(Tags.CARCINOGEN)
        .tag(Tags.ACUTELY_TOXIC)
        .build(),

    TRICHLOROFLUOROMETHANE = builder()
        .id("trichlorofluoromethane")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:FC(Cl)(Cl)Cl"))
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
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:CN(C)C"))
        .boilingPoint(5f)
        .density(670f)
        .molarHeatCapacity(132.55f)
        .tag(Tags.SMELLY)
        .tag(Tags.SMOG)
        .build(),

    TRIMETHYL_BORATE = builder()
        .id("trimethyl_borate")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:COB(OC)OC"))
        .boilingPoint(68.5f)
        .density(932f)
        .molarHeatCapacity(189.9f)
        .build(),
    
    TRINITROTOLUENE = builder()
        .id("tnt")
        .structure(LegacyMolecularStructure.deserialize("destroy:benzene:C,N~(~O)O,,N~(~O)O,,N~(~O)O"))
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
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:C=COC(=O)C"))
        .boilingPoint(72.7f)
        .density(934f)
        .molarHeatCapacity(169.5f)
        .tag(Tags.ADHESIVE)
        .tag(Tags.CARCINOGEN)
        .tag(Tags.FRAGRANT)
        .tag(Tags.SMOG)
        .build(),

    WATER = builder()
        .id("water")
        .structure(LegacyMolecularStructure.deserialize("destroy:linear:HOH"))
        .boilingPoint(100f)
        .density(1000f)
        .specificHeatCapacity(4160f)
        .tag(Tags.GREENHOUSE)
        .tag(Tags.SOLVENT)
        .build(),

    ZINC_ION = builder()
        .id("zinc_ion")
        .translationKey("zinc")
        .structure(LegacyMolecularStructure.atom(LegacyElement.ZINC, 2))
        .build(),

    PROTON = builder()
        .id("proton")
        .structure(LegacyMolecularStructure.atom(LegacyElement.HYDROGEN, 1))
        .build();

    private static final MoleculeBuilder builder() {
        return new MoleculeBuilder(Destroy.MOD_ID);
    };

    public static class Tags {

        
        @SuppressWarnings("null")
        public static final LegacySpeciesTag

        /**
         * This Molecule automatically has a very high 'concentration' when in open Vats and Basins, and can be used up as much as necessary without depleting.
         */
        ABUNDANT_IN_AIR = new LegacySpeciesTag("destroy", "abundant_in_air")
            .withColor(0xFFFFFF),

        /**
         * This Molecule will cause damage to Players exposed to it.
         */
        ACUTELY_TOXIC = new LegacySpeciesTag("destroy", "acutely_toxic")
            .withColor(ChatFormatting.RED.getColor()),

        /**
         * This Molecule will increase the world's acid rain levels if released into the environment.
         */
        ACID_RAIN = new LegacySpeciesTag("destroy", "acid_rain")
            .withColor(ChatFormatting.DARK_GREEN.getColor()),

        /**
         * This Molecule can be used to make glue.
         */
        ADHESIVE = new LegacySpeciesTag("destroy", "adhesive")
            .withColor(0x6FEF40),

        /**
         * This Molecule can be used as a bleach in recipes.
         */
        BLEACH = new LegacySpeciesTag("destroy", "bleach")
            .withColor(0x8CFFC6),

        /**
         * This Molecule is a carcinogen.
         */
        CARCINOGEN = new LegacySpeciesTag("destroy", "carcinogen")
            .withColor(0xFF85EB),

        /**
         * This Molecule induces estris in players.
         */
        ESTROGEN = new LegacySpeciesTag("destroy", "estrogen")
            .withColor(0xFF96F6),

        /**
         * This Molecule is resistant to burning.
         */
        FLAME_RETARDANT = new LegacySpeciesTag("destroy", "flame_retardant")
            .withColor(0xF7AD0E),

        /**
         * This Molecule can be used to make perfume.
         */
        FRAGRANT = new LegacySpeciesTag("destroy", "fragrant")
            .withColor(0xF01D63),

        /**
         * This Molecule makes fuel give more energy.
         */
        FUEL_ADDITIVE = new LegacySpeciesTag("destroy", "fuel_additive")
            .withColor(0x89541B),
    
        /**
         * This Molecule will increase the world's greenhouse gas level if released into the atmosphere.
         */
        GREENHOUSE = new LegacySpeciesTag("destroy", "greenhouse")
            .withColor(0x00AD34),

        /**
         * This Molecule cannot partake in Reactions.
         */
        HYPOTHETICAL = new LegacySpeciesTag("destroy", "hypothetical")
            .withColor(0xFAFFC4),

        /**
         * This Molecule will make entities exposed to it cry.
         */
        LACRIMATOR = new LegacySpeciesTag("destroy", "lacrimator")
            .withColor(0xCBF2F0),

        /**
         * This Molecule will increase the world's ozone depletion level if released into the atmosphere.
         */
        OZONE_DEPLETER = new LegacySpeciesTag("destroy", "ozone_depleter")
            .withColor(0xC7FFFD),

        /**
         * This Molecule can be used as a plasticizer for making plastics.
         */
        PLASTICIZER = new LegacySpeciesTag("destroy", "plasticizer")
            .withColor(ChatFormatting.YELLOW.getColor()),

        /**
         * This Molecule can be used as 'fuel' for the Cooler.
         */
        REFRIGERANT = new LegacySpeciesTag("destroy", "refrigerant")
            .withColor(ChatFormatting.AQUA.getColor()),

        /**
         * This Molecule is nauseous if the Player is not wearing perfume.
         */
        SMELLY = new LegacySpeciesTag("destroy", "smelly")
            .withColor(ChatFormatting.GREEN.getColor()),

        /**
         * This Molecule will increase the world's smog levels if released into the atmosphere.
         */
        SMOG = new LegacySpeciesTag("destroy", "smog")
            .withColor(ChatFormatting.GOLD.getColor()),

        /**
         * This Molecule is ignored when displaying the written contents of a Mixture, and ignored when used in Recipes.
         */
        SOLVENT = new LegacySpeciesTag("destroy", "solvent")
            .withColor(ChatFormatting.BLUE.getColor());
    };

    public static void register() {};
};
