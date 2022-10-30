package com.petrolpark.destroy.item;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.content.curiosities.CombustibleItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Destroy.MOD_ID);


    // PLASTICS

    public static final RegistryObject<Item> POLYETHENE_TEREPHTHALATE = ITEMS.register("polyethene_terephthalate",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYVINYL_CHLORIDE = ITEMS.register("polyvinyl_chloride",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYETHENE = ITEMS.register("polyethene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYPROPENE = ITEMS.register("polypropene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYSTYRENE = ITEMS.register("polystyrene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ABS = ITEMS.register("abs",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYTETRAFLUOROETHENE = ITEMS.register("polytetrafluoroethene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NYLON = ITEMS.register("nylon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POLYSTYRENE_BUTADIENE = ITEMS.register("polystyrene_butadiene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> RUBBER = ITEMS.register("rubber",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // INGOTS ETC

    public static final RegistryObject<Item> FLUORITE = ITEMS.register("fluorite",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PALLADIUM_INGOT = ITEMS.register("palladium_ingot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PLATINUM_INGOT = ITEMS.register("platinum_ingot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> RHODIUM_INGOT = ITEMS.register("rhodium_ingot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );  

    public static final RegistryObject<Item> PURE_GOLD_INGOT = ITEMS.register("pure_gold_ingot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZIRCONIUM_INGOT = ITEMS.register("zirconium_ingot",
    () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
);

    public static final RegistryObject<Item> SALTPETER = ITEMS.register("saltpeter",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );
    
    public static final RegistryObject<Item> ZINC_DUST = ITEMS.register("zinc_dust",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // RAW MATERIALS

    public static final RegistryObject<Item> RAW_NICKEL = ITEMS.register("raw_nickel",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CRUSHED_NICKEL_ORE = ITEMS.register("crushed_nickel_ore",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CRUSHED_PALLADIUM_ORE = ITEMS.register("crushed_palladium_ore",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CRUSHED_PLATINUM_ORE = ITEMS.register("crushed_platinum_ore",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CRUSHED_RHODIUM_ORE = ITEMS.register("crushed_rhodium_ore",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );
    
    public static final RegistryObject<Item> PURE_GOLD_DUST = ITEMS.register("pure_gold_dust",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZIRCON = ITEMS.register("zircon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );



    // PRIMARY EXPLOSIVES

    public static final RegistryObject<Item> ACETONE_PEROXIDE = ITEMS.register("acetone_peroxide",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> FULMINATED_MERCURY = ITEMS.register("fulminated_mercury",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NICKEL_HYDRAZINE_NITRATE = ITEMS.register("nickel_hydrazine_nitrate",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> TOUCH_POWDER = ITEMS.register("touch_powder",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // SECONDARY EXPLOSIVES

    public static final RegistryObject<Item> ANFO = ITEMS.register("anfo",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CORDITE = ITEMS.register("cordite",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DYNAMITE = ITEMS.register("dynamite",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NITROCELLULOSE = ITEMS.register("nitrocellulose",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SODIUM_PICRATE_TABLET = ITEMS.register("sodium_picrate_tablet",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> TNT_TABLET = ITEMS.register("tnt_tablet",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // TOOLS AND ARMOR

    public static final RegistryObject<Item> CHALK = ITEMS.register("chalk",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIAMOND_DRILL_BIT = ITEMS.register("diamond_drill_bit",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> GAS_FLITER = ITEMS.register("gas_filter",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> GAS_MASK = ITEMS.register("gas_mask",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> HAZMAT_SUIT = ITEMS.register("hazmat_suit",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> HAZMAT_LEGGINGS = ITEMS.register("hazmat_leggings",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> WELLINGTON_BOOTS = ITEMS.register("wellington_boots",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SOAP = ITEMS.register("soap",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // TOYS

    public static final RegistryObject<Item> BUCKET_AND_SPADE = ITEMS.register("bucket_and_spade",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PLAYWELL = ITEMS.register("playwell",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // SPRAY BOTTLES

    public static final RegistryObject<Item> SPRAY_BOTTLE = ITEMS.register("spray_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PERFUME_BOTTLE = ITEMS.register("perfume_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SHAMPOO_BOTTLE = ITEMS.register("shampoo_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SUNSCREEN_BOTTLE = ITEMS.register("sunscreen_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // SYRINGES

    public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ASPIRIN_SYRINGE = ITEMS.register("aspirin_syringe",
        () -> new SyringeItem(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CISPLATIN_SYRINGE = ITEMS.register("cisplatin_syringe",
        () -> new SyringeItem(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> IMPURE_METH_SYRINGE = ITEMS.register("impure_meth_syringe",
        () -> new SyringeItem(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> METH_SYRINGE = ITEMS.register("meth_syringe",
        () -> new SyringeItem(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    
    // SEISMOGRAPHS

    public static final RegistryObject<Item> SEISMOGRAPH_GOOD = ITEMS.register("seismograph_good",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SEISMOGRAPH_MIDDLE = ITEMS.register("seismograph_middle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SEISMOGRAPH_BAD = ITEMS.register("seismograph_bad",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // SILICA

    public static final RegistryObject<Item> SILICA = ITEMS.register("silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIRTY_SILICA = ITEMS.register("dirty_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ACETONE_COATED_SILICA = ITEMS.register("acetone_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> COPPER_COATED_SILICA = ITEMS.register("copper_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> IRON_COATED_SILICA = ITEMS.register("iron_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> LIMEWATER_COATED_SILICA = ITEMS.register("limewater_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NICKEL_COATED_SILICA = ITEMS.register("nickel_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PLATINUM_COATED_SILICA = ITEMS.register("platinum_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> RHODIUM_COATED_SILICA = ITEMS.register("rhodium_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SODIUM_ACETATE_COATED_SILICA = ITEMS.register("sodium_acetate_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SULFURIC_ACID_COATED_SILICA = ITEMS.register("sulfuric_acid_coated_silica",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // NON-SILICA CATALYSTS

    public static final RegistryObject<Item> CONVERSION_CATALYST = ITEMS.register("conversion_catalyst",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIRTY_CONVERSION_CATALYST = ITEMS.register("dirty_conversion_catalyst",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PALLADIUM_ON_CARBON = ITEMS.register("palladium_on_carbon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIRTY_COAL = ITEMS.register("dirty_coal",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZEOLITE = ITEMS.register("zeolite",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIRTY_ZEOLITE = ITEMS.register("dirty_zeolite",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZEIGLER_NATTA = ITEMS.register("ziegler-natta",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIRTY_ZEIGLER_NATTA = ITEMS.register("dirty_ziegler-natta",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // COMPOUNDS

    public static final RegistryObject<Item> AMMONIUM_NITRATE_PRILL = ITEMS.register("ammonium_nitrate_prill",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CALCIUM_CHLORIDE = ITEMS.register("calcium_chloride",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> WET_CALCIUM_CHLORIDE = ITEMS.register("wet_calcium_chloride",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> CHALK_DUST = ITEMS.register("chalk_dust",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> IODINE = ITEMS.register("iodine",
        () -> new IodineItem(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> METHAMPHETAMINE_CRYSTAL = ITEMS.register("methamphetamine_crystal",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> METHAMPHETAMINE_POWDER = ITEMS.register("methamphetamine_powder",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.CRUSHED_METHAMPHETAMINE))
    );

    public static final RegistryObject<Item> NAPTHALENE = ITEMS.register("napthalene",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> POTASSIUM_IODIDE = ITEMS.register("potassium_iodide",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PRUSSIAN_BLUE = ITEMS.register("prussian_blue",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ROCK_SALT = ITEMS.register("rock_salt",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZINC_OXIDE = ITEMS.register("zinc_oxide",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // FOOD AND DRINK

    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.BUTTER))
    );

    public static final RegistryObject<Item> RAW_FRIES = ITEMS.register("raw_fries",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.RAW_FRIES))
    );

    public static final RegistryObject<Item> UNSEASONED_FRIES = ITEMS.register("unseasoned_fries",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.UNSEASONED_FRIES))
    );

    public static final RegistryObject<Item> FRIES = ITEMS.register("fries",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.FRIES))
    );

    public static final RegistryObject<Item> MASHED_POTATO = ITEMS.register("mashed_potato",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY).food(DestroyFoods.MASHED_POTATO))
    );

    public static final RegistryObject<Item> UNDISTILLED_MOONSHINE_BOTTLE = ITEMS.register("undistilled_moonshine_bottle",
        () -> new AlcoholicDrinkItem(new Item.Properties()
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        , 1)
    );

    public static final RegistryObject<Item> ONCE_DISTILLED_MOONSHINE_BOTTLE = ITEMS.register("once_distilled_moonshine_bottle",
        () -> new AlcoholicDrinkItem(new Item.Properties()
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        , 1)
    );

    public static final RegistryObject<Item> TWICE_DISTILLED_MOONSHINE_BOTTLE = ITEMS.register("twice_distilled_moonshine_bottle",
        () -> new AlcoholicDrinkItem(new Item.Properties()
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        , 2)
    );

    public static final RegistryObject<Item> THRICE_DISTILLED_MOONSHINE_BOTTLE = ITEMS.register("thrice_distilled_moonshine_bottle",
        () -> new AlcoholicDrinkItem(new Item.Properties()
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        , 3)
    );


    // BLAZE BURNER TREATS

    public static final RegistryObject<Item> EMPTY_BOMB_BON = ITEMS.register("empty_bomb_bon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> BOMB_BON = ITEMS.register("bomb_bon",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NAPALM_SUNDAE = ITEMS.register("napalm_sundae",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> THERMITE_BROWNIE = ITEMS.register("thermite_brownie",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // BEETROOT

    public static final RegistryObject<Item> HEFTY_BEETROOT = ITEMS.register("hefty_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> COAL_INFUSED_BEETROOT = ITEMS.register("coal_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> COPPER_INFUSED_BEETROOT = ITEMS.register("copper_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIAMOND_INFUSED_BEETROOT = ITEMS.register("diamond_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> EMERALD_INFUSED_BEETROOT = ITEMS.register("emerald_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> FLUORITE_INFUSED_BEETROOT = ITEMS.register("fluorite_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> GOLD_INFUSED_BEETROOT = ITEMS.register("gold_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> IRON_INFUSED_BEETROOT = ITEMS.register("iron_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> LAPIS_INFUSED_BEETROOT = ITEMS.register("lapis_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NICKEL_INFUSED_BEETROOT = ITEMS.register("nickel_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> REDSTONE_INFUSED_BEETROOT = ITEMS.register("redstone_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZINC_INFUSED_BEETROOT = ITEMS.register("zinc_infused_beetroot",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // BEETROOT ASHES

    public static final RegistryObject<Item> BEETROOT_ASHES = ITEMS.register("beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> COAL_INFUSED_BEETROOT_ASHES = ITEMS.register("coal_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> COPPER_INFUSED_BEETROOT_ASHES = ITEMS.register("copper_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> DIAMOND_INFUSED_BEETROOT_ASHES = ITEMS.register("diamond_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> EMERALD_INFUSED_BEETROOT_ASHES = ITEMS.register("emerald_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> FLUORITE_INFUSED_BEETROOT_ASHES = ITEMS.register("fluorite_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> GOLD_INFUSED_BEETROOT_ASHES = ITEMS.register("gold_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> IRON_INFUSED_BEETROOT_ASHES = ITEMS.register("iron_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> LAPIS_INFUSED_BEETROOT_ASHES = ITEMS.register("lapis_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> NICKEL_INFUSED_BEETROOT_ASHES = ITEMS.register("nickel_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> REDSTONE_INFUSED_BEETROOT_ASHES = ITEMS.register("redstone_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> ZINC_INFUSED_BEETROOT_ASHES = ITEMS.register("zinc_infused_beetroot_ashes",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // UNCATEGORISED
    
    public static final RegistryObject<Item> AGAR = ITEMS.register("agar",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> HYPERACCUMULATING_FERTILIZER = ITEMS.register("hyperaccumulating_fertilizer",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> PAPER_PULP = ITEMS.register("paper_pulp",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> SAWDUST = ITEMS.register("sawdust",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> TEAR_BOTTLE = ITEMS.register("tear_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> URINE_BOTTLE = ITEMS.register("urine_bottle",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> YEAST = ITEMS.register("yeast",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );


    // SEQUENCED ASSEMBLY INTERMEDIATES

    public static final RegistryObject<Item> UNPROCESSED_CONVERSION_CATALYST = ITEMS.register("unprocessed_conversion_catalyst",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> UNPROCESSED_MASHED_POTATO = ITEMS.register("unprocessed_mashed_potato",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> UNPROCESSED_NAPALM_SUNDAE = ITEMS.register("unprocessed_napalm_sundae",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static final RegistryObject<Item> UNPROCESSED_SUPER_GLUE = ITEMS.register("unprocessed_super_glue",
        () -> new Item(new Item.Properties().tab(DestroyCreativeModeTabs.TAB_DESTROY))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
