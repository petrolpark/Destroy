package com.petrolpark.destroy;

import static com.petrolpark.destroy.Destroy.REGISTRATE;
import static com.simibubi.create.AllTags.forgeItemTag;

import com.petrolpark.compat.CompatMods;
import com.petrolpark.compat.curios.CuriosSetup;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.confetti.ConfettiItem;
import com.petrolpark.destroy.content.confetti.ConfettoParticleData;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem;
import com.petrolpark.destroy.content.oil.seismology.SeismometerItem;
import com.petrolpark.destroy.content.processing.discstamping.DiscStamperItem;
import com.petrolpark.destroy.content.processing.phytomining.HyperaccumulatingFertilizerItem;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitBoardItem;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitMaskItem;
import com.petrolpark.destroy.content.product.AspirinSyringeItem;
import com.petrolpark.destroy.content.product.CarboxylatingItem;
import com.petrolpark.destroy.content.product.ChewingGumItem;
import com.petrolpark.destroy.content.product.CisplatinSyringeItem;
import com.petrolpark.destroy.content.product.CreatineItem;
import com.petrolpark.destroy.content.product.IodineItem;
import com.petrolpark.destroy.content.product.MilkCartonItem;
import com.petrolpark.destroy.content.product.OxidizingItem;
import com.petrolpark.destroy.content.product.WaterSensitiveSpontaneouslyCombustingItem;
import com.petrolpark.destroy.content.product.alcohol.AlcoholicDrinkItem;
import com.petrolpark.destroy.content.product.alcohol.ChorusWineItem;
import com.petrolpark.destroy.content.product.babyblue.BabyBlueSyringeItem;
import com.petrolpark.destroy.content.sandcastle.BucketAndSpadeItem;
import com.petrolpark.destroy.content.tool.SprayBottleItem;
import com.petrolpark.destroy.content.tool.swissarmyknife.SwissArmyKnifeItem;
import com.petrolpark.destroy.content.tool.syringe.SyringeItem;
import com.petrolpark.destroy.core.chemistry.MoleculeDisplayItem;
import com.petrolpark.destroy.core.chemistry.hazard.protection.ChemistryProtectionHeadwearItem;
import com.petrolpark.destroy.core.chemistry.hazard.protection.GasMaskModel;
import com.petrolpark.destroy.core.chemistry.hazard.protection.GoldLaboratoryGogglesModel;
import com.petrolpark.destroy.core.chemistry.hazard.protection.HazmatSuitArmorItem;
import com.petrolpark.destroy.core.chemistry.hazard.protection.LaboratoryGogglesModel;
import com.petrolpark.destroy.core.chemistry.hazard.protection.PaperMaskModel;
import com.petrolpark.destroy.core.chemistry.storage.testtube.TestTubeItem;
import com.petrolpark.destroy.core.explosion.ContactExplosiveItem;
import com.petrolpark.destroy.core.item.BlankRecordItem;
import com.petrolpark.destroy.core.item.DrinkItem;
import com.petrolpark.destroy.core.item.StackableBowlFoodItem;
import com.petrolpark.destroy.core.item.WithSecondaryItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.SimpleFoiledItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class DestroyItems {

    // DUMMY ITEMS

    public static final ItemEntry<Item>
    
    LOGO = REGISTRATE.item("logo", Item::new)
        .removeTab(CreativeModeTabs.SEARCH)
        .register(),

    POLLUTION_SYMBOL = REGISTRATE.item("pollution_symbol", Item::new)
        .removeTab(CreativeModeTabs.SEARCH)
        .register();

    public static final ItemEntry<MoleculeDisplayItem> MOLECULE_DISPLAY = REGISTRATE.item("molecule_display", MoleculeDisplayItem::new)
        .removeTab(CreativeModeTabs.SEARCH)
        .register();

    public static final ItemEntry<Item>

    // PLASTICS
    
    POLYETHENE_TEREPHTHALATE = REGISTRATE.item("polyethene_terephthalate", Item::new)
        .register(),
    POLYVINYL_CHLORIDE = REGISTRATE.item("polyvinyl_chloride", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag)
        .register(),
    POLYETHENE = REGISTRATE.item("polyethene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag)
        .register(),
    POLYPROPENE = REGISTRATE.item("polypropene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag)
        .register(),
    POLYSTYRENE = REGISTRATE.item("polystyrene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag, DestroyTags.Items.POROUS_PLASTICS.tag)
        .register(),
    ABS = REGISTRATE.item("abs", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag)
        .register(),
    POLYTETRAFLUOROETHENE = REGISTRATE.item("polytetrafluoroethene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag, DestroyTags.Items.INERT_PLASTICS.tag)
        .register(),
    NYLON = REGISTRATE.item("nylon", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag, DestroyTags.Items.TEXTILE_PLASTICS.tag)
        .register(),
    POLYSTYRENE_BUTADIENE = REGISTRATE.item("polystyrene_butadiene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RUBBER_PLASTICS.tag, Tags.Items.STRING)
        .register(),
    POLYACRYLONITRILE = REGISTRATE.item("polyacrylonitrile", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag)
        .register(),
    POLYISOPRENE = REGISTRATE.item("polyisoprene", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.TEXTILE_PLASTICS.tag, DestroyTags.Items.RUBBER_PLASTICS.tag)
        .register(),
    POLYURETHANE = REGISTRATE.item("polyurethane", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag, DestroyTags.Items.TEXTILE_PLASTICS.tag, DestroyTags.Items.POROUS_PLASTICS.tag)
        .register(),
    POLYMETHYL_METHACRYLATE = REGISTRATE.item("polymethyl_methacrylate", Item::new)
        .tag(DestroyTags.Items.PLASTICS.tag, DestroyTags.Items.RIGID_PLASTICS.tag, DestroyTags.Items.TRANSPARENT_PLASTICS.tag)
        .register(),

    CARD_STOCK = REGISTRATE.item("card_stock", Item::new)
        .register(),
    CARBON_FIBER = REGISTRATE.item("carbon_fiber", Item::new)
        .register(),

    // INGOTS ETC

    FLUORITE = REGISTRATE.item("fluorite", Item::new)
        .tag(forgeItemTag("raw_materials/fluorite"), ItemTags.BEACON_PAYMENT_ITEMS, ItemTags.TRIM_MATERIALS, DestroyTags.Items.FLUXES.tag)
        .register(),
    NICKEL_INGOT = REGISTRATE.item("nickel_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/nickel"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    CHROMIUM_INGOT = REGISTRATE.item("chromium_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/chromium"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    LEAD_INGOT = REGISTRATE.item("lead_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/lead"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    OXIDIZED_SODIUM_INGOT = REGISTRATE.item("oxidized_sodium_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, Tags.Items.INGOTS, DestroyTags.Items.FLUXES.tag)
        .register(),
    PALLADIUM_INGOT = REGISTRATE.item("palladium_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/palladium"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    PLATINUM_INGOT = REGISTRATE.item("platinum_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/platinum"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    RHODIUM_INGOT = REGISTRATE.item("rhodium_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/rhodium"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    STAINLESS_STEEL_INGOT = REGISTRATE.item("stainless_steel_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/steel"), forgeItemTag("ingots/stainless_steel"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register(),
    PURE_GOLD_INGOT = REGISTRATE.item("pure_gold_ingot", Item::new)
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, Tags.Items.INGOTS, ItemTags.PIGLIN_LOVED, ItemTags.TRIM_MATERIALS)
        .register(),
    CHROMIUM_NUGGET = REGISTRATE.item("chromium_nugget", Item::new)
        .tag(Tags.Items.NUGGETS, forgeItemTag("nuggets/chromium"))
        .register(),
    ZINC_SHEET = REGISTRATE.item("zinc_sheet", Item::new)
        .tag(forgeItemTag("plates/zinc"), forgeItemTag("plates"))
        .register(),
    STAINLESS_STEEL_SHEET = REGISTRATE.item("stainless_steel_sheet", Item::new)
        .tag(forgeItemTag("plates/steel"), forgeItemTag("plates/stainless_steel"), forgeItemTag("plates"))
        .register(),
    STAINLESS_STEEL_ROD = REGISTRATE.item("stainless_steel_rod", Item::new)
        .tag(forgeItemTag("rods/steel"), forgeItemTag("rods/stainless_steel"), Tags.Items.RODS)
        .register();

    public static final ItemEntry<OxidizingItem>

    SODIUM_INGOT = REGISTRATE.item("sodium_ingot", p -> new OxidizingItem(p, DestroyItems.OXIDIZED_SODIUM_INGOT::asStack, () -> DestroyAllConfigs.SERVER.substances.sodiumDecayTime))
        .tag(DestroyTags.Items.DESTROY_INGOTS.tag, forgeItemTag("ingots/sodium"), Tags.Items.INGOTS, ItemTags.TRIM_MATERIALS)
        .register();

    public static final ItemEntry<Item>

    // RAW MATERIALS

    RAW_NICKEL = REGISTRATE.item("raw_nickel", Item::new)
        .tag(forgeItemTag("raw_materials/nickel"), Tags.Items.RAW_MATERIALS)
        .register(),
    CRUSHED_RAW_CHROMIUM = REGISTRATE.item("crushed_raw_chromium", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
        .register(),
    CRUSHED_RAW_PALLADIUM = REGISTRATE.item("crushed_raw_palladium", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
        .register(),
    CRUSHED_RAW_RHODIUM = REGISTRATE.item("crushed_raw_rhodium", Item::new)
        .tag(AllItemTags.CRUSHED_RAW_MATERIALS.tag)
        .register(),
    PURE_GOLD_DUST = REGISTRATE.item("pure_gold_dust", Item::new)
        .tag(forgeItemTag("dusts/gold"), Tags.Items.DUSTS, ItemTags.PIGLIN_LOVED)
        .register(),
    NETHER_CROCOITE = REGISTRATE.item("nether_crocoite", Item::new)
        .register(),
    BORAX = REGISTRATE.item("borax", Item::new)
        .tag(forgeItemTag("raw_materials/borax"), DestroyTags.Items.FLUXES.tag)
        .register(),
    SLAG = REGISTRATE.item("slag", Item::new)
        .register(),
    SILICA = REGISTRATE.item("silica", Item::new)
        .tag(forgeItemTag("dusts/silica"), forgeItemTag("raw_materials/silica"))
        .register(),
    ZEOLITE = REGISTRATE.item("zeolite", Item::new)
        .register();

    public static final ItemEntry<SolidBucketItem>
    
    MOLTEN_STAINLESS_STEEL_BUCKET = REGISTRATE.item("molten_stainless_steel_bucket", p -> new SolidBucketItem(DestroyBlocks.MOLTEN_STAINLESS_STEEL.get(), SoundEvents.BUCKET_EMPTY_LAVA, p))
        .register(),
    MOLTEN_BOROSILICATE_GLASS_BUCKET = REGISTRATE.item("molten_borosilicate_glass_bucket", p -> new SolidBucketItem(DestroyBlocks.MOLTEN_BOROSILICATE_GLASS.get(), SoundEvents.BUCKET_EMPTY_LAVA, p))
        .register();

    // DUSTS

    public static final ItemEntry<Item>

    COPPER_POWDER = REGISTRATE.item("copper_powder", Item::new)
        .tag(forgeItemTag("dusts/copper"), Tags.Items.DUSTS)
        .register(),
    IRON_POWDER = REGISTRATE.item("iron_powder", Item::new)
        .tag(forgeItemTag("dusts/iron"), Tags.Items.DUSTS)
        .register(),
    NICKEL_POWDER = REGISTRATE.item("nickel_powder", Item::new)
        .tag(forgeItemTag("dusts/nickel"), Tags.Items.DUSTS)
        .register(),
    CHROMIUM_POWDER = REGISTRATE.item("chromium_powder", Item::new)
        .tag(forgeItemTag("dusts/chromium"), Tags.Items.DUSTS)
        .register(),
    LEAD_POWDER = REGISTRATE.item("lead_powder", Item::new)
        .tag(forgeItemTag("dusts/lead"), Tags.Items.DUSTS)
        .register(),
    PALLADIUM_POWDER = REGISTRATE.item("palladium_powder", Item::new)
        .tag(forgeItemTag("dusts/palladium"), Tags.Items.DUSTS)
        .register(),
    PLATINUM_POWDER = REGISTRATE.item("platinum_powder", Item::new)
        .tag(forgeItemTag("dusts/platinum"), Tags.Items.DUSTS)
        .register(),
    RHODIUM_POWDER = REGISTRATE.item("rhodium_powder", Item::new)
        .tag(forgeItemTag("dusts/rhodium"), Tags.Items.DUSTS)
        .register(),
    ZINC_POWDER = REGISTRATE.item("zinc_powder", Item::new)
        .tag(forgeItemTag("dusts/zinc"), Tags.Items.DUSTS)
        .register(),

    // PRIMARY EXPLOSIVES

    ACETONE_PEROXIDE = REGISTRATE.item("acetone_peroxide", Item::new)
        .tag(DestroyTags.Items.PRIMARY_EXPLOSIVES.tag, Tags.Items.DUSTS)
        .register(),
    FULMINATED_MERCURY = REGISTRATE.item("fulminated_mercury", Item::new)
        .tag(DestroyTags.Items.PRIMARY_EXPLOSIVES.tag, Tags.Items.DUSTS)
        .register(),
    NICKEL_HYDRAZINE_NITRATE = REGISTRATE.item("nickel_hydrazine_nitrate", Item::new)
        .tag(DestroyTags.Items.PRIMARY_EXPLOSIVES.tag, Tags.Items.DUSTS)
        .register();

    public static final ItemEntry<ContactExplosiveItem>

    TOUCH_POWDER = REGISTRATE.item("touch_powder", ContactExplosiveItem::new)
        .tag(DestroyTags.Items.PRIMARY_EXPLOSIVES.tag, Tags.Items.DUSTS)
        .register();

    // SECONDARY EXPLOSIVES

    public static final ItemEntry<Item>

    ANFO = REGISTRATE.item("anfo", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag, Tags.Items.DUSTS)
        .register(),
    CORDITE = REGISTRATE.item("cordite_rods", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag)
        .register(),
    DYNAMITE = REGISTRATE.item("dynamite", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag)
        .register(),
    NITROCELLULOSE = REGISTRATE.item("nitrocellulose", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag)
        .register(),
    PICRIC_ACID_TABLET = REGISTRATE.item("picric_acid_tablet", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag)
        .register(),
    TNT_TABLET = REGISTRATE.item("tnt_tablet", Item::new)
        .tag(DestroyTags.Items.SECONDARY_EXPLOSIVES.tag)
        .register(),

    // COMPOUNDS
    
    CHALK_DUST = REGISTRATE.item("chalk_dust", Item::new)
        .tag(Tags.Items.DUSTS, forgeItemTag("dusts/chalk"), DestroyTags.Items.FLUXES.tag)
        .register(),
    CALCIUM_CARBIDE = REGISTRATE.item("calcium_carbide", Item::new)
        .register(),
    BABY_BLUE_CRYSTAL = REGISTRATE.item("baby_blue_crystal", Item::new)
        .register(),
    BABY_BLUE_POWDER = REGISTRATE.item("baby_blue_powder", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BABY_BLUE_POWDER)
        ).tag(Tags.Items.DUSTS)
        .register();

    public static final ItemEntry<CarboxylatingItem> QUICKLIME = REGISTRATE.item("quicklime", p -> new CarboxylatingItem(p, CHALK_DUST::asStack, () -> DestroyAllConfigs.SERVER.substances.quicklimeBaseDecayTime))
        .tag(Tags.Items.DUSTS, forgeItemTag("dusts/lime"), DestroyTags.Items.FLUXES.tag)
        .register();

    public static final ItemEntry<WaterSensitiveSpontaneouslyCombustingItem> SODIUM_HYDRIDE = REGISTRATE.item("sodium_hydride", WaterSensitiveSpontaneouslyCombustingItem::new)
        .tag(Tags.Items.DUSTS)
        .register();
    
    public static final ItemEntry<IodineItem> IODINE = REGISTRATE.item("iodine", IodineItem::new)
        .register();

    public static final ItemEntry<CreatineItem> CREATINE = REGISTRATE.item("creatine", CreatineItem::new)
        .properties(p -> p
            .food(DestroyFoods.CREATINE)
        ).tag(Tags.Items.DUSTS)
        .register();

    // TOOLS AND ARMOR

    public static final ItemEntry<SwissArmyKnifeItem>

    SWISS_ARMY_KNIFE = REGISTRATE.item("swiss_army_knife", (p) -> new SwissArmyKnifeItem(5f, -1f, Tiers.DIAMOND, p))    
        .properties(p -> p
            .durability(1600)
        ).register();

    public static final ItemEntry<Item>

    GAS_FILTER = REGISTRATE.item("gas_filter", Item::new)
        .register();

    public static final ItemEntry<ChemistryProtectionHeadwearItem>

    LABORATORY_GOGGLES = REGISTRATE.item("laboratory_goggles", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).onRegister(CreateRegistrate.itemModel(() -> LaboratoryGogglesModel::new))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag)
        .transform(CuriosSetup.renderOnHead())
        .transform(CuriosSetup.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyAllConfigs.SERVER.equipment.laboratoryGogglesDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(DestroyTags.Items.TRANSPARENT_PLASTICS.tag)))
        .register(),

    GOLD_LABORATORY_GOGGLES = REGISTRATE.item("gold_laboratory_goggles", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).onRegister(CreateRegistrate.itemModel(() -> GoldLaboratoryGogglesModel::new))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag)
        .transform(CuriosSetup.renderOnHead())
        .transform(CuriosSetup.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyAllConfigs.SERVER.equipment.goldLaboratoryGogglesDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(forgeItemTag("plates/gold"))))
        .onRegister(ChemistryProtectionHeadwearItem.enchantable())
        .register(),

    PAPER_MASK = REGISTRATE.item("paper_mask", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).onRegister(CreateRegistrate.itemModel(() -> PaperMaskModel::new))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_NOSE.tag, DestroyTags.Items.CHEMICAL_PROTECTION_MOUTH.tag)
        .transform(CuriosSetup.renderOnHead())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyAllConfigs.SERVER.equipment.paperMaskDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(Items.PAPER)))
        .register(),

    GAS_MASK = REGISTRATE.item("gas_mask", ChemistryProtectionHeadwearItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).onRegister(CreateRegistrate.itemModel(() -> GasMaskModel::new))
        .tag(DestroyTags.Items.CHEMICAL_PROTECTION_HEAD.tag, DestroyTags.Items.CHEMICAL_PROTECTION_EYES.tag, DestroyTags.Items.CHEMICAL_PROTECTION_NOSE.tag, DestroyTags.Items.CHEMICAL_PROTECTION_MOUTH.tag, DestroyTags.Items.CONTAMINABLE.tag, ItemTags.create(CompatMods.BIG_CANNONS.asResource("gas_mask")))
        .transform(CuriosSetup.renderOnHead())
        .onRegister(ChemistryProtectionHeadwearItem.goggles())
        .onRegister(ChemistryProtectionHeadwearItem.durability(() -> DestroyAllConfigs.SERVER.equipment.gasMaskDurability))
        .onRegister(ChemistryProtectionHeadwearItem.repairIngredient(() -> Ingredient.of(DestroyTags.Items.TEXTILE_PLASTICS.tag)))
        .register();

    public static final ItemEntry<? extends HazmatSuitArmorItem>

    HAZMAT_SUIT = REGISTRATE.item("hazmat_suit", p -> new HazmatSuitArmorItem(Type.CHESTPLATE, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyTags.Items.CHEMICAL_PROTECTION_CHEST.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register(),
    HAZMAT_LEGGINGS = REGISTRATE.item("hazmat_leggings", p -> new HazmatSuitArmorItem(Type.LEGGINGS, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyTags.Items.CHEMICAL_PROTECTION_LEGS.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register(),
    WELLINGTON_BOOTS = REGISTRATE.item("wellington_boots", p -> new HazmatSuitArmorItem(Type.BOOTS, p))
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyTags.Items.CHEMICAL_PROTECTION_FEET.tag, DestroyTags.Items.CONTAMINABLE.tag)
        .register();

    public static final ItemEntry<SeismometerItem>

    SEISMOMETER = REGISTRATE.item("seismometer", SeismometerItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).register();

    public static final ItemEntry<SeismographItem>

    SEISMOGRAPH = REGISTRATE.item("seismograph", SeismographItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).register();

    public static final ItemEntry<TestTubeItem>

    TEST_TUBE = REGISTRATE.item("test_tube", TestTubeItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).tag(DestroyTags.Items.TEST_TUBE_RACK_STORABLE.tag)
        .register();

    // TOYS

    public static final ItemEntry<BucketAndSpadeItem> BUCKET_AND_SPADE = REGISTRATE.item("bucket_and_spade", BucketAndSpadeItem::new)
        .properties(p -> p
            .durability(4)
        ).register();

    // SPRAY BOTTLES

    public static final ItemEntry<Item>

    SPRAY_BOTTLE = REGISTRATE.item("spray_bottle", Item::new)
        .tag(DestroyTags.Items.SPRAY_BOTTLES.tag)
        .register();

    public static final ItemEntry<SprayBottleItem>
    
    PERFUME_BOTTLE = REGISTRATE.item("perfume_bottle", p -> new SprayBottleItem(p, new MobEffectInstance(DestroyMobEffects.FRAGRANCE.get(), 12000, 0)))
        .tag(DestroyTags.Items.SPRAY_BOTTLES.tag)
        .register(),
    SUNSCREEN_BOTTLE = REGISTRATE.item("sunscreen_bottle", p -> new SprayBottleItem(p, new MobEffectInstance(DestroyMobEffects.SUN_PROTECTION.get(), 12000, 0, false, false, true)))
        .tag(DestroyTags.Items.SPRAY_BOTTLES.tag)
        .register();

    public static final ItemEntry<SimpleFoiledItem>

    MAGIC_OXIDANT = REGISTRATE.item("magic_oxidant", SimpleFoiledItem::new)
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .register(),
    MAGIC_REDUCTANT = REGISTRATE.item("magic_reductant", SimpleFoiledItem::new)
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .register();

    // FOOD AND DRINK

    @MoveToPetrolparkLibrary //TODO also remove models, lang entries and textures
    public static final ItemEntry<Item>

    BUTTER = REGISTRATE.item("butter", Item::new)
        .properties(p -> p
            .food(DestroyFoods.BUTTER)
        ).register(),
    RAW_FRIES = REGISTRATE.item("raw_fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.RAW_FRIES)
        ).register(),
    FRIES = REGISTRATE.item("fries", Item::new)
        .properties(p -> p
            .food(DestroyFoods.FRIES)
        ).register(),
    MASHED_POTATO = REGISTRATE.item("mashed_potato", Item::new)
        .properties(p -> p
            .food(DestroyFoods.MASHED_POTATO)
        ).register();

    public static final ItemEntry<Item>

    EMPTY_CARTON = REGISTRATE.item("empty_carton", Item::new)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register();

    public static final ItemEntry<ChewingGumItem>

    CHEWING_GUM = REGISTRATE.item("chewing_gum", ChewingGumItem::new)
    .properties(p -> p
        .food(DestroyFoods.CHEWING_GUM)
    ).register();

    public static final ItemEntry<StackableBowlFoodItem>
    
    BANGERS_AND_MASH = REGISTRATE.item("bangers_and_mash", StackableBowlFoodItem::new)
        .properties(p -> p
            .food(DestroyFoods.BANGERS_AND_MASH)
        ).register();

    public static final ItemEntry<DrinkItem>

    APPLE_JUICE_CARTON = REGISTRATE.item("apple_juice_carton", DrinkItem::new)
        .properties(p -> p
            .food(DestroyFoods.APPLE_JUICE)
            .craftRemainder(EMPTY_CARTON.get())
        ).tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register();

    public static final ItemEntry<MilkCartonItem>

    MILK_CARTON = REGISTRATE.item("milk_carton", MilkCartonItem::new)
        .properties(p -> p
            .food(DestroyFoods.MILK_CARTON)
            .craftRemainder(EMPTY_CARTON.get())
        ).tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register();

    public static final ItemEntry<AlcoholicDrinkItem>

    UNDISTILLED_MOONSHINE_BOTTLE = REGISTRATE.item("undistilled_moonshine_bottle", p -> new AlcoholicDrinkItem(p, 1))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .register(),

    MOONSHINE_BOTTLE = REGISTRATE.item("moonshine_bottle", p -> new AlcoholicDrinkItem(p, 3))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .register();
    
    public static final ItemEntry<ChorusWineItem>
    
    CHORUS_WINE_BOTTLE = REGISTRATE.item("chorus_wine_bottle", p -> new ChorusWineItem(p, 1))
        .properties(p -> p
            .food(DestroyFoods.MOONSHINE)
            .craftRemainder(Items.GLASS_BOTTLE)
            .stacksTo(16)
        ).tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .tag(DestroyTags.Items.ALCOHOLIC_DRINKS.tag)
        .register();

    // SEQUENCED ASSEMBLY INTERMEDIATES

    public static final ItemEntry<SequencedAssemblyItem>

    UNFINISHED_BLACKLIGHT = REGISTRATE.item("unfinished_blacklight", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_CATALYTIC_CONVERTER = REGISTRATE.item("unfinished_catalytic_converter", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_CIRCUIT_BOARD = REGISTRATE.item("unfinished_circuit_board", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_CARD_STOCK = REGISTRATE.item("unfinished_card_stock", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_VOLTAIC_PILE = REGISTRATE.item("unfinished_voltaic_pile", SequencedAssemblyItem::new)
        .tab(null)
        .register(),
    UNFINISHED_UNVARNISHED_PLYWOOD = REGISTRATE.item("unfinished_unvarnished_plywood", SequencedAssemblyItem::new)
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
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(1000))
        .register(),
    BOMB_BON = REGISTRATE.item("bomb_bon", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(20000))
        .register(),
    NAPALM_SUNDAE = REGISTRATE.item("napalm_sundae", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag, AllItemTags.UPRIGHT_ON_BELT.tag)
        .onRegister(i -> i.setBurnTime(20000))
        .register(),
    THERMITE_BROWNIE = REGISTRATE.item("thermite_brownie", CombustibleItem::new)
        .tag(AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.tag)
        .onRegister(i -> i.setBurnTime(20000))
        .register();

    // BEETROOT

    public static final ItemEntry<ItemNameBlockItem>

    MAGIC_BEETROOT_SEEDS = REGISTRATE.item("magic_beetroot_seeds", p -> new ItemNameBlockItem(DestroyBlocks.MAGIC_BEETROOT_SHOOTS.get(), p))
        .tag(Tags.Items.SEEDS)
        .tag(Tags.Items.SEEDS_BEETROOT)
        .register();

    public static final ItemEntry<Item>

    HEFTY_BEETROOT = REGISTRATE.item("hefty_beetroot", Item::new)
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register();

    public static final ItemEntry<WithSecondaryItem> 

    COAL_INFUSED_BEETROOT = REGISTRATE.item("coal_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.COAL)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    COPPER_INFUSED_BEETROOT = REGISTRATE.item("copper_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_COPPER)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT = REGISTRATE.item("diamond_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.DIAMOND)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT = REGISTRATE.item("emerald_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.EMERALD)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT = REGISTRATE.item("fluorite_infused_beetroot", p -> new WithSecondaryItem(p, i -> FLUORITE.asStack()))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    GOLD_INFUSED_BEETROOT = REGISTRATE.item("gold_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_GOLD)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    IRON_INFUSED_BEETROOT = REGISTRATE.item("iron_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_IRON)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT = REGISTRATE.item("lapis_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.LAPIS_LAZULI)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT = REGISTRATE.item("nickel_infused_beetroot", p -> new WithSecondaryItem(p, i -> RAW_NICKEL.asStack()))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    NETHER_CROCOITE_INFUSED_BEETROOT = REGISTRATE.item("nether_crocoite_infused_beetroot", p -> new WithSecondaryItem(p, i -> NETHER_CROCOITE.asStack()))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    QUARTZ_INFUSED_BEETROOT = REGISTRATE.item("quartz_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.QUARTZ)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT = REGISTRATE.item("redstone_infused_beetroot", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.REDSTONE)))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register(),
    ZINC_INFUSED_BEETROOT = REGISTRATE.item("zinc_infused_beetroot", p -> new WithSecondaryItem(p, i -> AllItems.RAW_ZINC.asStack()))
        .tag(DestroyTags.Items.HEFTY_BEETROOTS.tag)
        .register();
    
    // BEETROOT ASHES

    public static final ItemEntry<Item>

    BEETROOT_ASHES = REGISTRATE.item("beetroot_ashes", Item::new)
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register();

    public static final ItemEntry<WithSecondaryItem> 

    COAL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("coal_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.COAL)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    COPPER_INFUSED_BEETROOT_ASHES = REGISTRATE.item("copper_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_COPPER)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    DIAMOND_INFUSED_BEETROOT_ASHES = REGISTRATE.item("diamond_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.DIAMOND)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    EMERALD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("emerald_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.EMERALD)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    FLUORITE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("fluorite_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> FLUORITE.asStack()))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    GOLD_INFUSED_BEETROOT_ASHES = REGISTRATE.item("gold_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_GOLD)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    IRON_INFUSED_BEETROOT_ASHES = REGISTRATE.item("iron_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.RAW_IRON)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    LAPIS_INFUSED_BEETROOT_ASHES = REGISTRATE.item("lapis_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.LAPIS_LAZULI)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    NETHER_CROCOITE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("nether_crocoite_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> NETHER_CROCOITE.asStack()))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    NICKEL_INFUSED_BEETROOT_ASHES = REGISTRATE.item("nickel_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> RAW_NICKEL.asStack()))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    QUARTZ_INFUSED_BEETROOT_ASHES = REGISTRATE.item("quartz_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.QUARTZ)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    REDSTONE_INFUSED_BEETROOT_ASHES = REGISTRATE.item("redstone_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> new ItemStack(Items.REDSTONE)))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register(),
    ZINC_INFUSED_BEETROOT_ASHES = REGISTRATE.item("zinc_infused_beetroot_ashes", p -> new WithSecondaryItem(p, i -> AllItems.RAW_ZINC.asStack()))
        .tag(DestroyTags.Items.BEETROOT_ASHES.tag)
        .register();

    // SYRINGES

    public static final ItemEntry<Item> SYRINGE = REGISTRATE.item("syringe", Item::new)
        .tag(DestroyTags.Items.SYRINGES.tag)
        .register();

    public static final ItemEntry<? extends SyringeItem>

    ASPIRIN_SYRINGE = REGISTRATE.item("aspirin_syringe", AspirinSyringeItem::new)
        .tag(DestroyTags.Items.SYRINGES.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 16716136 : -1)
        .register(),

    BABY_BLUE_SYRINGE = REGISTRATE.item("baby_blue_syringe", p -> new BabyBlueSyringeItem(p, 1200, 1))
        .tag(DestroyTags.Items.SYRINGES.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 8825802 : -1)
        .register(),

    CISPLATIN_SYRINGE = REGISTRATE.item("cisplatin_syringe", CisplatinSyringeItem::new)
        .tag(DestroyTags.Items.SYRINGES.tag)
        .color(() -> () -> (stack, tintIndex) -> tintIndex == 0 ? 11459547 : -1)
        .register();

    // UNCATEGORISED

    public static final ItemEntry<Item>

    DISCHARGED_VOLTAIC_PILE = REGISTRATE.item("discharged_voltaic_pile", Item::new)
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .register(),
    MESH = REGISTRATE.item("mesh", Item::new)
        .register(),
    TEAR_BOTTLE = REGISTRATE.item("tear_bottle", Item::new)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    URINE_BOTTLE = REGISTRATE.item("urine_bottle", Item::new)
        .tag(AllItemTags.UPRIGHT_ON_BELT.tag)
        .register(),
    VOLTAIC_PILE = REGISTRATE.item("voltaic_pile", Item::new)
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .register(),
    YEAST = REGISTRATE.item("yeast", Item::new)
        .tag(DestroyTags.Items.FERTILIZERS.tag, DestroyTags.Items.YEAST.tag)
        .register(),
    NANODIAMONDS = REGISTRATE.item("nanodiamonds", Item::new)
        .register();

    public static final ItemEntry<CircuitMaskItem> CIRCUIT_MASK = REGISTRATE.item("circuit_mask", CircuitMaskItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).register();
    public static final ItemEntry<CircuitBoardItem> CIRCUIT_BOARD = REGISTRATE.item("circuit_board", CircuitBoardItem::new)
        .register();

    public static final ItemEntry<Item>
    
    RUINED_CIRCUIT_MASK = REGISTRATE.item("ruined_circuit_mask", Item::new)
        .register(),
    RUINED_CIRCUIT_BOARD = REGISTRATE.item("ruined_circuit_board", Item::new)
        .register();

    public static final ItemEntry<HyperaccumulatingFertilizerItem> HYPERACCUMULATING_FERTILIZER = REGISTRATE.item("hyperaccumulating_fertilizer", HyperaccumulatingFertilizerItem::new)
        .tag(Tags.Items.DUSTS, DestroyTags.Items.BONEMEAL_BYPASSES_POLLUTION.tag)
        .register();

    public static final ItemEntry<ConfettiItem>

    CONFETTI = REGISTRATE.item("confetti", p -> new ConfettiItem(p, ConfettoParticleData::new))
        .register(),
    WHITE_CONFETTI = REGISTRATE.item("white_confetti", p -> new ConfettiItem(p, ConfettoParticleData.White::new))
        .register();


    // MUSIC RELATED

    public static final ItemEntry<DiscStamperItem> DISC_STAMPER = REGISTRATE.item("disc_stamper", DiscStamperItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).register();

    public static final ItemEntry<BlankRecordItem> BLANK_MUSIC_DISC = REGISTRATE.item("blank_music_disc", BlankRecordItem::new)
        .tag(ItemTags.MUSIC_DISCS)
        .register();

    public static final ItemEntry<RecordItem> MUSIC_DISC_SPECTRUM = REGISTRATE.item("music_disc_spectrum", p -> new RecordItem(9, () -> DestroySoundEvents.MUSIC_DISC_SPECTRUM.getMainEvent(), p, 3720))
        .properties(p -> p
            .stacksTo(1)
        ).tag(ItemTags.MUSIC_DISCS)
        .register();

    public static void register() {};
}
