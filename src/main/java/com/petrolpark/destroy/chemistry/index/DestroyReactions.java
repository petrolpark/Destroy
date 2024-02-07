package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.Reaction.ReactionBuilder;
import com.petrolpark.destroy.chemistry.reactionresult.CombinedReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.ExplosionReactionResult;
import com.petrolpark.destroy.chemistry.reactionresult.PrecipitateReactionResult;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;

import net.minecraft.world.item.Items;

public class DestroyReactions {

    public static final Reaction

    ABS_COPOLYMERIZATION = builder()
        .id("abs_copolymerization")
        .addReactant(DestroyMolecules.ACRYLONITRILE)
        .addReactant(DestroyMolecules.BUTADIENE)
        .addReactant(DestroyMolecules.STYRENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(1f, PrecipitateReactionResult.of(DestroyItems.ABS::asStack))
        .preexponentialFactor(15f)
        .activationEnergy(20f)
        .build(),

    AIBN_SYNTHESIS = builder()
        .id("aibn_synthesis")
        .addReactant(DestroyMolecules.ACETONE_CYANOHYDRIN, 2)
        .addReactant(DestroyMolecules.HYDRAZINE)
        .addReactant(DestroyMolecules.CHLORINE)
        .addProduct(DestroyMolecules.AIBN)
        .addProduct(DestroyMolecules.WATER, 2)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID, 2)
        .build(),

    ANDRUSSOW_PROCESS = builder()
        .id("andrussow_process")
        .addReactant(DestroyMolecules.METHANE, 2, 1)
        .addReactant(DestroyMolecules.AMMONIA, 2, 1)
        .addReactant(DestroyMolecules.OXYGEN, 3, 1)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/platinum"), 1f)
        .addProduct(DestroyMolecules.HYDROGEN_CYANIDE, 2)
        .addProduct(DestroyMolecules.WATER, 6)
        .activationEnergy(1000) //TODO tweak to make Reaction require very high temperatures
        .build(),

    ANTHRAQUINONE_PROCESS = builder()
        .id("anthraquinone_process")
        .addReactant(DestroyMolecules.ETHYLANTHRAHYDROQUINONE)
        .addReactant(DestroyMolecules.OXYGEN)
        .addProduct(DestroyMolecules.ETHYLANTHRAQUINONE)
        .addProduct(DestroyMolecules.HYDROGEN_PEROXIDE)
        .build(),

    ANTHRAQUINONE_REDUCTION = builder()
        .id("anthraquinone_reduction")
        .addReactant(DestroyMolecules.ETHYLANTHRAQUINONE)
        .addReactant(DestroyMolecules.HYDROGEN)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/palladium"), 1f)
        .addProduct(DestroyMolecules.ETHYLANTHRAHYDROQUINONE)
        .build(),

    BABY_BLUE_PRECIPITATION = builder()
        .id("baby_blue_precipitation")
        .addReactant(DestroyMolecules.METHYL_SALICYLATE)
        .addCatalyst(DestroyMolecules.SODIUM_ION, 0)
        .withResult(0.9f, PrecipitateReactionResult.of(DestroyItems.BABY_BLUE_CRYSTAL::asStack))
        .build(),

    BENZENE_ETHYLATION = builder()
        .id("benzene_ethylation")
        .addReactant(DestroyMolecules.BENZENE)
        .addReactant(DestroyMolecules.ETHENE)
        .addCatalyst(DestroyMolecules.PROTON, 1)
        .addProduct(DestroyMolecules.ETHYLBENZENE)
        .build(),

    BENZENE_HYDROGENATION = builder()
        .id("benzene_hydrogenation")
        .addReactant(DestroyMolecules.BENZENE)
        .addReactant(DestroyMolecules.HYDROGEN, 2, 1)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/nickel"), 1f)
        .addProduct(DestroyMolecules.CYCLOHEXENE)
        .build(),

    BUTADIENE_CARBONYLATION = builder()
        .id("butadiene_carbonylation")
        .addReactant(DestroyMolecules.BUTADIENE)
        .addReactant(DestroyMolecules.CARBON_MONOXIDE, 2)
        .addReactant(DestroyMolecules.WATER, 2, 1)
        .addProduct(DestroyMolecules.ADIPIC_ACID)
        .build(),

    CARBON_CAPTURE = builder()
        .id("carbon_capture")
        .addReactant(DestroyMolecules.CALCIUM_ION)
        .addReactant(DestroyMolecules.CARBON_DIOXIDE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.PROTON, 2)
        .withResult(2f, PrecipitateReactionResult.of(DestroyItems.CHALK_DUST::asStack))
        .build(),

    CARBON_MONOXIDE_OXIDATION = builder()
        .id("carbon_monoxide_oxidation")
        .addReactant(DestroyMolecules.CARBON_MONOXIDE, 2, 1)
        .addReactant(DestroyMolecules.OXYGEN)
        .addProduct(DestroyMolecules.CARBON_DIOXIDE, 2)
        .build(),

    CARBON_TETRACHLORIDE_FLUORINATION = builder()
        .id("carbon_tetrachloride_fluorination")
        .addReactant(DestroyMolecules.CARBON_TETRACHLORIDE, 2, 1)
        .addReactant(DestroyMolecules.HYDROFLUORIC_ACID, 3, 1)
        .addProduct(DestroyMolecules.DICHLORODIFLUOROMETHANE)
        .addProduct(DestroyMolecules.TRICHLOROFLUOROMETHANE)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID, 3)
        .build(), //TODO eventually replace with generic halogen substitution reaction

    CELLULOSE_NITRATION = builder()
        .id("cellulose_nitration")
        .addReactant(DestroyMolecules.NITRONIUM)
        .addSimpleItemReactant(DestroyItems.PAPER_PULP, 2f)
        .addProduct(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER) //TODO in future add oxalic acid side product
        .withResult(2f, PrecipitateReactionResult.of(DestroyItems.NITROCELLULOSE::asStack))
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

    CHLORINE_SOLVATION = builder()
        .id("chlorine_solvation")
        .addReactant(DestroyMolecules.CHLORINE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID)
        .addProduct(DestroyMolecules.HYPOCHLOROUS_ACID)
        .requireUV()
        .reverseReaction(reaction -> {})
        .build(),

    //TODO add fluorine/chlorine substitution
    //TODO add UV chlorination with Chlorine gas

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

    CISPLATIN_SYNTHESIS = builder()
        .id("cisplatin_synthesis")
        .addReactant(DestroyMolecules.CHLORIDE)
        .addReactant(DestroyMolecules.AMMONIA)
        .addSimpleItemTagReactant(AllTags.forgeItemTag("dusts/platinum"), 2f)
        .addProduct(DestroyMolecules.CISPLATIN)
        .addProduct(DestroyMolecules.HYDROXIDE) //TODO change as this is not quite right
        .build(),

    CONTACT_PROCESS = builder()
        .id("contact_process")
        .addReactant(DestroyMolecules.SULFUR_DIOXIDE, 2, 1)
        .addReactant(DestroyMolecules.OXYGEN)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/platinum"), 3f)
        .addProduct(DestroyMolecules.SULFUR_TRIOXIDE, 2)
        .build(),

    CORDITE_PRECIPITATION = builder()
        .id("cordite_precipitation")
        .addReactant(DestroyMolecules.ACETONE)
        .addReactant(DestroyMolecules.NITROGLYCERINE)
        .addSimpleItemReactant(DestroyItems.NITROCELLULOSE::get, 1f)
        .withResult(2.99f, PrecipitateReactionResult.of(DestroyBlocks.CORDITE_BLOCK::asStack))
        .build(),

    CUMENE_PROCESS = builder()
        .id("cumene_process")
        .addReactant(DestroyMolecules.BENZENE)
        .addReactant(DestroyMolecules.PROPENE)
        .addReactant(DestroyMolecules.OXYGEN)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .addCatalyst(DestroyMolecules.PROTON, 1)
        //TODO add Lewis acid catalyst
        .addProduct(DestroyMolecules.PHENOL)
        .addProduct(DestroyMolecules.ACETONE)
        .build(),

    CYCLOHEXENE_OXIDATIVE_CLEAVAGE = builder()
        .id("cyclohexene_oxidative_cleavage")
        .addReactant(DestroyMolecules.CYCLOHEXENE)
        .addReactant(DestroyMolecules.HYDROGEN_PEROXIDE, 3, 1)
        .addProduct(DestroyMolecules.ADIPIC_ACID)
        .addProduct(DestroyMolecules.WATER, 2)
        .build(), //TODO add "tungsten" catalyst. This reaction also has as phase-transfer catalyst but I have chosen to ignore it.
    
    //TODO possible cyclopentanone/cyclopentadiene interconversion
    //TODO cyclopentanone synthesis from adipic acid

    BASIC_DIELS_ALDER_REACTION = builder()
        .id("basic_diels_alder_reaction")
        .addReactant(DestroyMolecules.BUTADIENE)
        .addReactant(DestroyMolecules.ETHENE)
        .addProduct(DestroyMolecules.CYCLOHEXENE)
        .build(),

    COPPER_DISSOLUTION = builder() //TODO replace with redox
        .id("copper_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemTagReactant(AllTags.forgeItemTag("dusts/copper"), 0.9f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.COPPER_II)
        .build(),

    COPPER_ORE_DISSOLUTION = builder() //TODO replace with redox
        .id("copper_ore_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemReactant(AllItems.CRUSHED_COPPER::get, 1.5f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.COPPER_II)
        .build(),

    ETHENE_POLYMERIZATION = builder()
        .id("ethene_polymerization")
        .addReactant(DestroyMolecules.ETHENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.POLYETHENE::asStack))
        .preexponentialFactor(10f)
        .activationEnergy(10f)
        .build(),

    ETHYLANTHRAQUINONE_SYNTHESIS = builder()
        .id("ethylanthraquinone_synthesis")
        .addReactant(DestroyMolecules.PHTHALIC_ANHYDRIDE)
        .addReactant(DestroyMolecules.ETHYLBENZENE)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.ETHYLANTHRAQUINONE)
        .build(),

    ETHYLBENZENE_DEHYDROGENATION = builder()
        .id("ethylbenzene_dehydrogenation")
        .addReactant(DestroyMolecules.ETHYLBENZENE)
        .addCatalyst(DestroyMolecules.WATER, 2)
        .addCatalyst(DestroyMolecules.IRON_III, 1)
        .addProduct(DestroyMolecules.STYRENE)
        .addProduct(DestroyMolecules.HYDROGEN)
        .build(), //TODO ensure superheated and carefully balance rate constant with that of hydrogenation of styrene

    ETHYLBENZENE_TRANSALKYLATION = builder()
        .id("ethylbenzene_transalkylation")
        .addReactant(DestroyMolecules.ETHYLBENZENE, 3)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.METAXYLENE)
        .addProduct(DestroyMolecules.ORTHOXYLENE)
        .addProduct(DestroyMolecules.PARAXYLENE)
        .build(),

    FLUORITE_DISSOLUTION = builder()
        .id("fluorite_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemReactant(DestroyItems.FLUORITE::get, 5f)
        .addProduct(DestroyMolecules.CALCIUM_ION)
        .addProduct(DestroyMolecules.HYDROFLUORIC_ACID, 2)
        .build(),

    GLYCEROL_NITRATION = builder()
        .id("glycerol_nitration")
        .addReactant(DestroyMolecules.GLYCEROL)
        .addReactant(DestroyMolecules.NITRONIUM, 3)
        .addProduct(DestroyMolecules.PROTON, 3)
        .addProduct(DestroyMolecules.NITROGLYCERINE)
        .build(),

    GOLD_DISSOLUTION = builder()
        .id("gold_dissolution")
        .addSimpleItemReactant(() -> Items.GOLDEN_CARROT, 1f)
        .addReactant(DestroyMolecules.NITRIC_ACID)
        .addReactant(DestroyMolecules.HYDROCHLORIC_ACID, 4, 1)
        .addProduct(DestroyMolecules.CHLOROAURATE)
        .addProduct(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER, 3)
        .addProduct(DestroyMolecules.NITROGEN_DIOXIDE, 3)
        .build(),

    //TODO iodine dissolution and precipitation

    HABER_PROCESS = builder()
        .id("haber_process")
        .addReactant(DestroyMolecules.NITROGEN)
        .addReactant(DestroyMolecules.HYDROGEN, 3, 0)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/iron"), 1f)
        .addProduct(DestroyMolecules.AMMONIA, 2)
        .build(), //TODO add reversibility and appropriate rate constants
    //TODO add oxidation of nitrogen in air so this is more difficult
        
    HYDROGEN_CHLORIDE_SYNTHESIS = builder()
        .id("hydrogen_chloride_synthesis")
        .addReactant(DestroyMolecules.HYDROGEN)
        .addReactant(DestroyMolecules.CHLORINE)
        .addProduct(DestroyMolecules.HYDROCHLORIC_ACID, 2)
        .requireUV()
        .build(),

    HYDROGEN_COMBUSTION = builder()
        .id("hydrogen_combustion")
        .addReactant(DestroyMolecules.HYDROGEN, 2, 1)
        .addReactant(DestroyMolecules.OXYGEN)
        .addProduct(DestroyMolecules.WATER, 2)
        .preexponentialFactor(1e10f)
        .activationEnergy(100f)
        .enthalpyChange(-500f)
        .build(),
    
    HYDROGEN_IODIDE_SYNTHESIS = builder()
        .id("hydrogen_iodide_synthesis")
        .addReactant(DestroyMolecules.HYDRAZINE)
        .addReactant(DestroyMolecules.IODINE, 2)
        .addProduct(DestroyMolecules.HYDROGEN_IODIDE, 4)
        .addProduct(DestroyMolecules.NITROGEN)
        .build(),
    
    HYDROXIDE_NEUTRALIZATION = builder()
        .id("hydroxide_neutralization")
        .addReactant(DestroyMolecules.HYDROXIDE)
        .addReactant(DestroyMolecules.PROTON)
        .addProduct(DestroyMolecules.WATER)
        .activationEnergy(0f)
        .preexponentialFactor(6.5e4f) //TODO fiddle with values because currently Water is the most acidic substance in the mod
        .enthalpyChange(-52.014f)
        .reverseReaction(reaction -> reaction
            .activationEnergy(52.014f)
            .preexponentialFactor(519.5f)
            .setOrder(DestroyMolecules.WATER, 2)
        ).build(),

    HYPOCHLORITE_FORMATION = builder()
        .id("hypochlorite_formation")
        .addReactant(DestroyMolecules.CHLORINE)
        .addReactant(DestroyMolecules.HYDROXIDE, 2, 1)
        .addCatalyst(DestroyMolecules.SODIUM_ION, 1)
        .addProduct(DestroyMolecules.CHLORIDE)
        .addProduct(DestroyMolecules.HYPOCHLORITE)
        .addProduct(DestroyMolecules.WATER)
        .requireUV() //TODO add reverse reaction
        .build(),

    IODIDE_DISPLACEMENT = builder()
        .id("iodide_displacement")
        .addReactant(DestroyMolecules.IODIDE, 2, 1)
        .addReactant(DestroyMolecules.CHLORINE)
        .addProduct(DestroyMolecules.IODINE)
        .addProduct(DestroyMolecules.CHLORIDE, 2)
        .build(),

    IODINE_DISSOLUTION = builder()
        .id("iodine_dissolution")
        .addReactant(DestroyMolecules.WATER, 0)
        .addSimpleItemReactant(DestroyItems.IODINE::get, 2f)
        .addProduct(DestroyMolecules.IODINE)
        .reverseReaction(reaction -> reaction
            .withResult(2.1f, PrecipitateReactionResult.of(DestroyItems.IODINE::asStack))
        ).build(),

    IRON_DISSOLUTION = builder() //TODO replace with redox
        .id("iron_dissolution")
        .addReactant(DestroyMolecules.PROTON, 6, 1)
        .addSimpleItemTagReactant(AllTags.forgeItemTag("dusts/iron"), 0.45f)
        .addProduct(DestroyMolecules.HYDROGEN, 3)
        .addProduct(DestroyMolecules.IRON_III, 2)
        .build(),

    IRON_ORE_DISSOLUTION = builder() //TODO replace with redox
        .id("iron_ore_dissolution")
        .addReactant(DestroyMolecules.PROTON, 6, 1)
        .addSimpleItemReactant(AllItems.CRUSHED_IRON::get, 0.75f)
        .addProduct(DestroyMolecules.HYDROGEN, 3)
        .addProduct(DestroyMolecules.IRON_III, 2)
        .build(),

    KELP_DISSOLUTION = builder()
        .id("kelp_dissolution")
        .addSimpleItemReactant(() -> Items.DRIED_KELP, 1f)
        .addReactant(DestroyMolecules.ETHANOL, 0)
        .addProduct(DestroyMolecules.POTASSIUM_ION)
        .addProduct(DestroyMolecules.IODIDE)
        .build(),

    KOLBE_SCHMITT_REACTION = builder()
        .id("kolbe_schmitt_reaction")
        .addReactant(DestroyMolecules.CARBON_DIOXIDE)
        .addReactant(DestroyMolecules.PHENOL)
        .addCatalyst(DestroyMolecules.SODIUM_ION, 1) //TODO actually add sodium phenoxide intermediate
        .addCatalyst(DestroyMolecules.PROTON, 1)
        .addProduct(DestroyMolecules.SALICYLIC_ACID)
        .build(),

    MERCURY_FULMINATION = builder()
        .id("mercury_fulmination")
        .addReactant(DestroyMolecules.MERCURY, 3, 1)
        .addReactant(DestroyMolecules.NITRIC_ACID, 12, 2)
        .addReactant(DestroyMolecules.ETHANOL, 4, 1)
        .addProduct(DestroyMolecules.CARBON_DIOXIDE, 2)
        .addProduct(DestroyMolecules.WATER, 18)
        .addProduct(DestroyMolecules.NITROGEN_DIOXIDE, 6)
        .withResult(1f, PrecipitateReactionResult.of(DestroyItems.FULMINATED_MERCURY::asStack))
        .build(),

    METAXYLENE_TRANSALKYLATION = builder()
        .id("metaxylene_transalkylation")
        .addReactant(DestroyMolecules.METAXYLENE, 3)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.ORTHOXYLENE)
        .addProduct(DestroyMolecules.PARAXYLENE)
        .addProduct(DestroyMolecules.ETHYLBENZENE)
        .build(),

    METHANOL_SYNTHESIS = builder()
        .id("methanol_synthesis")
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addReactant(DestroyMolecules.HYDROGEN, 2, 1) //TODO check rate exponent of hydrogen
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/copper"), 1f)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/zinc"), 1f)
        .addProduct(DestroyMolecules.METHANOL)
        .build(), //TODO ensure high pressure is used

    METHYL_ACETATE_CARBONYLATION = builder()
        .id("methyl_acetate_carbonylation")
        .addReactant(DestroyMolecules.METHANOL)
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addSimpleItemCatalyst(DestroyItems.SILICA::get, 1f)
        .addProduct(DestroyMolecules.ACETIC_ACID)
        .build(),

    NAUGHTY_REACTION = builder()
        .id("naughty_reaction")
        .addReactant(DestroyMolecules.PHENYLACETONE)
        .addReactant(DestroyMolecules.METHYLAMINE)
        .withResult(0f, (m, r) -> new CombinedReactionResult(m, r)
            .with(ExplosionReactionResult::small)
            .with(DestroyAdvancements.TRY_TO_MAKE_METH::asReactionResult)
        ).dontIncludeInJei()
        .build(),

    NHN_SYNTHESIS = builder()
        .id("nhn_synthesis")
        .addReactant(DestroyMolecules.NICKEL_ION)
        .addReactant(DestroyMolecules.NITRATE, 2, 0)
        .addReactant(DestroyMolecules.HYDRAZINE, 3)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.NICKEL_HYDRAZINE_NITRATE::asStack)) //TODO figure out actual molar ratios
        .build(),

    NICKEL_DISSOLUTION = builder() //TODO replace with redox
        .id("nickel_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemTagReactant(AllTags.forgeItemTag("dusts/nickel"), 0.9f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.NICKEL_ION)
        .build(),

    NICKEL_ORE_DISSOLUTION = builder() //TODO replace with redox
        .id("nickel_ore_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemReactant(AllItems.CRUSHED_NICKEL::get, 1.5f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.NICKEL_ION)
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
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.NYLON::asStack)) //TODO work out proportions
        .build(),

    OLEUM_FORMATION = builder()
        .id("oleum_formation")
        .addReactant(DestroyMolecules.SULFURIC_ACID)
        .addReactant(DestroyMolecules.SULFUR_TRIOXIDE)
        .addProduct(DestroyMolecules.OLEUM)
        .preexponentialFactor(2e4f) 
        .reverseReaction(r -> r
            .preexponentialFactor(5e3f)
        ).build(),

    OLEUM_HYDRATION = builder()
        .id("oleum_hydration")
        .addReactant(DestroyMolecules.OLEUM)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.SULFURIC_ACID, 2)
        .preexponentialFactor(1e10f)
        .activationEnergy(2f)
        .build(),

    ORTHOXYLENE_OXIDATION = builder()
        .id("orthoxylene_oxidation")
        .addReactant(DestroyMolecules.ORTHOXYLENE)
        .addReactant(DestroyMolecules.OXYGEN, 3, 1)
        .addCatalyst(DestroyMolecules.MERCURY, 1)
        .addProduct(DestroyMolecules.PHTHALIC_ANHYDRIDE)
        .build(),

    ORTHOXYLENE_TRANSALKYLATION = builder()
        .id("orthoxylene_transalkylation")
        .addReactant(DestroyMolecules.ORTHOXYLENE, 3)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.METAXYLENE)
        .addProduct(DestroyMolecules.PARAXYLENE)
        .addProduct(DestroyMolecules.ETHYLBENZENE)
        .build(),

    OSTWALD_PROCESS = builder()
        .id("ostwald_process")
        .addReactant(DestroyMolecules.AMMONIA)
        .addReactant(DestroyMolecules.OXYGEN, 2)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/rhodium"), 1f)
        .addProduct(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.NITRIC_ACID)
        .withResult(0f, DestroyAdvancements.OSTWALD_PROCESS::asReactionResult)
        .build(), //TODO potentially split into multiple equations, and add side reactions

    //TODO phenylacetic acid synthesis, either from benzyl chloride or benzyl cyanide

    PARAXYLENE_TRANSALKYLATION = builder()
        .id("paraxylene_transalkylation")
        .addReactant(DestroyMolecules.PARAXYLENE, 3)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.METAXYLENE)
        .addProduct(DestroyMolecules.ORTHOXYLENE)
        .addProduct(DestroyMolecules.ETHYLBENZENE)
        .build(),

    PEROXIDE_PROCESS = builder()
        .id("peroxide_process")
        .addReactant(DestroyMolecules.HYDROGEN_PEROXIDE)
        .addReactant(DestroyMolecules.AMMONIA, 2, 1)
        .addCatalyst(DestroyMolecules.ACETONE, 1) //TODO possibly replace with butanone
        .addCatalyst(DestroyMolecules.PROTON, 0)
        .addProduct(DestroyMolecules.HYDRAZINE)
        .addProduct(DestroyMolecules.WATER, 2)
        .build(),

    PHENOL_NITRATION = builder()
        .id("phenol_nitration")
        .addReactant(DestroyMolecules.PHENOL)
        .addReactant(DestroyMolecules.NITRONIUM, 3, 1)
        .addProduct(DestroyMolecules.PICRIC_ACID)
        .addProduct(DestroyMolecules.PROTON, 3)
        .build(),

    PHOSGENE_FORMATION = builder()
        .id("phosgene_formation")
        .addReactant(DestroyMolecules.CARBON_MONOXIDE)
        .addReactant(DestroyMolecules.CHLORINE)
        .addProduct(DestroyMolecules.PHOSGENE)
        .enthalpyChange(-107.6f)
        .build(),

    PROPENE_POLYMERIZATION = builder()
        .id("propene_polymerization")
        .addReactant(DestroyMolecules.PROPENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.POLYPROPENE::asStack))
        .preexponentialFactor(10f)
        .activationEnergy(10f)
        .build(),

    SODIUM_DISSOLUTION = builder()
        .id("sodium_dissolution")
        .addReactant(DestroyMolecules.SODIUM_METAL, 2, 1)
        .addReactant(DestroyMolecules.WATER, 2, 1)
        .addProduct(DestroyMolecules.SODIUM_ION, 2)
        .addProduct(DestroyMolecules.HYDROXIDE, 2)
        .addProduct(DestroyMolecules.HYDROGEN)
        .build(),

    STEAM_REFORMATION = builder()
        .id("steam_reformation")
        .addReactant(DestroyMolecules.WATER)
        .addReactant(DestroyMolecules.METHANE)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/nickel"), 1f)
        .addProduct(DestroyMolecules.CARBON_MONOXIDE)
        .addProduct(DestroyMolecules.HYDROGEN, 3)
        .build(),

    STYRENE_BUTADIENE_COPOLYMERIZATION = builder()
        .id("styrene_butadiene_copolymerization")
        .addReactant(DestroyMolecules.STYRENE)
        .addReactant(DestroyMolecules.BUTADIENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(1.5f, PrecipitateReactionResult.of(DestroyItems.POLYSTYRENE_BUTADIENE::asStack))
        .preexponentialFactor(10f)
        .activationEnergy(15f)
        .build(),

    STYRENE_POLYMERIZATION = builder()
        .id("styrene_polymerization")
        .addReactant(DestroyMolecules.STYRENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.POLYSTYRENE::asStack))
        .preexponentialFactor(10f)
        .activationEnergy(10f)
        .build(),
    
    SULFUR_OXIDATION = builder()    
        .id("sulfur_oxidation")
        .addReactant(DestroyMolecules.OCTASULFUR)
        .addReactant(DestroyMolecules.OXYGEN, 8, 1)
        .addProduct(DestroyMolecules.SULFUR_DIOXIDE, 8)
        .build(), //TODO replace with half-equation

    SULFUR_TRIOXIDE_HYDRATION = builder()
        .id("sulfur_trioxide_hydration")
        .addReactant(DestroyMolecules.SULFUR_TRIOXIDE)
        .addReactant(DestroyMolecules.WATER)
        .addProduct(DestroyMolecules.SULFURIC_ACID)
        .activationEnergy(10f)
        .enthalpyChange(-200f)
        .reverseReaction(r -> r
            .preexponentialFactor(1e3f) // Hydrated form preferred
            .activationEnergy(210f)
            .enthalpyChange(200f)
        ).build(),

    TATP = builder()
        .id("tatp")
        .addReactant(DestroyMolecules.ACETONE)
        .addReactant(DestroyMolecules.HYDROGEN_PEROXIDE)
        .addCatalyst(DestroyMolecules.PROTON, 1)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.ACETONE_PEROXIDE::asStack))
        .build(),

    TETRAFLUOROETHENE_POLYMERIZATION = builder()
        .id("tetrafluoroethene_polymerization")
        .addReactant(DestroyMolecules.TETRAFLUOROETHENE)
        .addCatalyst(DestroyMolecules.AIBN, 0)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.POLYTETRAFLUOROETHENE::asStack))
        .preexponentialFactor(10f)
        .activationEnergy(10f)
        .build(),

    TOLUENE_NITRATION = builder()
        .id("toluene_nitration")
        .addReactant(DestroyMolecules.TOLUENE)
        .addReactant(DestroyMolecules.NITRONIUM, 3, 1)
        .addProduct(DestroyMolecules.TRINITROTOLUENE)
        .addProduct(DestroyMolecules.PROTON, 3)
        .build(),

    TOLUENE_TRANSALKYLATION = builder()
        .id("toluene_transalkylation")
        .addReactant(DestroyMolecules.TOLUENE, 8)
        .addSimpleItemCatalyst(DestroyItems.ZEOLITE::get, 1f)
        .addProduct(DestroyMolecules.BENZENE, 4)
        .addProduct(DestroyMolecules.METAXYLENE)
        .addProduct(DestroyMolecules.ORTHOXYLENE)
        .addProduct(DestroyMolecules.PARAXYLENE)
        .addProduct(DestroyMolecules.ETHYLBENZENE)
        .build(),

    TOUCH_POWDER_SYNTHESIS = builder()
        .id("touch_powder_synthesis")
        .addReactant(DestroyMolecules.AMMONIA)
        .addSimpleItemReactant(DestroyItems.IODINE::get, 3f)
        .withResult(3f, PrecipitateReactionResult.of(DestroyItems.TOUCH_POWDER::asStack))
        .build(),

    VINYL_ACETATE_SYNTHESIS = builder()
        .id("vinyl_acetate_synthesis")
        .addReactant(DestroyMolecules.ETHENE, 2, 1)
        .addReactant(DestroyMolecules.ACETIC_ACID, 2, 1)
        .addReactant(DestroyMolecules.OXYGEN)
        .addSimpleItemTagCatalyst(AllTags.forgeItemTag("dusts/palladium"), 1f)
        .addProduct(DestroyMolecules.VINYL_ACETATE, 2)
        .addProduct(DestroyMolecules.WATER, 2)
        .build(),

    ZINC_DISSOLUTION = builder() //TODO replace with redox
        .id("zinc_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemTagReactant(AllTags.forgeItemTag("dusts/zinc"), 0.9f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.ZINC_ION)
        .build(),

    ZINC_ORE_DISSOLUTION = builder() //TODO replace with redox
        .id("zinc_ore_dissolution")
        .addReactant(DestroyMolecules.PROTON, 2, 1)
        .addSimpleItemReactant(AllItems.CRUSHED_ZINC::get, 1.5f)
        .addProduct(DestroyMolecules.HYDROGEN)
        .addProduct(DestroyMolecules.ZINC_ION)
        .build();

    // Acids
    static {
        builder().acid(DestroyMolecules.ACETIC_ACID, DestroyMolecules.ACETATE, 4.76f);
        builder().acid(DestroyMolecules.AMMONIUM, DestroyMolecules.AMMONIA, 9.25f);
        builder().acid(DestroyMolecules.HYDROCHLORIC_ACID, DestroyMolecules.CHLORIDE, -6.3f);
        builder().acid(DestroyMolecules.HYDROFLUORIC_ACID, DestroyMolecules.FLUORIDE, 3.17f);
        builder().acid(DestroyMolecules.HYDROGEN_CYANIDE, DestroyMolecules.CYANIDE, 9.2f);
        builder().acid(DestroyMolecules.HYDROGEN_IODIDE, DestroyMolecules.IODIDE, -9.3f);
        builder().acid(DestroyMolecules.HYDROGENSULFATE, DestroyMolecules.SULFATE, 1.99f);
        builder().acid(DestroyMolecules.HYPOCHLOROUS_ACID, DestroyMolecules.HYPOCHLORITE, 7.53f);
        builder().acid(DestroyMolecules.NITRIC_ACID, DestroyMolecules.NITRATE, -1.3f);
        builder().acid(DestroyMolecules.SULFURIC_ACID, DestroyMolecules.HYDROGENSULFATE, -2.18f);
    };

    private static ReactionBuilder builder() {
        return new ReactionBuilder(Destroy.MOD_ID);
    };

    public static void register() {};
}
