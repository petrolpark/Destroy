package com.petrolpark.destroy.item;

import static com.petrolpark.destroy.Destroy.REGISTRATE;
import static com.simibubi.create.AllTags.forgeItemTag;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.food.DestroyFoods;
import com.petrolpark.destroy.item.renderer.GasMaskModel;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraftforge.common.Tags;

public class DestroyItems {

    public static final ItemEntry<Item> LOGO = REGISTRATE.item("logo", Item::new)
        .tab(null)
        .register();

    public static final ItemEntry<MoleculeDisplayItem> MOLECULE_DISPLAY = REGISTRATE.item("molecule_display", MoleculeDisplayItem::new)
        .tab(null)
        .register();

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

    CARD_STOCK = REGISTRATE.item("card_stock", Item::new)
        .register(),

    // INGOTS ETC

    FLUORITE = REGISTRATE.item("fluorite", Item::new)
        .tag(DestroyItemTags.SALT.tag, forgeItemTag("raw_materials/fluorite"), ItemTags.BEACON_PAYMENT_ITEMS)
        .register(),
    NICKEL_INGOT = REGISTRATE.item("nickel_ingot", Item::new)
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/nickel"), Tags.Items.INGOTS)
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
        .tag(DestroyItemTags.DESTROY_INGOTS.tag, forgeItemTag("ingots/zirconium"), Tags.Items.INGOTS, DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    SULFUR = REGISTRATE.item("sulfur", Item::new)
        .tag(forgeItemTag("raw_materials/sulfur"))
        .register(),
    ZINC_SHEET = REGISTRATE.item("zinc_sheet", Item::new)
        .tag(forgeItemTag("plates/zinc"))
        .register(),

    // RAW MATERIALS

    RAW_NICKEL = REGISTRATE.item("raw_nickel", Item::new)
        .tag(forgeItemTag("raw_materials/nickel"), Tags.Items.RAW_MATERIALS)
        .register(),
    CRUSHED_RAW_PALLADIUM = REGISTRATE.item("crushed_raw_palladium", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
        .register(),
    CRUSHED_RAW_PLATINUM = REGISTRATE.item("crushed_raw_platinum", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
        .model(AssetLookup.existingItemModel())
        .register(),
    CRUSHED_RAW_RHODIUM = REGISTRATE.item("crushed_raw_rhodium", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
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
        .register();

    public static final ItemEntry<ContactExplosiveItem>

    TOUCH_POWDER = REGISTRATE.item("touch_powder", ContactExplosiveItem::new)
        .tag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register();

    // SECONDARY EXPLOSIVES

    public static final ItemEntry<Item>

    ANFO = REGISTRATE.item("anfo", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag, Tags.Items.DUSTS)
        .register(),
    CORDITE = REGISTRATE.item("cordite_rods", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    DYNAMITE = REGISTRATE.item("dynamite", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    NITROCELLULOSE = REGISTRATE.item("nitrocellulose", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    PICRIC_ACID_TABLET = REGISTRATE.item("picric_acid_tablet", Item::new)
        .tag(DestroyItemTags.SALT.tag, DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),
    TNT_TABLET = REGISTRATE.item("tnt_tablet", Item::new)
        .tag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag)
        .register(),

    // COMPOUNDS

    AMMONIUM_NITRATE_PRILL = REGISTRATE.item("ammonium_nitrate_prill", Item::new)
        .tag(DestroyItemTags.PRILL.tag, DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    CHALK_DUST = REGISTRATE.item("chalk_dust", Item::new)
        .tag(DestroyItemTags.SALT.tag, Tags.Items.DUSTS, DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    BABY_BLUE_CRYSTAL = REGISTRATE.item("baby_blue_crystal", Item::new)
        .register(),
    BABY_BLUE_POWDER = REGISTRATE.item("baby_blue_powder", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BABY_BLUE_POWDER)
        ).tag(Tags.Items.DUSTS)
        .register();
    
    public static final ItemEntry<IodineItem> IODINE = REGISTRATE.item("iodine", IodineItem::new)
        .tag(DestroyItemTags.PRILL.tag)
        .register();

    // TOOLS AND ARMOR

    public static final ItemEntry<SwissArmyKnifeItem>

    SWISS_ARMY_KNIFE = REGISTRATE.item("swiss_army_knife", (p) -> new SwissArmyKnifeItem(5f, 5f, Tiers.IRON, p))    
        .properties(p -> p
            .durability(800)
        ).register();

    public static final ItemEntry<Item>

    GAS_FLITER = REGISTRATE.item("gas_filter", Item::new)
        .register();

    public static final ItemEntry<GasMaskItem>

    GAS_MASK = REGISTRATE.item("gas_mask", GasMaskItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).onRegister(CreateRegistrate.itemModel(() -> GasMaskModel::new))
        .tag(DestroyItemTags.CHEMICAL_PROTECTION_HEAD.tag)
        .register();

    public static final ItemEntry<? extends HazmatSuitArmorItem>

    HAZMAT_SUIT = REGISTRATE.item("hazmat_suit", p -> new HazmatSuitArmorItem(Type.CHESTPLATE, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_TORSO.tag)
        .register(),
    HAZMAT_LEGGINGS = REGISTRATE.item("hazmat_leggings", p -> new HazmatSuitArmorItem(Type.LEGGINGS, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_LEGS.tag)
        .register(),
    WELLINGTON_BOOTS = REGISTRATE.item("wellington_boots", p -> new HazmatSuitArmorItem(Type.BOOTS, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyItemTags.CHEMICAL_PROTECTION_FEET.tag)
        .register();

    // public static final ItemEntry<ZirconiumPantsItem>

    // ZIRCONIUM_PANTS = REGISTRATE.item("zirconium_pants", ZirconiumPantsItem::new)
    //     .properties(p -> p
    //         .stacksTo(1)
    //     ).tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
    //     .register();

    public static final ItemEntry<SeismometerItem>

    SEISMOMETER = REGISTRATE.item("seismometer", SeismometerItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).register();

    public static final ItemEntry<TestTubeItem>

    TEST_TUBE = REGISTRATE.item("test_tube", TestTubeItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).color(() -> () -> TestTubeItem::getColor)
        .register();

    // TOYS

    // static {
    //     REGISTRATE.startSection(AllSections.MATERIALS);
    // };

    public static final ItemEntry<BucketAndSpadeItem> BUCKET_AND_SPADE = REGISTRATE.item("bucket_and_spade", BucketAndSpadeItem::new)
        .properties(p -> p
            .durability(4)
        ).register();

    public static final ItemEntry<Item>

    PLAYWELL = REGISTRATE.item("playwell", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),

    // SPRAY BOTTLES

    SPRAY_BOTTLE = REGISTRATE.item("spray_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    PERFUME_BOTTLE = REGISTRATE.item("perfume_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),
    SUNSCREEN_BOTTLE = REGISTRATE.item("sunscreen_bottle", Item::new)
        .tag(DestroyItemTags.SPRAY_BOTTLE.tag)
        .register(),

    // SILICA

    SILICA = REGISTRATE.item("silica", Item::new)
        .register(),
    
    // NON-SILICA CATALYSTS

    CONVERSION_CATALYST = REGISTRATE.item("conversion_catalyst", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    PALLADIUM_ON_CARBON = REGISTRATE.item("palladium_on_carbon", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    ZEOLITE = REGISTRATE.item("zeolite", Item::new)
        .register(),
    ZEIGLER_NATTA = REGISTRATE.item("ziegler-natta", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    MAGIC_OXIDANT = REGISTRATE.item("magic_oxidant", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    MAGIC_REDUCTANT = REGISTRATE.item("magic_reductant", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),

    // FOOD AND DRINK

    BUTTER = REGISTRATE.item("butter", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BUTTER)
        ).register(),
    RAW_FRIES = REGISTRATE.item("raw_fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.RAW_FRIES)
        ).tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    UNSEASONED_FRIES = REGISTRATE.item("unseasoned_fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.UNSEASONED_FRIES)
        ).tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    FRIES = REGISTRATE.item("fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.FRIES)
        ).tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    MASHED_POTATO = REGISTRATE.item("mashed_potato", Item::new)
        .properties(p -> p
            .food(DestroyFoods.MASHED_POTATO)
        ).register(),
    WHITE_WHEAT = REGISTRATE.item("white_wheat", Item::new)
        .tag(Tags.Items.CROPS)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    BIFURICATED_CARROT = REGISTRATE.item("bifuricated_carrot", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BIFURICATED_CARROT)
        ).tag(Tags.Items.CROPS, DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    POTATE_O = REGISTRATE.item("potate_o", Item::new)
        .properties(p -> p
            .food(DestroyFoods.POTATE_O)
        ).tag(Tags.Items.CROPS, DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register();

    public static final ItemEntry<BowlFoodItem>
    
    BANGERS_AND_MASH = REGISTRATE.item("bangers_and_mash", BowlFoodItem::new)
        .properties(p -> p
            .food(DestroyFoods.BANGERS_AND_MASH)
        ).register();

    public static final ItemEntry<AlcoholicDrinkItem>

    UNDISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("undistilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 1))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
        .register(),
    ONCE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("once_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 1))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
        .register(),
    TWICE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("twice_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 2))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
        .register(),
    THRICE_DISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("thrice_distilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 3))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
        .register();
    
    public static final ItemEntry<ChorusWineItem>
    
    CHORUS_WINE_BOTTLE = REGISTRATE.item("chorus_wine_bottle", p -> new ChorusWineItem(p, 1))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
        .register();

    // SEQUENCED ASSEMBLY INTERMEDIATES

    public static final ItemEntry<SequencedAssemblyItem>

    UNFINISHED_BLACKLIGHT = REGISTRATE.item("unfinished_blacklight", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_CARD_STOCK = REGISTRATE.item("unfinished_card_stock", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_VOLTAIC_PILE = REGISTRATE.item("unfinished_voltaic_pile", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNPROCESSED_CONVERSION_CATALYST = REGISTRATE.item("unprocessed_conversion_catalyst", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNPROCESSED_MASHED_POTATO = REGISTRATE.item("unprocessed_mashed_potato", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNPROCESSED_NAPALM_SUNDAE = REGISTRATE.item("unprocessed_napalm_sundae", SequencedAssemblyItem::new)
        .tab(null)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register();

    public static final ItemEntry<CombustibleItem>

    // BLAZE BURNER TREATS

    EMPTY_BOMB_BON = REGISTRATE.item("empty_bomb_bon", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(800))
        .register(),
    BOMB_BON = REGISTRATE.item("bomb_bon", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(6400))
        .register(),
    NAPALM_SUNDAE = REGISTRATE.item("napalm_sundae", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(6400))
        .register(),
    THERMITE_BROWNIE = REGISTRATE.item("thermite_brownie", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag)
        .onRegister(i -> i.setBurnTime(6400))
        .register();

    // BEETROOT

    public static final ItemEntry<ItemNameBlockItem>

    MAGIC_BEETROOT_SEEDS = REGISTRATE.item("magic_beetroot_seeds", p -> new ItemNameBlockItem(DestroyBlocks.MAGIC_BEETROOT_SHOOTS.get(), p))
        .tag(Tags.Items.SEEDS)
        .tag(Tags.Items.SEEDS_BEETROOT)
        .register();

    public static final ItemEntry<Item>

    HEFTY_BEETROOT = REGISTRATE.item("hefty_beetroot", Item::new)
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register();

    public static final ItemEntry<WithSecondaryItem> 

    COAL_INFUSED_BEETROOT = REGISTRATE.item("coal_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.COAL)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    COPPER_INFUSED_BEETROOT = REGISTRATE.item("copper_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_COPPER)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT = REGISTRATE.item("diamond_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.DIAMOND)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT = REGISTRATE.item("emerald_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.EMERALD)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT = REGISTRATE.item("fluorite_infused_beetroot", p -> new WithSecondaryItem(p, FLUORITE::asStack))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    GOLD_INFUSED_BEETROOT = REGISTRATE.item("gold_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_GOLD)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    IRON_INFUSED_BEETROOT = REGISTRATE.item("iron_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_IRON)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT = REGISTRATE.item("lapis_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.LAPIS_LAZULI)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT = REGISTRATE.item("nickel_infused_beetroot", p -> new WithSecondaryItem(p, RAW_NICKEL::asStack))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT = REGISTRATE.item("redstone_infused_beetroot", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.REDSTONE)))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register(),
    ZINC_INFUSED_BEETROOT = REGISTRATE.item("zinc_infused_beetroot", p -> new WithSecondaryItem(p, AllItems.RAW_ZINC::asStack))
        .tag(DestroyItemTags.HEFTY_BEETROOT.tag)
        .register();
    
    // BEETROOT ASHES

    public static final ItemEntry<Item>

    BEETROOT_ASHES = REGISTRATE.item("beetroot_ashes", Item::new)
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register();

    public static final ItemEntry<WithSecondaryItem> 

    COAL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("coal_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.COAL)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    COPPER_INFUSED_BEETROOT_ASHES = REGISTRATE.item("copper_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_COPPER)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT_ASHES = REGISTRATE.item("diamond_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.DIAMOND)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("emerald_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.EMERALD)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("fluorite_infused_beetroot_ashes", p -> new WithSecondaryItem(p, FLUORITE::asStack))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    GOLD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("gold_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_GOLD)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    IRON_INFUSED_BEETROOT_ASHES = REGISTRATE.item("iron_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.RAW_IRON)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT_ASHES = REGISTRATE.item("lapis_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.LAPIS_LAZULI)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("nickel_infused_beetroot_ashes", p -> new WithSecondaryItem(p, RAW_NICKEL::asStack))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("redstone_infused_beetroot_ashes", p -> new WithSecondaryItem(p, () -> new ItemStack(Items.REDSTONE)))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register(),
    ZINC_INFUSED_BEETROOT_ASHES = REGISTRATE.item("zinc_infused_beetroot_ashes", p -> new WithSecondaryItem(p, AllItems.RAW_ZINC::asStack))
        .tag(DestroyItemTags.BEETROOT_ASHES.tag)
        .register();

    // static {
    //     REGISTRATE.startSection(AllSections.CURIOSITIES);
    // };

    // SYRINGES

    public static final ItemEntry<Item> SYRINGE = REGISTRATE.item("syringe", Item::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .register();

    public static final ItemEntry<? extends SyringeItem>

    ASPIRIN_SYRINGE = REGISTRATE.item("aspirin_syringe", AspirinSyringeItem::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 16716136 : -1)
        .register(),

    BABY_BLUE_SYRINGE = REGISTRATE.item("baby_blue_syringe", p -> new BabyBlueSyringeItem(p, 1200, 1))
        .tag(DestroyItemTags.SYRINGE.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 8825802 : -1)
        .register(),

    CISPLATIN_SYRINGE = REGISTRATE.item("cisplatin_syringe", CisplatinSyringeItem::new)
        .tag(DestroyItemTags.SYRINGE.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 11459547 : -1)
        .register();

    // UNCATEGORISED

    // static {
    //     REGISTRATE.startSection(AllSections.CURIOSITIES);
    // };

    public static final ItemEntry<Item>

    AGAR = REGISTRATE.item("agar", Item::new)
        .register(),
    DISCHARGED_VOLTAIC_PILE = REGISTRATE.item("discharged_voltaic_pile", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    PAPER_PULP = REGISTRATE.item("paper_pulp", Item::new)
        .register(),
    TEAR_BOTTLE = REGISTRATE.item("tear_bottle", Item::new)
        .register(),
    URINE_BOTTLE = REGISTRATE.item("urine_bottle", Item::new)
        .register(),
    VOLTAIC_PILE = REGISTRATE.item("voltaic_pile", Item::new)
        .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
        .register(),
    YEAST = REGISTRATE.item("yeast", Item::new)
        .register(),
    // CHALK = REGISTRATE.item("chalk", Item::new)
    //     .tag(DestroyItemTags.LIABLE_TO_CHANGE.tag)
    //     .register(),
    NANODIAMONDS = REGISTRATE.item("nanodiamonds", Item::new)
        .register();

    public static final ItemEntry<HyperaccumulatingFertilizerItem> HYPERACCUMULATING_FERTILIZER = REGISTRATE.item("hyperaccumulating_fertilizer", HyperaccumulatingFertilizerItem::new)
        .tag(Tags.Items.DUSTS)
        .register();

    public static final ItemEntry<RecordItem> MUSIC_DISC_SPECTRUM = REGISTRATE.item("music_disc_spectrum", p -> new RecordItem(9, () -> DestroySoundEvents.MUSIC_DISC_SPECTRUM.getMainEvent(), p, 3720))
        .properties(p -> p
            .stacksTo(1)
        ).tag(ItemTags.MUSIC_DISCS)
        .register();

    public static void register() {};
}
