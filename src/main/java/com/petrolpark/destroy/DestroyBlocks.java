package com.petrolpark.destroy;

import static com.petrolpark.destroy.Destroy.REGISTRATE;
import static com.simibubi.create.AllTags.forgeItemTag;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;
import static com.simibubi.create.foundation.data.CreateRegistrate.casingConnectivity;
import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.config.DestroyStressConfigs;
import com.petrolpark.destroy.content.logistics.creativepump.CreativePumpBlock;
import com.petrolpark.destroy.content.logistics.siphon.SiphonBlock;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackBlock;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackBlockItem;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackCamBlock;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackStructuralBlock;
import com.petrolpark.destroy.content.processing.ageing.AgingBarrelBlock;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeBlock;
import com.petrolpark.destroy.content.processing.cooler.CoolerBlock;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlock;
import com.petrolpark.destroy.content.processing.dynamo.DynamoBlock;
import com.petrolpark.destroy.content.processing.dynamo.arcfurnace.ArcFurnaceLidBlock;
import com.petrolpark.destroy.content.processing.extrusion.ExtrusionDieBlock;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlock;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeItem;
import com.petrolpark.destroy.content.processing.moltenblock.BorosilicateGlassFiberBlock;
import com.petrolpark.destroy.content.processing.moltenblock.FastCoolingMoltenPillarBlock;
import com.petrolpark.destroy.content.processing.moltenblock.MoltenBorosilicateGlassBlock;
import com.petrolpark.destroy.content.processing.moltenblock.MoltenStainlessSteelBlock;
import com.petrolpark.destroy.content.processing.phytomining.HeftyBeetrootBlock;
import com.petrolpark.destroy.content.processing.phytomining.MagicBeetrootShootsBlock;
import com.petrolpark.destroy.content.processing.sieve.MechanicalSieveBlock;
import com.petrolpark.destroy.content.processing.treetap.TreeTapBlock;
import com.petrolpark.destroy.content.processing.trypolithography.keypunch.KeypunchBlock;
import com.petrolpark.destroy.content.product.alcohol.UrineCauldronBlock;
import com.petrolpark.destroy.content.product.periodictable.PeriodicTableBlock;
import com.petrolpark.destroy.content.product.periodictable.PeriodicTableBlockItem;
import com.petrolpark.destroy.content.product.periodictable.TankPeriodicTableBlock;
import com.petrolpark.destroy.content.product.periodictable.TankPeriodicTableBlockItem;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerBlock;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerBlockItem;
import com.petrolpark.destroy.content.sandcastle.SandCastleBlock;
import com.petrolpark.destroy.core.block.FlippableRotatedPillarBlock;
import com.petrolpark.destroy.core.block.FullyGrownCropBlock;
import com.petrolpark.destroy.core.block.copycat.CopycatFullBlockModel;
import com.petrolpark.destroy.core.chemistry.storage.ElementTankBlock;
import com.petrolpark.destroy.core.chemistry.storage.SimplePlaceableMixtureTankBlock;
import com.petrolpark.destroy.core.chemistry.storage.SimplePlaceableMixtureTankBlockItem;
import com.petrolpark.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlock;
import com.petrolpark.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlockItem;
import com.petrolpark.destroy.core.chemistry.storage.testtube.TestTubeRackBlock;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlock;
import com.petrolpark.destroy.core.chemistry.vat.VatSideBlock;
import com.petrolpark.destroy.core.chemistry.vat.observation.colorimeter.ColorimeterBlock;
import com.petrolpark.destroy.core.chemistry.vat.uv.BlacklightBlock;
import com.petrolpark.destroy.core.explosion.DynamiteBlock;
import com.petrolpark.destroy.core.explosion.PrimeableBombBlock;
import com.petrolpark.destroy.core.explosion.PrimedBombEntity;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlock;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockItem;
import com.petrolpark.destroy.core.item.CombustibleBlockItem;
import com.petrolpark.destroy.core.pollution.PollutometerBlock;
import com.petrolpark.destroy.core.pollution.catalyticconverter.CatalyticConverterBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.Tags;

public class DestroyBlocks {

    // BLOCK ENTITIES

    public static final BlockEntry<AgingBarrelBlock> AGING_BARREL = REGISTRATE.block("aging_barrel", AgingBarrelBlock::new)
        .initialProperties(SharedProperties::wooden)
        .properties(p -> p
            .mapColor(MapColor.COLOR_BROWN)
            .noOcclusion()
        ).transform(TagGen.axeOnly())
        .item()
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<BlowpipeBlock> BLOWPIPE = REGISTRATE.block("blowpipe", BlowpipeBlock::new)
        .initialProperties(AllBlocks.SHAFT)
        .properties(p -> p
            .mapColor(MapColor.NONE)
        ).item(BlowpipeItem::new)
        .build()
        .register();

    public static final BlockEntry<BubbleCapBlock> BUBBLE_CAP = REGISTRATE.block("bubble_cap", BubbleCapBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(displaySource(DestroyDisplaySources.BUBBLE_CAP))
        .transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CatalyticConverterBlock> CATALYTIC_CONVERTER = REGISTRATE.block("catalytic_converter", CatalyticConverterBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.block("centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(displaySource(DestroyDisplaySources.CENTRIFUGE_INPUT))
        .transform(displaySource(DestroyDisplaySources.CENTRIFUGE_DENSE_OUTPUT))
        .transform(displaySource(DestroyDisplaySources.CENTRIFUGE_LIGHT_OUTPUT))
        .blockstate((c,p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c,p)))
        .transform(TagGen.pickaxeOnly())
        .transform(DestroyStressConfigs.setImpact(5.0))
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<ColorimeterBlock> COLORIMETER = REGISTRATE.block("colorimeter", ColorimeterBlock::new)
        .initialProperties(() -> Blocks.OBSERVER)
        .transform(TagGen.pickaxeOnly())
        .transform(displaySource(DestroyDisplaySources.COLORIMETER))
        .item()
        .build()
        .register();

    public static final BlockEntry<CoolerBlock> COOLER = REGISTRATE.block("cooler", CoolerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .mapColor(MapColor.COLOR_GRAY)
            .noOcclusion()
            .sound(DestroySoundTypes.COOLER)
        ).transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CreativePumpBlock> CREATIVE_PUMP = REGISTRATE.block("creative_pump", CreativePumpBlock::new)
        .initialProperties(AllBlocks.MECHANICAL_PUMP)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .forceSolidOn()
        ).transform(TagGen.pickaxeOnly())
        .item()
        .properties(p -> p
            .rarity(Rarity.EPIC)
        ).build()
        .register();

    public static final BlockEntry<MixedExplosiveBlock> CUSTOM_EXPLOSIVE_MIX = REGISTRATE.block("custom_explosive_mix", MixedExplosiveBlock::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.SNOW)
        ).item(MixedExplosiveBlockItem::new)
        .onRegister(registerPrimeableBombDispenserBehaviour())
        .build()
        .register();

    public static final BlockEntry<DynamoBlock> DYNAMO = REGISTRATE.block("dynamo", DynamoBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p
            .mapColor(MapColor.GOLD)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .transform(DestroyStressConfigs.setImpact(6.0))
        .item(AssemblyOperatorBlockItem::new)
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<ArcFurnaceLidBlock> ARC_FURNACE_LID = REGISTRATE.block("arc_furnace_lid", ArcFurnaceLidBlock::new)
        .initialProperties(AllBlocks.BASIN)
        .transform(TagGen.pickaxeOnly())
        .register();

    public static final BlockEntry<ElementTankBlock> ELEMENT_TANK = REGISTRATE.block("element_tank", ElementTankBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .strength(4f)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn(DestroyBlocks::never)
            .isRedstoneConductor(DestroyBlocks::never)
            .isSuffocating(DestroyBlocks::never)
            .isViewBlocking(DestroyBlocks::never)
        ).transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<ExtrusionDieBlock> EXTRUSION_DIE = REGISTRATE.block("extrusion_die", ExtrusionDieBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(BlockBehaviour.Properties::noCollission
        ).transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<KeypunchBlock> KEYPUNCH = REGISTRATE.block("keypunch", KeypunchBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(BlockBehaviour.Properties::noOcclusion
        ).transform(TagGen.axeOrPickaxe())
        .item(AssemblyOperatorBlockItem::new)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<MeasuringCylinderBlock> MEASURING_CYLINDER = REGISTRATE.block("measuring_cylinder", MeasuringCylinderBlock::new)
        .item(MeasuringCylinderBlockItem::new)
        .properties(p -> p
            .stacksTo(1)
        ).build()
        .register();

    public static final BlockEntry<MechanicalSieveBlock> MECHANICAL_SIEVE = REGISTRATE.block("mechanical_sieve", MechanicalSieveBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(BlockBehaviour.Properties::noOcclusion
        ).transform(DestroyStressConfigs.setImpact(0.5d))
        .transform(TagGen.axeOrPickaxe())
        .item()
        .transform(customItemModel())
        .register();
        
    public static final BlockEntry<PollutometerBlock> POLLUTOMETER = REGISTRATE.block("pollutometer", PollutometerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .mapColor(MapColor.NONE)
            .noOcclusion()
        ).transform(displaySource(DestroyDisplaySources.POLLUTOMETER))
        .transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<PumpjackBlock> PUMPJACK = REGISTRATE.block("pumpjack", PumpjackBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
            .isSuffocating((state, level, pos) -> false)
        ).transform(TagGen.pickaxeOnly())
        .transform(DestroyStressConfigs.setImpact(8.0))
        .item(PumpjackBlockItem::new)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<PumpjackCamBlock> PUMPJACK_CAM = REGISTRATE.block("pumpjack_cam", PumpjackCamBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
            .isSuffocating((state, level, pos) -> false)
        ).transform(TagGen.pickaxeOnly())
        .blockstate(BlockStateGen.horizontalAxisBlockProvider(false))
        .register();

    public static final BlockEntry<PumpjackStructuralBlock> PUMPJACK_STRUCTURAL = REGISTRATE.block("pumpjack_structure", PumpjackStructuralBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .noOcclusion()
            .isSuffocating((state, level, pos) -> false)
        ).transform(TagGen.pickaxeOnly())
        .blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(BlockStateGen.mapToAir(p), PumpjackStructuralBlock.FACING)
        ).register();

    public static final BlockEntry<RedstoneProgrammerBlock> REDSTONE_PROGRAMMER = REGISTRATE.block("redstone_programmer", RedstoneProgrammerBlock::new)
        .initialProperties(SharedProperties::wooden)
        .properties(p -> p
            .noOcclusion()
            .noLootTable() // Handled in RedstoneProgrammerBlock class
        ).item(RedstoneProgrammerBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<SandCastleBlock> SAND_CASTLE = REGISTRATE.block("sand_castle", SandCastleBlock::new)
        .initialProperties(() -> Blocks.POPPY)
        .properties(p -> p
            .mapColor(MapColor.SAND)
            .noOcclusion()
            .noLootTable()
            .instabreak()
            .sound(SoundType.SAND)
        ).register();

    public static final BlockEntry<SiphonBlock> SIPHON = REGISTRATE.block("siphon", SiphonBlock::new)
        .initialProperties(AllBlocks.FLUID_TANK)
        .properties(p -> p
            .mapColor(MapColor.METAL)
        ).transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<TestTubeRackBlock> TEST_TUBE_RACK = REGISTRATE.block("test_tube_rack", TestTubeRackBlock::new)
        .initialProperties(() -> Blocks.OAK_PLANKS)
        .properties(p -> p
        ).tag(BlockTags.MINEABLE_WITH_AXE)
        .item()
        .build()
        .register();

    public static final BlockEntry<TreeTapBlock> TREE_TAP = REGISTRATE.block("tree_tap", TreeTapBlock::new)
        .initialProperties(AllBlocks.DEPLOYER)
        .transform(TagGen.axeOrPickaxe())
        .item()
        .build()
        .register();

    public static final BlockEntry<VatControllerBlock> VAT_CONTROLLER = REGISTRATE.block("vat_controller", VatControllerBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(BlockBehaviour.Properties::noOcclusion
        ).onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, DestroySpriteShifts.STAINLESS_STEEL_BLOCK,
			(s, f) -> f != s.getValue(VatControllerBlock.FACING)))
        ).transform(displaySource(DestroyDisplaySources.VAT_CONTROLLER_ALL))
        .transform(displaySource(DestroyDisplaySources.VAT_CONTROLLER_SOLUTION))
        .transform(displaySource(DestroyDisplaySources.VAT_CONTROLLER_GAS))
        .transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<VatSideBlock> VAT_SIDE = REGISTRATE.block("vat_side", VatSideBlock::new)
        .transform(BuilderTransformers.copycat())
        .properties(p -> p
            .isViewBlocking(DestroyBlocks::never)
        ).onRegister(CreateRegistrate.blockModel(() -> CopycatFullBlockModel::new))
        .transform(displaySource(DestroyDisplaySources.VAT_SIDE_ALL))
        .transform(displaySource(DestroyDisplaySources.VAT_SIDE_SOLUTION))
        .transform(displaySource(DestroyDisplaySources.VAT_SIDE_GAS))
        .register();

    public static final BlockEntry<UrineCauldronBlock> URINE_CAULDRON = REGISTRATE.block("urine_cauldron", p -> new UrineCauldronBlock(p, DestroyCauldronInteractions.URINE))
        .initialProperties(() -> Blocks.WATER_CAULDRON)
        .tag(BlockTags.CAULDRONS)
        .register();

    public static final BlockEntry<BlacklightBlock> BLACKLIGHT = REGISTRATE.block("blacklight", BlacklightBlock::new)
        .initialProperties(() -> Blocks.LANTERN)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .sound(SoundType.GLASS)
            .forceSolidOn()
        ).transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register();

    public static final BlockEntry<SimplePlaceableMixtureTankBlock>
    
    BEAKER = REGISTRATE.block("beaker", SimplePlaceableMixtureTankBlock.of(() -> DestroyAllConfigs.SERVER.blocks.beakerCapacity.get(), 5.5f, 0.5f, 5.5f, 10.5f, 7f, 10.5f, DestroyVoxelShapes.BEAKER))
        .initialProperties(MEASURING_CYLINDER)
        .item(SimplePlaceableMixtureTankBlockItem::new)
        .build()
        .register(),

    ROUND_BOTTOMED_FLASK = REGISTRATE.block("round_bottomed_flask", SimplePlaceableMixtureTankBlock.of(() -> DestroyAllConfigs.SERVER.blocks.roundBottomedFlaskCapacity.get(), 5.5f, 0.5f, 5.5f, 10.5f, 4.5f, 10.5f, DestroyVoxelShapes.ROUND_BOTTOMED_FLASK))
        .initialProperties(BEAKER)
        .item(SimplePlaceableMixtureTankBlockItem::new)
        .build()
        .register();

    // EXPLOSIVES

    public static final BlockEntry<PrimeableBombBlock<PrimedBombEntity.Anfo>> ANFO_BLOCK = REGISTRATE.block("anfo_block", p -> new PrimeableBombBlock<PrimedBombEntity.Anfo>(p, PrimedBombEntity.Anfo::new))
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PINK)
        ).item()
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .onRegister(registerPrimeableBombDispenserBehaviour())
        .build()
        .register();

    public static final BlockEntry<PrimeableBombBlock<PrimedBombEntity.Cordite>> CORDITE = REGISTRATE.block("cordite", p -> new PrimeableBombBlock<PrimedBombEntity.Cordite>(p, PrimedBombEntity.Cordite::new))
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
        ).item()
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .onRegister(registerPrimeableBombDispenserBehaviour())
        .build()
        .register();

    public static final BlockEntry<DynamiteBlock> DYNAMITE_BLOCK = REGISTRATE.block("dynamite_block", DynamiteBlock::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.COLOR_MAGENTA)
        ).item()
        .build()
        .register();

    public static final BlockEntry<PrimeableBombBlock<PrimedBombEntity.Nitrocellulose>> NITROCELLULOSE_BLOCK = REGISTRATE.block("nitrocellulose_block", p -> new PrimeableBombBlock<PrimedBombEntity.Nitrocellulose>(p, PrimedBombEntity.Nitrocellulose::new))
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.COLOR_LIGHT_GREEN)
        ).item()
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .tag(DestroyTags.Items.OBLITERATION_EXPLOSIVES.tag)
        .onRegister(registerPrimeableBombDispenserBehaviour())
        .build()
        .register();

    public static final BlockEntry<PrimeableBombBlock<PrimedBombEntity.PicricAcid>> PICRIC_ACID_BLOCK = REGISTRATE.block("picric_acid_block", (p) -> new PrimeableBombBlock<PrimedBombEntity.PicricAcid>(p, PrimedBombEntity.PicricAcid::new))
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
        ).item()
        .tag(DestroyTags.Items.LIABLE_TO_CHANGE.tag)
        .onRegister(registerPrimeableBombDispenserBehaviour())
        .build()
        .register();

    // STORAGE BLOCKS

    public static final BlockEntry<Block> CARBON_FIBER_BLOCK = REGISTRATE.block("carbon_fiber_block", Block::new)
        .initialProperties(() -> Blocks.OBSIDIAN)
        .properties(p -> p
            .strength(40f, 800f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(DestroyTags.Blocks.ARC_FURNACE_TRANSFORMABLE.tag)
        .transform(TagGen.tagBlockAndItem("storage_blocks/carbon_fiber"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    FLUORITE_BLOCK = REGISTRATE.block("fluorite_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .strength(6f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/fluorite"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    RAW_NICKEL_BLOCK = REGISTRATE.block("raw_nickel_block", Block::new)
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.SAND)
            .requiresCorrectToolForDrops()
            .strength(5f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/raw_nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    CHROMIUM_BLOCK = REGISTRATE.block("chromium_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .requiresCorrectToolForDrops()
            .strength(5f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/chromium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    IODINE_BLOCK = REGISTRATE.block("iodine_block", Block::new)
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.COLOR_GRAY)
            .strength(2f, 2f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL, Tags.Blocks.STORAGE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/iodine"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),

    NETHER_CROCOITE_BLOCK = REGISTRATE.block("nether_crocoite_block", Block::new)
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(2f, 2f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS)
        .item()
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    NICKEL_BLOCK = REGISTRATE.block("nickel_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.SAND)
            .requiresCorrectToolForDrops()
            .strength(5f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    PALLADIUM_BLOCK = REGISTRATE.block("palladium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.DIRT)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(6f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/palladium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
        
    PLATINUM_BLOCK = REGISTRATE.block("platinum_block", Block::new)
        .initialProperties(() -> Blocks.DIAMOND_BLOCK)
        .properties(p -> p
            .requiresCorrectToolForDrops()
            .instrument(NoteBlockInstrument.BELL)
            .strength(6f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/platinum"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),
    
    RHODIUM_BLOCK = REGISTRATE.block("rhodium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
            .instrument(NoteBlockInstrument.BELL)
            .requiresCorrectToolForDrops()
            .strength(6f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/rhodium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register(),

    LEAD_BLOCK = REGISTRATE.block("lead_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .requiresCorrectToolForDrops()
            .strength(7f, 6f)
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/lead"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();
        
    public static final BlockEntry<CasingBlock> STAINLESS_STEEL_BLOCK = REGISTRATE.block("stainless_steel_block", CasingBlock::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .sound(SoundType.METAL)
            .strength(7f, 8f)
        ).transform(TagGen.pickaxeOnly())
        .properties(p -> p.sound(SoundType.COPPER))
        .blockstate((c, p) -> p.simpleBlock(c.get()))
        .onRegister(connectedTextures(() -> new EncasedCTBehaviour(DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/stainless_steel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<RotatedPillarBlock> CHISELED_RHODIUM_BLOCK = REGISTRATE.block("chiseled_rhodium_block", RotatedPillarBlock::new)
        .initialProperties(RHODIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/rhodium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    // ORES

    public static final BlockEntry<Block> FLUORITE_ORE = REGISTRATE.block("fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(3f, 3f)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_FLUORITE_ORE = REGISTRATE.block("deepslate_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
            .strength(4.5f, 3f)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> END_FLUORITE_ORE = REGISTRATE.block("end_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.END_STONE)
        .properties(p -> p
            .mapColor(MapColor.COLOR_PURPLE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(4f, 9f)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/end_stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block("nickel_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .mapColor(MapColor.SAND)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(3f, 3f)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/nickel", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block("deepslate_nickel_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .mapColor(MapColor.SAND)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
            .strength(4.5f, 3f)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get()).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/nickel", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> NETHER_CROCOITE_ORE = REGISTRATE.block("nether_crocoite_ore", Block::new)
        .initialProperties(() -> Blocks.NETHER_QUARTZ_ORE)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.NETHERRACK)
            .requiresCorrectToolForDrops()
        ).onRegister(CreateRegistrate.connectedTextures(() -> new SimpleCTBehaviour(DestroySpriteShifts.NETHER_CROCOITE_BLOCK)))
        .transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, lt.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.NETHER_CROCOITE.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 5.0f))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))))))
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES, Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Blocks.ORE_BEARING_GROUND_NETHERRACK)
        .item()
        .tag(Tags.Items.ORES)
        .build()
        .register();

    // CROPS

    @SuppressWarnings("removal")
    public static final BlockEntry<MagicBeetrootShootsBlock>

    MAGIC_BEETROOT_SHOOTS = REGISTRATE.block("magic_beetroot_shoots", MagicBeetrootShootsBlock::new)
        .addLayer(() -> RenderType::cutout)
        .initialProperties(() -> Blocks.BEETROOTS)
        .register();

    public static final BlockEntry<FullyGrownCropBlock>

    GOLDEN_CARROTS = REGISTRATE.block("golden_carrots", p -> new FullyGrownCropBlock(p, () -> Items.GOLDEN_CARROT))
        .loot((lt, b) ->
            lt.add(b, lt.createCropDrops(b, Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, LootItemBlockStatePropertyCondition.hasBlockStateProperties(b)))
        ).initialProperties(() -> Blocks.CARROTS)
        .tag(BlockTags.CROPS)
        .register();

    public static final BlockEntry<HeftyBeetrootBlock> 

    HEFTY_BEETROOT = REGISTRATE.block("hefty_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.HEFTY_BEETROOT))
        .initialProperties(() -> Blocks.BEETROOTS)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    COAL_INFUSED_BEETROOT = REGISTRATE.block("coal_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.COAL_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    COPPER_INFUSED_BEETROOT = REGISTRATE.block("copper_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.COPPER_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    DIAMOND_INFUSED_BEETROOT = REGISTRATE.block("diamond_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.DIAMOND_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    EMERALD_INFUSED_BEETROOT = REGISTRATE.block("emerald_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.EMERALD_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    FLUORITE_INFUSED_BEETROOT = REGISTRATE.block("fluorite_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.FLUORITE_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    GOLD_INFUSED_BEETROOT = REGISTRATE.block("gold_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.GOLD_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    IRON_INFUSED_BEETROOT = REGISTRATE.block("iron_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.IRON_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    LAPIS_INFUSED_BEETROOT = REGISTRATE.block("lapis_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.LAPIS_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    NICKEL_INFUSED_BEETROOT = REGISTRATE.block("nickel_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.NICKEL_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    NETHER_CROCOITE_INFUSED_BEETROOT = REGISTRATE.block("nether_crocoite_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.NETHER_CROCOITE_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    QUARTZ_INFUSED_BEETROOT = REGISTRATE.block("quartz_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.QUARTZ_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    REDSTONE_INFUSED_BEETROOT = REGISTRATE.block("redstone_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.REDSTONE_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register(),

    ZINC_INFUSED_BEETROOT = REGISTRATE.block("zinc_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.ZINC_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .tag(DestroyTags.Blocks.BEETROOTS.tag)
        .register();

    // Periodic Table blocks

    public static final BlockEntry<TankPeriodicTableBlock>

    HYDROGEN_PERIODIC_TABLE_BLOCK = REGISTRATE.block("hydrogen_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0x20FFFFFF))
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .strength(2f)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn(DestroyBlocks::never)
            .isRedstoneConductor(DestroyBlocks::never)
            .isSuffocating(DestroyBlocks::never)
            .isViewBlocking(DestroyBlocks::never)
        ).transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<PeriodicTableBlock>

    CARBON_PERIODIC_TABLE_BLOCK = REGISTRATE.block("carbon_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(CARBON_FIBER_BLOCK)
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/carbon_fiber"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/carbon_fiber"))
        .build()
        .register();

    public static final BlockEntry<TankPeriodicTableBlock>

    NITROGEN_PERIODIC_TABLE_BLOCK = REGISTRATE.block("nitrogen_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0x20FFFFFF))
        .initialProperties(HYDROGEN_PERIODIC_TABLE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register(),

    OXYGEN_PERIODIC_TABLE_BLOCK = REGISTRATE.block("oxygen_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0x20FFFFFF))
        .initialProperties(HYDROGEN_PERIODIC_TABLE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register(),

    FLUORINE_PERIODIC_TABLE_BLOCK = REGISTRATE.block("fluorine_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0x40F8F9A7))
        .initialProperties(HYDROGEN_PERIODIC_TABLE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register(),

    CHLORINE_PERIODIC_TABLE_BLOCK = REGISTRATE.block("chlorine_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0x40C0F9A7))
        .initialProperties(HYDROGEN_PERIODIC_TABLE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<PeriodicTableBlock>

    CHROMIUM_PERIODIC_TABLE_BLOCK = REGISTRATE.block("chromium_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(CHROMIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/chromium"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/chromium"))
        .build()
        .register(),

    IRON_PERIODIC_TABLE_BLOCK = REGISTRATE.block("iron_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Blocks.STORAGE_BLOCKS_IRON)
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS_IRON)
        .build()
        .register(),

    NICKEL_PERIODIC_TABLE_BLOCK = REGISTRATE.block("nickel_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(NICKEL_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/nickel"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/nickel"))
        .build()
        .register(),

    COPPER_PERIODIC_TABLE_BLOCK = REGISTRATE.block("copper_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(() -> Blocks.COPPER_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Blocks.STORAGE_BLOCKS_COPPER)
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS_COPPER)
        .build()
        .register(),

    ZINC_PERIODIC_TABLE_BLOCK = REGISTRATE.block("zinc_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(AllBlocks.ZINC_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/zinc"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/zinc"))
        .build()
        .register(),

    RHODIUM_PERIODIC_TABLE_BLOCK = REGISTRATE.block("rhodium_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(RHODIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/rhodium"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/rhodium"))
        .build()
        .register(),

    PALLADIUM_PERIODIC_TABLE_BLOCK = REGISTRATE.block("palladium_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(PALLADIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/palladium"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/palladium"))
        .build()
        .register(),

    IODINE_PERIODIC_TABLE_BLOCK = REGISTRATE.block("iodine_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(IODINE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/iodine"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/iodine"))
        .build()
        .register(),

    PLATINUM_PERIODIC_TABLE_BLOCK = REGISTRATE.block("platinum_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(CHROMIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/platinum"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/platinum"))
        .build()
        .register(),

    GOLD_PERIODIC_TABLE_BLOCK = REGISTRATE.block("gold_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(() -> Blocks.GOLD_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, Tags.Blocks.STORAGE_BLOCKS_GOLD)
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS_GOLD)
        .build()
        .register();

    public static final BlockEntry<TankPeriodicTableBlock>

    MERCURY_PERIODIC_TABLE_BLOCK = REGISTRATE.block("mercury_periodic_table_block", p -> new TankPeriodicTableBlock(p, 0xFFB3B3B3))
        .initialProperties(HYDROGEN_PERIODIC_TABLE_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .item(TankPeriodicTableBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<PeriodicTableBlock>

    LEAD_PERIODIC_TABLE_BLOCK = REGISTRATE.block("lead_periodic_table_block", PeriodicTableBlock::new)
        .initialProperties(CHROMIUM_BLOCK)
        .transform(TagGen.pickaxeOnly())
        .tag(Tags.Blocks.STORAGE_BLOCKS, AllTags.forgeBlockTag("storage_blocks/lead"))
        .item(PeriodicTableBlockItem::new)
        .tag(Tags.Items.STORAGE_BLOCKS, forgeItemTag("storage_blocks/lead"))
        .build()
        .register();

    // FOOD

    public static final BlockEntry<Block> MASHED_POTATO_BLOCK = REGISTRATE.block("mashed_potato_block", Block::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)
        .item()
        .build()
        .register();

    public static final BlockEntry<RotatedPillarBlock> RAW_FRIES_BLOCK = REGISTRATE.block("raw_fries_block", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, LootItem.lootTableItem(DestroyItems.RAW_FRIES).apply(SetItemCountFunction.setCount(ConstantValue.exactly(5f))))))
        .tag(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_HOE)
        .item()
        .build()
        .register();

    // UNCATEGORISED

    public static final BlockEntry<Block> FIBERGLASS_BLOCK = REGISTRATE.block("fiberglass_block", Block::new)
        .initialProperties(() -> Blocks.GLASS)
        .properties(p -> p
            .strength(6f)
            .mapColor(MapColor.SNOW)
        ).tag(Tags.Blocks.GLASS, BlockTags.MINEABLE_WITH_PICKAXE)
        .item()
        .tag(Tags.Items.GLASS)
        .build()
        .register();

    public static final BlockEntry<GlassBlock>
    
    BOROSILICATE_GLASS = REGISTRATE.block("borosilicate_glass", GlassBlock::new)
        .initialProperties(() -> Blocks.GLASS)
        .properties(p -> p
            .strength(2f)
        ).tag(Tags.Blocks.GLASS, Tags.Blocks.GLASS_COLORLESS, BlockTags.MINEABLE_WITH_PICKAXE)
        .onRegister(CreateRegistrate.connectedTextures(() -> new SimpleCTBehaviour(DestroySpriteShifts.BOROSILICATE_GLASS)))
        .item()
        .tag(Tags.Items.GLASS, Tags.Items.GLASS_COLORLESS)
        .build()
        .register();

    public static final BlockEntry<FlippableRotatedPillarBlock>
    
    PLYWOOD = REGISTRATE.block("plywood", FlippableRotatedPillarBlock::new)
        .properties(p -> p
            .mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(4.0f, 6.0f)
        ).tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.PLANKS)
        .item()
        .tag(ItemTags.PLANKS)
        .build()
        .register(),

    UNVARNISHED_PLYWOOD = REGISTRATE.block("unvarnished_plywood", FlippableRotatedPillarBlock::new)
        .properties(p -> p
            .mapColor(MapColor.WOOD)
            .sound(SoundType.WOOD)
            .instrument(NoteBlockInstrument.BASS)
            .strength(3.0f, 5.0f)
            .ignitedByLava()
        ).tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.PLANKS)
        .item(CombustibleBlockItem::new)
        .tag(ItemTags.PLANKS)
        .onRegister(i -> i.setBurnTime(2000))
        .build()
        .register();

    public static final BlockEntry<MoltenStainlessSteelBlock> MOLTEN_STAINLESS_STEEL = REGISTRATE.block("molten_stainless_steel", MoltenStainlessSteelBlock::new)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .lightLevel(state -> 15)
            .noLootTable()
            .dynamicShape()
        ).tag(AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .register();

    public static final BlockEntry<MoltenBorosilicateGlassBlock> MOLTEN_BOROSILICATE_GLASS = REGISTRATE.block("molten_borosilicate_glass", MoltenBorosilicateGlassBlock::new)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
            .lightLevel(state -> 15)
            .noLootTable()
            .dynamicShape()
        ).tag(AllBlockTags.MOVABLE_EMPTY_COLLIDER.tag)
        .register();

    public static final BlockEntry<FastCoolingMoltenPillarBlock>
    
    STAINLESS_STEEL_RODS = REGISTRATE.block("stainless_steel_rods_block", FastCoolingMoltenPillarBlock::new)
        .initialProperties(STAINLESS_STEEL_BLOCK)
        .properties(p -> p
            .mapColor(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? MapColor.COLOR_ORANGE : MapColor.METAL)
            .lightLevel(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? 15 : 0)
        ).tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .item()
        .build()
        .register();

    public static final BlockEntry<BorosilicateGlassFiberBlock>

    BOROSILICATE_GLASS_FIBER = REGISTRATE.block("borosilicate_glass_fiber", BorosilicateGlassFiberBlock::new)
        .initialProperties(MOLTEN_BOROSILICATE_GLASS)
        .properties(p -> p
            .mapColor(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? MapColor.COLOR_RED : MapColor.NONE)
            .lightLevel(state -> state.getValue(FastCoolingMoltenPillarBlock.MOLTEN) ? 15 : 0)
            .sound(SoundType.WOOL)
        ).tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.WOOL)
        .item()
        .tag(ItemTags.WOOL)
        .build()
        .register();

    public static final BlockEntry<Block> CORDITE_BLOCK = REGISTRATE.block("cordite_block", Block::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).tag(BlockTags.MINEABLE_WITH_SHOVEL)
        .tag(BlockTags.MINEABLE_WITH_HOE)
        .item()
        .build()
        .register();

    public static final BlockEntry<RotatedPillarBlock>

    INSULATED_STAINLESS_STEEL_BLOCK = REGISTRATE.block("insulated_stainless_steel_block", RotatedPillarBlock::new)
        .initialProperties(STAINLESS_STEEL_BLOCK)
        .onRegister(CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(DestroySpriteShifts.STAINLESS_STEEL_BLOCK)))
        .onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, DestroySpriteShifts.STAINLESS_STEEL_BLOCK,
			(s, f) -> f.getAxis() == s.getValue(RotatedPillarBlock.AXIS)))
        ).transform(TagGen.pickaxeOnly())
        .item()
        .build()
        .register(),
    
    EXTRUDED_CORDITE_BLOCK = REGISTRATE.block("extruded_cordite_block", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .mapColor(MapColor.COLOR_ORANGE)
            .sound(SoundType.SLIME_BLOCK)
            .strength(0.2f)
        ).loot((lt, b) -> lt.add(b, RegistrateBlockLootTables.createSilkTouchDispatchTable(b, LootItem.lootTableItem(DestroyItems.CORDITE).apply(SetItemCountFunction.setCount(ConstantValue.exactly(5f))))))
        .tag(BlockTags.MINEABLE_WITH_SHOVEL)
        .tag(BlockTags.MINEABLE_WITH_HOE)
        .item()
        .build()
        .register(),

    CLAY_MONOLITH = REGISTRATE.block("clay_monolith", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .tag(BlockTags.MINEABLE_WITH_SHOVEL)
        .item()
        .build()
        .register(),

    CERAMIC_MONOLITH = REGISTRATE.block("ceramic_monolith", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.TERRACOTTA)
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .item()
        .build()
        .register();

    public static NonNullConsumer<? super BlockItem> registerPrimeableBombDispenserBehaviour() {
        return item -> DispenserBlock.registerBehavior(item, ((PrimeableBombBlock<?>)item.getBlock()).new PrimeableBombDispenseBehaviour());
    };

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    };

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return false;
    };

    public static void register() {};
};