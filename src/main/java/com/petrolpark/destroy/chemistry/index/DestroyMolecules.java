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
        .boilingPoint(221.2f)
        .density(1159f)
        .molarHeatCapacity(91.3f)
        .tag(Tags.ACUTELY_TOXIC)
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
        .build(),

    ACETIC_ACID = builder()
        .id("acetic_acid")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OH+4.756"))
        .boilingPoint(118.5f)
        .density(1049f)
        .molarHeatCapacity(123.1f)
        .build(),

    ACETIC_ANHYDRIDE = builder()
        .id("acetic_anhydride")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC(=O)C"))
        .boilingPoint(140f)
        .density(1082f)
        // Molar heat capacity unknown
        .build(),

    ACETONE = builder()
        .id("acetone")
        .structure(Formula.deserialize("destroy:linear:CC(=O)C"))
        .boilingPoint(56.08f)
        .density(784.5f)
        .molarHeatCapacity(126.3f)
        .build(),

    ACETONE_CYANOHYDRIN = builder()
        .id("acetone_cyanohydrin")
        .structure(Formula.deserialize("destroy:linear:CC(OH)(C#N)C"))
        .boilingPoint(95f)
        .density(932f)
        .molarHeatCapacity(160f) // Estimate based on similar compounds
        .build(),

    ACRYLONITRILE = builder()
        .id("acrylonitrile")
        .structure(Formula.deserialize("destroy:linear:C=CC#N"))
        .boilingPoint(77f)
        .density(810f)
        .molarHeatCapacity(113f)
        .build(),

    ADIPIC_ACID = builder()
        .id("adipic_acid")
        .structure(Formula.deserialize("destroy:linear:O=C(OH+4.43f)CCCCC(=O)OH+5.41f"))
        .boilingPoint(337.5f)
        .density(1360f)
        .molarHeatCapacity(196.5f)
        .build(),

    ADIPONITRILE = builder()
        .id("adiponitrile")
        .structure(Formula.deserialize("destroy:linear:N#CCCCCC#N"))
        .boilingPoint(295.1f)
        .density(951f)
        .build(),

    AIBN = builder()
        .id("aibn")
        .structure(Formula.deserialize("destroy:linear:CC(C)(C#N)N=NC(C)(C#N)C"))
        .boilingPoint(10000) // Doesn't boil - decomposes
        .density(1100f)
        .build(),

    AMMONIA = builder()
        .id("ammonia")
        .structure(Formula.deserialize("destroy:linear:N"))
        .boilingPoint(-33.34f)
        .density(900f) // Ammonium hydroxide has a density of ~0.9gcm^-3
        .molarHeatCapacity(80f)
        .tag(Tags.REFRIGERANT)
        .build(),

    AMMONIUM = builder()
        .id("ammonium")
        .structure(Formula.atom(Element.NITROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .charge(1)
        .build(),

    ASPIRIN = builder()
        .id("aspirin")
        .structure(Formula.deserialize("destroy:benzene:OC(=O)C,C(=O)OH+3.5f,,,,"))
        .boilingPoint(140f)
        .density(1400f)
        // Couldn't find heat capacity
        .build(),

    BENZENE = builder()
        .id("benzene")
        .structure(Formula.deserialize("destroy:benzene:,,,,"))
        .boilingPoint(80.1f)
        .density(876.5f)
        .molarHeatCapacity(134.8f),

    BENZYL_CHLORIDE = builder()
        .id("benzyl_chloride")
        .structure(Formula.deserialize("destroy:benzene:CCl,,,,,"))
        .boilingPoint(179f)
        .density(1100f)
        .molarHeatCapacity(182.4f)
        .build(),

    BUTADIENE = builder()
        .id("butadiene")
        .structure(Formula.deserialize("destroy:linear:C=CC=C"))
        .boilingPoint(-4.41f)
        .density(614.9f)
        .molarHeatCapacity(123.65f)
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
        .boilingPoint(-78.4645) // Sublimes, doesn't "boil"
        .density(1.977) // Gas density
        .molarHeatCapacity(37.135f)
        .build(),

    CARBON_MONOXIDE = builder()
        .id("carbon_monoxide")
        .structure(
            Formula.atom(Element.CARBON)
            .addAtom(Element.OXYGEN, BondType.TRIPLE)
        ).boilingPoint(-191.5f)
        .density(789f) // Liquid density; gas density is the same order of magnitude
        .molarHeatCapacity(29.1f)
        .build(),

    CARBON_TETRACHLORIDE = builder()
        .id("carbon_tetrachloride")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)(Cl)Cl"))
        .boilingPoint(76.72f)
        .density(1586.7f)
        .molarHeatCapacity(132.6f)
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
        .tag(Tags.REFRIGERANT)
        .build(),

    CHLOROFORM = builder()
        .id("chloroform")
        .structure(Formula.deserialize("destroy:linear:ClC(Cl)Cl"))
        .boilingPoint(61.15f)
        .density(1489f)
        .molarHeatCapacity(114.25f)
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
        .build(),

    CUBANEDICARBOXYLIC_ACID = builder()
        .id("cubanedicarboxylic_acid")
        .structure(Formula.deserialize("destroy:cubane:C(=O)OH,,,C(=O)OH,,,"))
        .boilingPoint(457.4f)
        .density(2400f)
        .build(),

    CYANIDE = builder()
        .id("cyanide")
        .structure(Formula.atom(Element.CARBON)
            .addAtom(Element.NITROGEN, BondType.TRIPLE)
        ).charge(-1)
        .build(),

    CYCLOHEXENE = builder()
        .id("cyclohexene")
        .structure(Formula.deserialize("destroy:linear:C")) //TODO cyclohexene topology
        .boilingPoint(82.98f)
        .density(811f)
        .molarHeatCapacity(152.9f)
        .build(),

    // TODO cyclopentadiene

    CYCLOPENTADIENIDE = builder()
        .id("cyclopentadienide")
        .structure(Formua.deserialize("destroy:cyclopentadienide:,,,"))
        .charge(-1)
        .build();

    DICHLORODIFLUOROMETHANE = builder()
        .id("dichlorodifluoromethane")
        .structure(Formula.deserialize("destroy:linear:ClC(F)(F)Cl"))
        .boilingPoint(-28.9f)
        .density(1486f)
        .molarHeatCapacity(126.8f) //TODO possibly increase this to make R-12 a more viable refrigerant
        .tag(Tags.REFRIGERANT)
        .build();

    ETHANOL = builder()
        .id("ethanol")
        .structure(Formula.deserialize("destroy:linear:CCO"))
        .boilingPoint(78.23f)
        .density(789.45f)
        .molarHeatCapacity(109f)
        .build(),

    ETHENE = builder()
        .id("ethene")
        .structure(Formula.deserialize("destroy:linear:C=C"))
        .boilingPoint(-103.7f)
        .density(1.178f) // Gas density
        .molarHeatCapacity(67.3f)
        .build(),

    ETHYLANTHRAQUINONE = builder()
        .id("ethylanthraquinone")
        .structure(Formula.deserialize("destroy:anthraquinone:,,,,=O,,,,CC,,,=O,,"))
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f) // Not accurate, just based off an isomer
        .build(),

    ETHYLANTHRAHYDROQUINONE = builder()
        .id("ethylanthrahydroquinone")
        .structure(Formula.deserialize("destroy:anthracene:,,,,O,,,,CC,,,O,,"))
        // No data found so using same as ethylanthraquinone
        .boilingPoint(415.4f)
        .density(1231f)
        .molarHeatCapacity(286.6f)
        .build(),
    
    ETHYLBENZENE = builder()
        .id("ethylbenzene")
        .structure(Formula.deserialize("destroy:benzene:CC,,,,,"))
        .boilingPoint(136f)
        .density(866.5f)
        .specificHeatCapacity(1726f)
        .build(),
    
    FLUORIDE_ION = builder()
        .id("fluoride_ion")
        .translationKey("fluoride")
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
        .boilingPoint(290f)
        .density(1261f)
        .molarHeatCapacity(213.8f)
        .build(),

    HEXANEDIAMINE = builder()
        .id("hexanediamine")
        .structure(Formula.deserialize("destroy:linear:NCCCCCCN"))
        .boilingPoint(204.6f)
        .density(840f)
        .molarHeatCapacity(250f) // Estimate: couldn't find heat capacity
        .build(),

    HYDRAZINE = builder()
        .id("hydrazine")
        .structure(Formula.deserialize("NN"))
        .boilingPoint(114f)
        .density(1021f)
        .molarHeatCapacity(70f) // Estimate: couldn't find heat capacity
        .build(),

    HYDROCHLORIC_ACID = builder()
        .id("hydrochloric_acid")
        .structure(Formula.deserialize("destroy:linear:ClH+-5.9"))
        .boilingPoint(-85.05f)
        .density(1490f)
        .specificHeatCapacity(798.1f)
        .build(),

    HYDROFLUORIC_ACID = builder()
        .id("hydrofluoric_acid")
        .structure(Formula.deserialize("destroy:linear:FH+3.17"))
        .density(1.15f)
        .boilingPoint(19.5f)
        // Heat capacity unknown
        .build(),

    HYDROGEN = builder()
        .id("hydrogen")
        .structure(Formula.atom(Element.HYDROGEN).addAtom(Element.HYDROGEN))
        .boilingPointInKelvins(20.271f)
        .density(0.08988)
        .molarHeatCapacity(28.84f)
        .build(),

    HYDROGEN_CYANIDE = builder()
        .id("hydrogen_cyanide")
        .structure(Formula.deserialize("destroy:linear:N#C"))
        .boilingPoint(26f)
        .density(687.6f)
        .molarHeatCapacity(35.9f)
        .build(),

    HYDROGEN_IODIDE = builder()
        .id("hydrogen_iodide")
        .structure(Formula.atom(Element.IODINE).addAtom(Element.HYDROGEN))
        .boilingPoint(-35.36f)
        .density(2850)
        .heatCapacity(29.2f)
        .build(),
    
    HYDROGEN_PEROXIDE = builder()
        .id("hydrogen_peroxide")
        .structure(Formula.deserialize("destroy:linear:OO"))
        .color(0x40C7F4FC)
        .boilingPoint(150.2f)
        .density(1110f)
        .specificHeatCapacity(2619f)
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
        .build(),

    HYDROXIDE = builder()
        .id("hydroxide")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.HYDROGEN))
        .charge(-1)
        .build(),

    HYPOCHLORITE = builder()
        .id("hypochlorite")
        .structure(Formula.atom(Element.OXYGEN).addAtom(Element.CHLORINE))
        .charge(-1)
        .build(),

    IODIDE_ION = builder()
        .id("iodide_ion")
        .translationKey("iodide")
        .structure(Formula.atom(Element.IODINE))
        .charge(-1)
        .build(),

    IODOMETHANE = builder()
        .id("iodomethane")
        .structure(Formula.deserialize("destroy:linear:CI"))
        .boilingPoint(42.6f)
        .density(2280f)
        .molarHeatCapacity(82.75f)
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
        .build(),

    METHANE = builder()
        .id("methane")
        .structure(Formula.deserialize("destroy:linear:C"))
        .boilingPoint(-161.5f)
        .density(0.657)
        .molarHeatCapacity(35.7f)
        .build(),

    METHANOL = builder()
        .id("methanol")
        .structure(Formula.deserialize("destroy:linear:CO"))
        .boilingPoint(65)
        .density(792f)
        .molarHeatCapacity(68.62f)
        .build(),

    METHYLAMINE = builder()
        .id("methylamine")
        .structure(Formula.deserialize("destroy:linear:CN"))
        .boilingPoint(-6.3f)
        .density(656.2f)
        .molarHeatCapacity(101.8f)
        .build(),

    METHYL_ACETATE = builder()
        .id("methyl_acetate")
        .structure(Formula.deserialize("destroy:linear:CC(=O)OC"))
        .boilingPoint(56.9f)
        .density(932f)
        .molarHeatCapacity(140.2f)
        .build(),
    
    NICKEL_ION = builder()
        .id("nickel_ion")
        .translationKey("nickel")
        .structure(Formula.atom(Element.NICKEL))
        .charge(2)
        .build(),

    NITRATE = builder()
        .id("nitrate")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
            .addAtom(Element.OXYGEN, BondType.AROMATIC)
        ).charge(-1)
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
        .build(),

    NITROGEN = builder()
        .id("nitrogen")
        .structure(Formula.deserialize("destroy:linear:N#N"))
        .boilingPointInKelvins(77.355f)
        .density(1.2506f)
        .molarHeatCapacity(29.12f)
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
        .build(),

    NITROGLYCERINE = builder()
        .id("nitroglycerine")
        .structure(Formula.deserialize("destroy:linear:C(ON(~O)(~O))C(ON(~O)(~O))CON(~O)(~O)"))
        .boilingPoint(50f)
        .density(1600f)
        // Heat capacity unknown
        .build(),

    NITRONIUM = builder()
        .id("nitronium")
        .structure(Formula.atom(Element.NITROGEN)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
        ).charge(1)
        .build(),

    ORTHOXYLENE = builder()
        .id("orthoxylene")
        .structure(Formula.deserialize("destroy:benzene:C,C,,,,"))
        .boilingPoint(144.4f)
        .density(880f)
        .molarHeatCapacity(187.1f)
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
        .density(1.429f)
        .molarHeatCapacity(29.378f)
        .build(),

    PARAXYLENE = builder()
        .id("paraxylene")
        .structure(Formula.deserialize("destroy:benzene:C,,,C,,"))
        .boilingPoint(138.35f)
        .density(861f)
        .molarHeatCapacity(182f)
        .build(),

    PHENOL = builder()
        .id("phenol")
        .structure(Formula.deserialize("destroy:benzene:O,,,,,"))
        .boilingPoint(181.7f)
        .density(1070f)
        .molarHeatCapacity(220.9f)
        .build(),

    PHENYLACETIC_ACID = builder()
        .id("phenylacetic_acid")
        .structure(Formula.deserialize("destroy:benzene:CC(=O)O"))
        .boilingPoint(265.5f)
        .density(1080.9f)
        .molarHeatCapacity(170f) // Estimate based on isomers
        .build(),

    PHENYLACETONE = builder()
        .id("phenylacetone")
        .structure(Formula.deserialize("destroy:benzene:CC(=O)C"))
        .boilingPoint(215f)
        .density(1006f)
        .molarHeatCapacity(250f) // Estimate based on isomers
        .build(),
    
    PHTHALIC_ANHYDRIDE = builder()
        .id("phthalic_anhydride")
        .structure(Formula.deserialize("destroy:isohydrobenzofuran:,,,,=O,,=O,,"))
        .boilingPoint(295f)
        .density(1530f)
        .molarHeatCapacity(161.8f)
        .build(),

    PICRIC_ACID = builder()
        .id("picric_acid")
        .structure(Formula.deserialize("destroy:benzene:O,N(~O)~O,C,N(~O)~O,,N(~O)~O"))
        .color(0xC0ED7417)
        .boilingPoint(300f) // Detonates before this
        .density(1763f)
        .molarHeatCapacity(200f) // Estimate, couldn't find heat capacity
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
        .build(),

    SALICYLIC_ACID = builder()
        .id("salicylic_acid")
        .structure(Formula.deserialize("destroy:benzene:CC(=O)O,O,,,,"))
        .boilingPoint(200f)
        .density(1443f)
        .molarHeatCapacity(159.4f)
        .build(),

    SODIUM_ION = builder()
        .id("sodium_ion")
        .translationKey("sodium")
        .structure(Formula.atom(Element.SODIUM))
        .charge(1)
        .build(),

    STYRENE = builder()
        .id("styrene")
        .structure(Formula.deserialize("destroy:benzene:C=C,,,,,"))
        .boilingPoint(145f)
        .density(909f)
        .molarHeatCapacity(183.2f)
        .build(),

    SULFATE = builder()
        .id("sulfate")
        .structure(Formula.atom(Element.SULFUR)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.DOUBLE)
            .addAtom(Element.OXYGEN, BondType.SINGLE)
            .addAtom(Element.OXYGEN, BondType.SINGLE)
        ).charge(-2)
        .build(),

    SULFIDE = builder()
        .id("sulfide")
        .structure(Formula.atom(Element.SULFUR))
        .charge(-2)
        .build(),

    SULFURIC_ACID = builder()
        .id("sulfuric_acid")
        .structure(Formula.deserialize("destroy:linear:OS(=O)(=O)O"))
        .boilingPoint(337f)
        .density(1830.2f)
        .molarHeatCapacity(83.68f)
        .build(),

    TETRAFLUOROETHENE = builder()
        .id("tetrafluoroethene")
        .structure(Formula.deserialize("destroy:linear:FC=(F)C(F)F"))
        .boilingPoint(-76.3f)
        .density(1519f)
        // Couldn't find heat capacity
        .build(),
    
    TOLUENE = builder()
        .id("toluene")
        .structure(Formula.deserialize("destroy:benzene:C,,,,,"))
        .boilingPoint(110.6f)
        .density(862.3f)
        .molarHeatCapacity(157.3f)
        .build(),

    TRICHLOROFLUOROMETHANE = builder()
        .id("trichlorofluoromethane")
        .structure(Formula.deserialize("destroy:linear:FC(Cl)(Cl)Cl"))
        .boilingPoint(23.77f)
        .density(1494f)
        .molarHeatCapacity(122.5f)
        .tag(Tags.REFRIGERANT)
        .build(),

    TRIMETHYLAMINE = builder()
        .id("trimethylamine")
        .structure(Formula.deserialize("destroy:linear:CN(C)C"))
        .boilingPoint(5f)
        .density(670f)
        .molarHeatCapacity(132.55f)
        .build(),
    
    TRINITROTOLUENE = builder()
        .id("tnt")
        .structure(Formula.deserialize("destroy:benzene:N(~O)~O,C,N(~O)~O,,N(~O)~O,"))
        .color(0xD0FCF1E8)
        .boilingPoint(240f) // Decomposes
        .density(1654f)
        .molarHeatCapacity(243.3f)
        .build(),

    VINYL_ACETATE = builder()
        .id("vinyl_acetate")
        .structure(Formula.deserialize("destroy:linear:C=COC(=O)C"))
        .boilingPoint(72.7f)
        .density(934f)
        .molarHeatCapacity(169.5f)
        .build(),

    WATER = builder()
        .id("water")
        .structure(Formula.deserialize("destroy:linear:O"))
        .tag(Tags.SOLVENT)
        .boilingPoint(100f)
        .density(1000f)
        .specificHeatCapacity(4160f)
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

        public static final MoleculeTag

        /**
         * This Molecule automatically has a very high 'concentration' when in open Vats and Basins,
         * and can be used up as much as necessary without depleting.
         */
        ABUNDANT_IN_AIR = new MoleculeTag(),

        /**
         * This Molecule will cause damage to Players exposed to it.
         */
        ACUTELY_TOXIC = new MoleculeTag(),

        /**
         * This Molecule cannot partake in Reactions.
         */
        HYPOTHETICAL = new MoleculeTag(),

        /**
         * This Molecule can be used bas 'fuel' for the Cooler.
         */
        REFRIGERANT = new MoleculeTag(),

        /**
         * This Molecule is ignored when displaying the written contents of a Mixture, and ignored when used in Recipes.
         */
        SOLVENT = new MoleculeTag();
    };

    public static void register() {};
};
