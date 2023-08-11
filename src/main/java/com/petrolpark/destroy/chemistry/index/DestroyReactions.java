package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.reactionResult.PrecipitateReactionResult;
import com.petrolpark.destroy.item.DestroyItems;

public class DestroyReactions {

    public static final Reaction

    ACETONE_CYANOHYDRIN_SYNTHESIS = builder()
        .id("acetone_cyanohydrin_synthesis")
        .addReactant(DestroyMolecules.CYANIDE)
        .addReactant(DestroyMolecules.ACETONE)
        .addReactant(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.ACETONE_CYANOHYDRIN)
        .build(), // This is a generic Cyanide addition-elimination and will be removed in place of the automatically generated reaction

    AIBN_SYNTHESIS = builder()
        .id("aibn_synthesis")
        .addReactant(DestroyMolecules.ACETONE_CYANOHYDRIN, 2)
        .addReactant(DestroyMolecules.HYDRAZINE)
        .addReactant(DestroyMolecules.CHLORINE)
        .addProduct(DestroyMolecules.AIBN)
        .addProduct(DestroyMolecules.WATER, 2)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID, 2)
        .build(),

    CHLORINE_HALOFORM_REACTION = builder()
        .id("chlorine_haloform_reaction")
        .addReactant(DestroyMolecules.HYPOCHLORITE, 3, 0)
        .addReactant(DestroyMolecules.ACETONE)
        .addProduct(DestroyMolecules.ACETATE)
        .addProduct(DestroyMolecules.CHLOROFORM)
        .addProduct(DestroyMolecules.HYDROXIDE, 2)
        .addCatalyst(DestroyMolecules.HYDROXIDE, 0)
        .build(),

    CHLORODIFLUOROMETHANE_PYROLYSIS = builder()
        .id("chlorodifluoromethane_pyrolysis")
        .addReactant(DestroyMolecules.CHLORODIFLUOROMETHANE, 2)
        .addProduct(DestroyMolecules.HYDROFLUORIC_ACID, 2)
        .addProduct(DestroyMolecules.TETRAFLUOROETHENE)
        .build(),

    CHLOROFORM_FLUORINATION = builder()
        .id("chloroform_fluorination")
        .addReactant(DestroyMolecules.CHLOROFORM)
        .addReactant(DestroyMolecules.HYDROFLUORIC_ACID, 2)
        .addProduct(DestroyMolecules.CHLORODIFLUOROMETHANE)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID, 2)
        .build(),
    
    //TODO contact process
    //TODO cisplatin synthesis
    //TODO possible cyclopentanone/cyclopentadiene interconversion

    ETHYLANTHRAQUINONE_SYNTHESIS = builder()
        .id("ethylanthraquinone_synthesis")
        .addReactant(DestroyMolecules.PHTHALIC_ANHYDRIDE)
        .addReactant(DestroyMolecules.ETHYLBENZENE)
        .addProduct(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.ETHYLANTHRAQUINONE) //TODO add zeolite catalyst
        .build(),

    ETHYLBENZENE_DEHYDROGENATION = builder()
        .id("ethylbenzene_dehydrogenation")
        .addReactant(DestroyMolecules.ETHYLBENZENE)
        .addCatalyst(DestroyMolecules.WATER, 2)
        .addCatalyst(DestroyMolecules.IRON_III)
        .addProduct(DestroyMolecules.STYRENE)
        .addProduct(DestroyMolecules.HYDROGEN)
        .build(), //TODO ensure superheated and carefully balance rate constant with that of hydrogenation of styrene

    GLYCEROL_NITRATION = builder()
        .id("glycerol_nitration")
        .addReactant(DestroyMolecules.GLYCEROL)
        .addReactant(DestroyMolecules.NITRONIUM, 3)
        .addProduct(DestroyMolecules.PROTON, 3)
        .addProduct(DestroyMolecules.NITROGLYCERINE)
        .build(),

    GOLD_DISSOLUTION = builder()
        .id("gold_dissolution")
        //TODO add gold carrot item
        .addReactant(DestroyMolecules.NITRIC_ACID)
        .addReactant(DestroyMolecules.HYDROCHLORIC_ACID, 4, 1)
        .addProduct(DestroyMolecules.CHLOROAURATE)
        .addProduct(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER, 3)
        .addProduct(DestroyMolecules.NITROGEN_DIOXIDE, 3)
        .build(),

    //TODO iodine dissolution and precipitation
        
    HYDROGEN_IODIDE_SYNTHESIS = builder()
        .id("hydrogen_iodide_synthesis")
        .addReactant(DestroyMolecules.HYDRAZINE)
        .addReactant(DestroyMolecules.IODINE, 2)
        .addProduct(DestroyMolecules.HYDROGEN_IODIDE, 4)
        .addProduct(DestroyMolecules.NITROGEN)
        .build(),

    MERCURY_FULMINATION = builder()
        .id("mercury_fulmination")
        .addReactant(DestroyMolecules.MERCURY, 3, 1)
        .addReactant(DestroyMolecules.NITRIC_ACID, 12, 2)
        .addReactant(DestroyMolecules.ETHANOL, 4, 1)
        .addProduct(DestroyMolecules.CARBON_DIOXIDE, 2)
        .addProduct(DestroyMolecules.WATER, 18)
        .addProduct(DestroyMolecules.NITROGEN_DIOXIDE, 6)
        .withResult(1f (m, r) -> new PrecipitateReactionResult(m, r, () -> DestroyItems.FULMINATED_MERCURY.asStack())) //TODO figure out actual molar ratios
        .build(),

    METHYL_ACETATE_CARBONYLATION = builder()
        .id("methyl_acetate_carbonylation")
        .addReactant(DestroyMolecules.METHANOL)
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .build(), //TODO silica catalyst

    NHN_SYNTHESIS = builder()
        .id("nhn_synthesis")
        .addReactant(DestroyMolecules.NICKEL_ION)
        .addReactant(DestroyMolecules.NITRATE, 2, 0)
        .addReactant(DestroyMolecules.HYDRAZINE, 3)
        .withResult(3f (m, r) -> new PrecipitateReactionResult(m, r, () -> DestroyItems.NICKEL_HYDRAZINE_NITRATE.asStack())) //TODO figure out actual molar ratios
        .build(),

    NITRONIUM_FORMATION = builder()
        .id("nitronium_formation")
        .addReactant(DestroyMolecules.NITRIC_ACID)
        .addReactant(DestroyMolecules.SULFURIC_ACID)
        .addProduct(DestroyMolecules.NITRONIUM)
        .addProduct(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.HYDROGENSULFATE)
        .build(), //TODO make reversible

    NYLON_POLYMERISATION = builder()
        .id("nylon_polymerisation")
        .addReactant(DestroyMolecules.ADIPIC_ACID)
        .addReactant(DestroyMolecules.HEXANEDIAMINE)
        .withResult(3f, (m, r) -> new PrecipitateReactionResult(m, r, () -> DestroyItems.NYLON.asStack())) //TODO work out proportions
        .build(),

    ORTHOXYLENE_OXIDATION = builder()
        .id("orthoxylene_oxidation")
        .addReactant(DestroyMolecules.ORTHOXYLENE)
        .addReactant(DestroyMolecules.OXYGEN, 3, 1)
        .addCatalyst(DestroyMolecules.MERCURY)
        .addProduct(DestroyMolecules.PHTHALIC_ANHYDRIDE)
        .build(),

    //TODO phenylacetic acid synthesis, either from benzyl chloride or benzyl cyanide
    //TODO dissolution of prussian blue to make cyanide (or just remove prussian blue)

    HYDROXIDE_NEUTRALIZATION = builder()
        .id("hydroxide_neutralization")
        .addReactant(DestroyMolecules.HYDROXIDE)
        .addReactant(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER)
        .activationEnergy(0f)
        .preexponentialFactor(1e14f)
        .build(),

    TATP = builder()
        .id("tatp")
        .addReactant(DestroyMolecules.ACETONE)
        .addReactant(DestroyMolecules.HYDROGEN_PEROXIDE)
        // TODO acid catalyst
        .withResult(3f, (m, r) -> new PrecipitateReactionResult(m, r, () -> DestroyItems.ACETONE_PEROXIDE.asStack()))
        .build();

    private static ReactionBuilder builder() {
        return new ReactionBuilder(Destroy.MOD_ID);
    };

    public static void register() {};
}
