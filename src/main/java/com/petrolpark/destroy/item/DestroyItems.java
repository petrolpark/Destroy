package com.petrolpark.destroy.item;

import static com.simibubi.create.AllTags.forgeItemTag;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.simibubi.create.AllTags.AllItemTags;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.itemAssembly.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.common.Tags;

public class DestroyItems {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();

    static {
        REGISTRATE.creativeModeTab(() -> DestroyCreativeModeTabs.TAB_DESTROY);
    };

    static {
        REGISTRATE.startSection(AllSections.MATERIALS);
    };

    public static final ItemEntry<Item>

    // PLASTICS
    
    POLYETHENE_TEREPHTHALATE = REGISTRATE.item("polyethene_terephthalate", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYVINYL_CHLORIDE = REGISTRATE.item("polyvinyl_chloride", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYETHENE = REGISTRATE.item("polyethene", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYPROPENE = REGISTRATE.item("polypropene", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYSTYRENE = REGISTRATE.item("polystyrene", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    ABS = REGISTRATE.item("abs", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYTETRAFLUOROETHENE = REGISTRATE.item("polytetrafluoroethene", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    NYLON = REGISTRATE.item("nylon", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    POLYSTYRENE_BUTADIENE = REGISTRATE.item("polystyrene_butadiene", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),
    RUBBER = REGISTRATE.item("rubber", Item::new)
        .tag(DestroyItemTags.PLASTIC.tag)
        .register(),

    // INGOTS ETC

    FLUORITE = REGISTRATE.item("fluorite", Item::new)
        .tag(DestroyItemTags.SALT.tag, forgeItemTag("raw_material/fluorite"), ItemTags.BEACON_PAYMENT_ITEMS)
        .register(),
    NICKEL_INGOT = REGISTRATE.item("nickel_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/palladium"), Tags.Items.INGOTS)
        .register(),
    PALLADIUM_INGOT = REGISTRATE.item("palladium_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/palladium"), Tags.Items.INGOTS)
        .register(),
    PLATINUM_INGOT = REGISTRATE.item("platinum_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/platinum"), Tags.Items.INGOTS)
        .register(),
    RHODIUM_INGOT = REGISTRATE.item("rhodium_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/rhodium"), Tags.Items.INGOTS)
        .register(),
    PURE_GOLD_INGOT = REGISTRATE.item("pure_gold_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, Tags.Items.INGOTS, ItemTags.PIGLIN_LOVED)
        .register(),
    ZIRCONIUM_INGOT = REGISTRATE.item("zirconium_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/zirconium"), Tags.Items.INGOTS)
        .register(),
    SULFUR = REGISTRATE.item("sulfur", Item::new)
        .tag(forgeItemTag("raw_materials/sulfur"))
        .register(),
    ZINC_DUST = REGISTRATE.item("zinc_dust", Item::new)
        .tag(forgeItemTag("dusts/zinc"), Tags.Items.DUSTS)
        .register(),

    // RAW MATERIALS

    RAW_NICKEL = REGISTRATE.item("raw_nickel", Item::new)
        .tag(forgeItemTag("raw_materials/nickel"), Tags.Items.RAW_MATERIALS)
        .register(),
    CRUSHED_NICKEL_ORE = REGISTRATE.item("crushed_nickel_ore", Item::new)
        .tag(AllItemTags.CRUSHED_ORES.tag)
        .model(AssetLookup.existingItemModel())
        .register(),
    CRUSHED_PALLADIUM_ORE = REGISTRATE.item("crushed_palladium_ore", Item::new)
        .tag(AllItemTags.CRUSHED_ORES.tag)
        .register(),
    CRUSHED_PLATINUM_ORE = REGISTRATE.item("crushed_platinum_ore", Item::new)
        .tag(AllItemTags.CRUSHED_ORES.tag)
        .model(AssetLookup.existingItemModel())
        .register(),
    CRUSHED_RHODIUM_ORE = REGISTRATE.item("crushed_rhodium_ore", Item::new)
        .tag(AllItemTags.CRUSHED_ORES.tag)
        .register(),
    PURE_GOLD_DUST = REGISTRATE.item("pure_gold_dust", Item::new)
        .tag(Tags.Items.DUSTS, ItemTags.PIGLIN_LOVED)
        .register(),
    ZIRCON = REGISTRATE.item("zircon", Item::new)
        .tag(forgeItemTag("raw_materials/zircon"))
        .register(),

    // PRIMARY EXPLOSIVES

    ACETONE_PEROXIDE = REGISTRATE.item("acetone_peroxide", Item::new)
        .tag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register(),
    FULMINATED_MERCURY = REGISTRATE.item("fulminated_mercury", Item::new)
        .tag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag, DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(),
    NICKEL_HYDRAZINE_NITRATE = REGISTRATE.item("nickel_hydrazine_nitrate", Item::new)
        .tag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register(),
    TOUCH_POWDER = REGISTRATE.item("touch_powder", Item::new)
        .tag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register(),

    // SECONDARY EXPLOSIVES

    ANFO = REGISTRATE.item("anfo", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register(),
    CORDITE = REGISTRATE.item("cordite", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    DYNAMITE = REGISTRATE.item("dynamite", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    NITROCELLULOSE = REGISTRATE.item("nitrocellulose", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    SODIUM_PICRATE_TABLET = REGISTRATE.item("sodium_picrate_tablet", Item::new)
        .tag(DestroyItemTags.SALT.tag, DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    TNT_TABLET = REGISTRATE.item("tnt_tablet", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),

    // COMPOUNDS

    AMMONIUM_NITRATE_PRILL = REGISTRATE.item("ammonium_nitrate_prill", Item::new)
        .tag(DestroyItemTags.PRILL.tag)
        .register(),
    CALCIUM_CHLORIDE = REGISTRATE.item("calcium_chloride", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(),
    WET_CALCIUM_CHLORIDE = REGISTRATE.item("wet_calcium_chloride", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(),
    CHALK_DUST = REGISTRATE.item("chalk_dust", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(),
    BABY_BLUE_CRYSTAL = REGISTRATE.item("baby_blue_crystal", Item::new)
        .register(),
    BABY_BLUE_POWDER = REGISTRATE.item("baby_blue_powder", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BABY_BLUE_POWDER)
        ).tag(Tags.Items.DUSTS)
        .register(),
    SALTPETER = REGISTRATE.item("saltpeter", Item::new)
        .tag(DestroyItemTags.SALT.tag, forgeItemTag("raw_materials/saltpeter"), Tags.Items.DUSTS)
        .register(),
    POTASSIUM_IODIDE = REGISTRATE.item("potassium_iodide", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(), 
    PRUSSIAN_BLUE = REGISTRATE.item("prussian_blue", Item::new)
        .tag(Tags.Items.DYES_BLUE, Tags.Items.DUSTS)
        .register(), 
    ROCK_SALT = REGISTRATE.item("rock_salt", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS)
        .register(), 
    ZINC_OXIDE = REGISTRATE.item("zinc_oxide", Item::new)
        .tag(DestroyItemTags.SALT.tag, DestroyItemTags.VULCANIZER.tag, Tags.Items.DUSTS)
        .register();
    
    public static final ItemEntry<IodineItem> IODINE = REGISTRATE.item("iodine", IodineItem::new)
        .tag(DestroyItemTags.PRILL.tag)
        .register();

    // TOOLS AND ARMOR

    public static final ItemEntry<Item>

    DIAMOND_DRILL_BIT = REGISTRATE.item("diamond_drill_bit", Item::new)
        .register(),
    GAS_FLITER = REGISTRATE.item("gas_filter", Item::new)
        .register(),
    GAS_MASK = REGISTRATE.item("gas_mask", Item::new)
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_HEAD.tag)
        .register(),
    HAZMAT_SUIT = REGISTRATE.item("hazmat_suit", Item::new)
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_TORSO.tag)
        .register(),
    HAZMAT_LEGGINGS = REGISTRATE.item("hazmat_leggings", Item::new)
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_LEGS.tag)
        .register(),
    WELLINGTON_BOOTS = REGISTRATE.item("wellington_boots", Item::new)
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_FEET.tag)
        .register(),
    SOAP = REGISTRATE.item("soap", Item::new)
        .register(),

    // TOYS

    BUCKET_AND_SPADE = REGISTRATE.item("bucket_and_spade", Item::new)
        .register(),
    PLAYWELL = REGISTRATE.item("playwell", Item::new)
        .register(),

    // SPRAY BOTTLES

    SPRAY_BOTTLE = REGISTRATE.item("spray_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    PERFUME_BOTTLE = REGISTRATE.item("perfume_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    SHAMPOO_BOTTLE = REGISTRATE.item("shampoo_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    SUNSCREEN_BOTTLE = REGISTRATE.item("sunscreen_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    
    // SEISMOGRAPHS

    SEISMOGRAPH_GOOD = REGISTRATE.item("seismograph_good", Item::new)
        .tag(DestroyItemTags.SEISMOGRAPH.tag)
        .register(),
    SEISMOGRAPH_MIDDLE = REGISTRATE.item("seismograph_middle", Item::new)
        .tag(DestroyItemTags.SEISMOGRAPH.tag)
        .register(),
    SEISMOGRAPH_BAD = REGISTRATE.item("seismograph_bad", Item::new)
        .tag(DestroyItemTags.SEISMOGRAPH.tag)
        .register(),

    // SILICA

    SILICA = REGISTRATE.item("silica", Item::new)
        .register(),
    DIRTY_SILICA = REGISTRATE.item("dirty_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    ACETONE_COATED_SILICA = REGISTRATE.item("acetone_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    COPPER_COATED_SILICA = REGISTRATE.item("copper_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    IRON_COATED_SILICA = REGISTRATE.item("iron_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    LIMEWATER_COATED_SILICA = REGISTRATE.item("limewater_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    NICKEL_COATED_SILICA = REGISTRATE.item("nickel_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    PLATINUM_COATED_SILICA = REGISTRATE.item("platinum_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    RHODIUM_COATED_SILICA = REGISTRATE.item("rhodium_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    SODIUM_ACETATE_COATED_SILICA = REGISTRATE.item("sodium_acetate_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    SULFURIC_ACID_COATED_SILICA = REGISTRATE.item("sulfuric_acid_coated_silica", Item::new)
        .tag(DestroyItemTags.DIRTY_SILICA.tag, Tags.Items.DUSTS)
        .register(),
    
    // NON-SILICA CATALYSTS

    CONVERSION_CATALYST = REGISTRATE.item("conversion_catalyst", Item::new)
        .register(),
    DIRTY_CONVERSION_CATALYST = REGISTRATE.item("dirty_conversion_catalyst", Item::new)
        .register(),
    PALLADIUM_ON_CARBON = REGISTRATE.item("palladium_on_carbon", Item::new)
        .register(),
    DIRTY_COAL = REGISTRATE.item("dirty_coal", Item::new)
        .register(),
    ZEOLITE = REGISTRATE.item("zeolite", Item::new)
        .register(),
    DIRTY_ZEOLITE = REGISTRATE.item("dirty_zeolite", Item::new)
        .register(),
    ZEIGLER_NATTA = REGISTRATE.item("ziegler-natta", Item::new)
        .register(),
    DIRTY_ZEIGLER_NATTA = REGISTRATE.item("dirty_ziegler-natta", Item::new)
        .register(),

    // FOOD AND DRINK

    BUTTER = REGISTRATE.item("butter", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BUTTER)
        ).register(),
    RAW_FRIES = REGISTRATE.item("raw_fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.RAW_FRIES)
        ).register(),
    UNSEASONED_FRIES = REGISTRATE.item("unseasoned_fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.UNSEASONED_FRIES)
        ).register(),
    FRIES = REGISTRATE.item("fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.FRIES)
        ).register(),
    MASHED_POTATO = REGISTRATE.item("mashed_potato", Item::new)
        .properties(p -> p
            .food(DestroyFoods.MASHED_POTATO)
        ).register(),
    WHITE_WHEAT = REGISTRATE.item("white_wheat", Item::new)
        .tag(Tags.Items.CROPS)
        .register(),
    BIFURICATED_CARROT = REGISTRATE.item("bifuricated_carrot", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BIFURICATED_CARROT)
        ).tag(Tags.Items.CROPS)
        .register(),
    POTATE_O = REGISTRATE.item("potate_o", Item::new)
        .properties(p -> p
            .food(DestroyFoods.POTATE_O)
        ).tag(Tags.Items.CROPS)
        .register();

    public static final ItemEntry<AlcoholicDrinkItem>

    UNDISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("undistilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 1))
        .properties(p -> p
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).register(),
    ONCE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("once_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 1))
        .properties(p -> p
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).register(),
    TWICE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("twice_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 2))
        .properties(p -> p
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).register(),
    THRICE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("thrice_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 3))
        .properties(p -> p
            .tab(DestroyCreativeModeTabs.TAB_DESTROY)
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).register();

    // SEQUENCED ASSEMBLY INTERMEDIATES

    public static final ItemEntry<SequencedAssemblyItem>

    UNPROCESSED_CONVERSION_CATALYST = REGISTRATE.item("unprocessed_conversion_catalyst", SequencedAssemblyItem::new)
        .register(),
    UNPROCESSED_MASHED_POTATO = REGISTRATE.item("unprocessed_mashed_potato", SequencedAssemblyItem::new)
        .register(),
    UNPROCESSED_napalm_sundae = REGISTRATE.item("unprocessed_napalm_sundae", SequencedAssemblyItem::new)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    UNPROCESSED_SUPER_GLUE = REGISTRATE.item("unprocessed_super_glue", SequencedAssemblyItem::new)
        .register();

    // UNCATEGORISED

    public static final ItemEntry<Item>

    AGAR = REGISTRATE.item("agar", Item::new)
        .register(),
    PAPER_PULP = REGISTRATE.item("paper_pulp", Item::new)
        .register(),
    SAWDUST = REGISTRATE.item("sawdust", Item::new)
        .register(),
    TEAR_BOTTLE = REGISTRATE.item("tear_bottle", Item::new)
        .register(),
    URINE_BOTTLE = REGISTRATE.item("urine_bottle", Item::new)
        .register(),
    YEAST = REGISTRATE.item("yeast", Item::new)
        .register(),

    // BLAZE BURNER TREATS

    EMPTY_BOMB_BON = REGISTRATE.item("empty_bomb_bon", Item::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    BOMB_BON = REGISTRATE.item("bomb_bon", Item::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    NAPALM_SUNDAE = REGISTRATE.item("napalm_sundae", Item::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    THERMITE_BROWNIE = REGISTRATE.item("thermite_brownie", Item::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag)
        .register(),

    // BEETROOT

    HEFTY_BEETROOT = REGISTRATE.item("hefty_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    COAL_INFUSED_BEETROOT = REGISTRATE.item("coal_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    COPPER_INFUSED_BEETROOT = REGISTRATE.item("copper_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT = REGISTRATE.item("diamond_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT = REGISTRATE.item("emerald_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT = REGISTRATE.item("fluorite_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    GOLD_INFUSED_BEETROOT = REGISTRATE.item("gold_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    IRON_INFUSED_BEETROOT = REGISTRATE.item("iron_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT = REGISTRATE.item("lapis_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT = REGISTRATE.item("nickel_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT = REGISTRATE.item("redstone_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    ZINC_INFUSED_BEETROOT = REGISTRATE.item("zinc_infused_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    
    // BEETROOT ASHES

    BEETROOT_ASHES = REGISTRATE.item("beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    COAL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("coal_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    COPPER_INFUSED_BEETROOT_ASHES = REGISTRATE.item("copper_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT_ASHES = REGISTRATE.item("diamond_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("emerald_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("fluorite_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    GOLD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("gold_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    IRON_INFUSED_BEETROOT_ASHES = REGISTRATE.item("iron_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT_ASHES = REGISTRATE.item("lapis_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("nickel_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("redstone_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    ZINC_INFUSED_BEETROOT_ASHES = REGISTRATE.item("zinc_infused_beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register();

    static {
        REGISTRATE.startSection(AllSections.CURIOSITIES);
    };

    // SYRINGES

    public static final ItemEntry<Item> SYRINGE = REGISTRATE.item("syringe", Item::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .register();

    public static final ItemEntry<AspirinSyringeItem> ASPIRIN_SYRINGE = REGISTRATE.item("aspirin_syringe", AspirinSyringeItem::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .register();

    public static final ItemEntry<SyringeItem> CISPLATIN_SYRINGE = REGISTRATE.item("cisplatin_syringe", SyringeItem::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .register();

    public static final ItemEntry<BabyBlueSyringeItem>
    
    BABY_BLUE_SYRINGE = REGISTRATE.item("baby_blue_syringe", p -> new BabyBlueSyringeItem(p, 1200, 1))
        .tag(DestroyItemTags.SYRINGE.tag)
        .register();

    // UNCATEGORISED

    public static final ItemEntry<HyperaccumulatingFertilizerItem> HYPERACCUMULATING_FERTILIZER = REGISTRATE.item("hyperaccumulating_fertilizer", HyperaccumulatingFertilizerItem::new)
        .tag(Tags.Items.DUSTS)
        .register();

    public static final ItemEntry<Item> CHALK = REGISTRATE.item("chalk", Item::new)
        .register();

    public static final ItemEntry<RecordItem> MUSIC_DISC_SPECTRUM = REGISTRATE.item("music_disc_spectrum", p -> new RecordItem(9, DestroySoundEvents.MUSIC_DISC_SPECTRUM, p, 3720))
        .properties(p -> p
            .stacksTo(1)
        ).tag(ItemTags.MUSIC_DISCS)
        .register();

    public static void register() {};
}
