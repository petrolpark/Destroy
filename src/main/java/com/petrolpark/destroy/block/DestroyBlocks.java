package com.petrolpark.destroy.block;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.display.PollutometerDisplaySource;
import com.petrolpark.destroy.block.entity.BubbleCapBlockEntity;
import com.petrolpark.destroy.block.model.CopycatBlockModel;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.PumpjackBlockItem;
import com.petrolpark.destroy.item.creativeModeTab.DestroyCreativeModeTabs;
import com.petrolpark.destroy.sound.DestroySoundTypes;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.processing.AssemblyOperatorBlockItem;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.common.Tags;

public class DestroyBlocks {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();

    static {
        REGISTRATE.creativeModeTab(() -> DestroyCreativeModeTabs.TAB_DESTROY);
    };

    // BLOCK ENTITIES

    public static final BlockEntry<AgingBarrelBlock> AGING_BARREL = REGISTRATE.block("aging_barrel", AgingBarrelBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_BROWN)
            .noOcclusion()
        ).transform(TagGen.axeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<BubbleCapBlock> BUBBLE_CAP = REGISTRATE.block("bubble_cap", BubbleCapBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).onRegister(AllDisplayBehaviours.assignDataBehaviour(BubbleCapBlockEntity.BUBBLE_CAP_DISPLAY_SOURCE, "bubble_cap"))
        .transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.block("centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).blockstate((c,p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c,p)))
        .transform(TagGen.pickaxeOnly())
        .transform(BlockStressDefaults.setImpact(5.0))
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CoolerBlock> COOLER = REGISTRATE.block("cooler", CoolerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_GRAY)
            .noOcclusion()
            .sound(DestroySoundTypes.COOLER)
        ).transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<DynamoBlock> DYNAMO = REGISTRATE.block("dynamo", DynamoBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .properties(p -> p
            .color(MaterialColor.GOLD)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .transform(BlockStressDefaults.setImpact(6.0))
        .item(AssemblyOperatorBlockItem::new)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<PollutometerBlock> POLLUTOMETER = REGISTRATE.block("pollutometer", PollutometerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.NONE)
            .noOcclusion()
        ).onRegister(AllDisplayBehaviours.assignDataBehaviour(new PollutometerDisplaySource(), "pollutometer"))
        .transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<PumpjackBlock> PUMPJACK = REGISTRATE.block("pumpjack", PumpjackBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .transform(BlockStressDefaults.setImpact(8.0))
        .item(PumpjackBlockItem::new)
        .transform(customItemModel())
        .register();

    public static final BlockEntry<PumpjackCamBlock> PUMPJACK_CAM = REGISTRATE.block("pumpjack_cam", PumpjackCamBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .blockstate(BlockStateGen.horizontalAxisBlockProvider(false))
        .register();

    public static final BlockEntry<PumpjackStructuralBlock> PUMPJACK_STRUCTURAL = REGISTRATE.block("pumpjack_structure", PumpjackStructuralBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .blockstate((c, p) -> p.getVariantBuilder(c.get())
            .forAllStatesExcept(BlockStateGen.mapToAir(p), PumpjackStructuralBlock.FACING)
        )
        .register();

    public static final BlockEntry<SandCastleBlock> SAND_CASTLE = REGISTRATE.block("sand_castle", SandCastleBlock::new)
        .initialProperties(Material.DECORATION)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .noOcclusion()
            .noLootTable()
            .instabreak()
            .sound(SoundType.SAND)
        ).register();

    public static final BlockEntry<VatControllerBlock> VAT_CONTROLLER = REGISTRATE.block("vat_controller", VatControllerBlock::new)
        .initialProperties(SharedProperties::copperMetal)
        .properties(p -> p
        
        ).item()
        .build()
        .register();

    public static final BlockEntry<VatSideBlock> VAT_SIDE = REGISTRATE.block("vat_side", VatSideBlock::new)
        .initialProperties(SharedProperties::softMetal)
        .transform(BuilderTransformers.copycat())
        .properties(p -> p.color(MaterialColor.GLOW_LICHEN))
        .onRegister(CreateRegistrate.blockModel(() -> CopycatBlockModel::new))
        .blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
        .register();

    public static final BlockEntry<UrineCauldronBlock> URINE_CAULDRON = REGISTRATE.block("urine_cauldron", p -> new UrineCauldronBlock(p, DestroyCauldronInteractions.URINE))
        .initialProperties(() -> Blocks.WATER_CAULDRON)
        .tag(BlockTags.CAULDRONS)
        .register();

    public static final BlockEntry<BlacklightBlock> BLACKLIGHT = REGISTRATE.block("blacklight", BlacklightBlock::new)
        .initialProperties(() -> Blocks.LANTERN)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .sound(SoundType.GLASS)
        ).item()
        .build()
        .register();

    // EXPLOSIVES

    public static final BlockEntry<Block> ANFO_BLOCK = REGISTRATE.block("anfo_block", Block::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .color(MaterialColor.COLOR_PINK)
        ).item()
        .build()
        .register();

    public static final BlockEntry<Block> CORDITE_BLOCK = REGISTRATE.block("cordite_block", Block::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
        ).item()
        .build()
        .register();

    public static final BlockEntry<DynamiteBlock> DYNAMITE_BLOCK = REGISTRATE.block("dynamite_block", DynamiteBlock::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
        ).item()
        .build()
        .register();

    public static final BlockEntry<Block> NITROCELLULOSE_BLOCK = REGISTRATE.block("nitrocellulose_block", Block::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .color(MaterialColor.COLOR_LIGHT_GREEN)
        ).item()
        .build()
        .register();

    public static final BlockEntry<Block> SODIUM_PICRATE_BLOCK = REGISTRATE.block("sodium_picrate_block", Block::new)
        .initialProperties(() -> Blocks.TNT)
        .properties(p -> p
            .color(MaterialColor.COLOR_YELLOW)
        ).item()
        .build()
        .register();

    // STORAGE BLOCKS

    public static final BlockEntry<Block> FLUORITE_BLOCK = REGISTRATE.block("fluorite_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/fluorite"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block("raw_nickel_block", Block::new)
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/raw_nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> NICKEL_BLOCK = REGISTRATE.block("nickel_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> PALLADIUM_BLOCK = REGISTRATE.block("palladium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.DIRT)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/palladium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> PLATINUM_BLOCK = REGISTRATE.block("platinum_block", Block::new)
        .initialProperties(() -> Blocks.DIAMOND_BLOCK)
        .properties(p -> p
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/platinum"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> RHODIUM_BLOCK = REGISTRATE.block("rhodium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.TERRACOTTA_LIGHT_BLUE)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/rhodium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> ZIRCONIUM_BLOCK = REGISTRATE.block("zirconium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.STONE)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(TagGen.tagBlockAndItem("storage_blocks/zirconium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    // ORES

    public static final BlockEntry<Block> FLUORITE_ORE = REGISTRATE.block("fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_FLUORITE_ORE = REGISTRATE.block("deepslate_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> END_FLUORITE_ORE = REGISTRATE.block("end_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.END_STONE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/end_stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block("nickel_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/nickel", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block("deepslate_nickel_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ).transform(TagGen.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(TagGen.tagBlockAndItem("ores/fluorite", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    // CROPS

    public static final BlockEntry<YeastMushroomBlock>

    YEAST_MUSHROOM = REGISTRATE.block("yeast_mushroom", YeastMushroomBlock::new)
        .initialProperties(() -> Blocks.BROWN_MUSHROOM)
        .item()
        .build()
        .register();

    public static final BlockEntry<FullyGrownCropBlock>
    
    BIFURICATED_CARROTS = REGISTRATE.block("bifuricated_carrots", p -> new FullyGrownCropBlock(p, DestroyItems.BIFURICATED_CARROT))
        .initialProperties(() -> Blocks.CARROTS)
        .register(),

    GOLDEN_CARROTS = REGISTRATE.block("golden_carrots", p -> new FullyGrownCropBlock(p, () -> Items.GOLDEN_CARROT))
        .initialProperties(() -> Blocks.CARROTS)
        .register(),

    POTATE_OS = REGISTRATE.block("potate_os", p -> new FullyGrownCropBlock(p, DestroyItems.POTATE_O))
        .initialProperties(() -> Blocks.CARROTS)
        .register();

    public static final BlockEntry<HeftyBeetrootBlock> 

    HEFTY_BEETROOT = REGISTRATE.block("hefty_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.HEFTY_BEETROOT))
        .initialProperties(() -> Blocks.BEETROOTS)
        .register(),

    COAL_INFUSED_BEETROOT = REGISTRATE.block("coal_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.COAL_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    COPPER_INFUSED_BEETROOT = REGISTRATE.block("copper_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.COPPER_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    DIAMOND_INFUSED_BEETROOT = REGISTRATE.block("diamond_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.DIAMOND_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    EMERALD_INFUSED_BEETROOT = REGISTRATE.block("emerald_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.EMERALD_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    FLUORITE_INFUSED_BEETROOT = REGISTRATE.block("fluorite_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.FLUORITE_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    GOLD_INFUSED_BEETROOT = REGISTRATE.block("gold_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.GOLD_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    IRON_INFUSED_BEETROOT = REGISTRATE.block("iron_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.IRON_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    LAPIS_INFUSED_BEETROOT = REGISTRATE.block("lapis_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.LAPIS_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    NICKEL_INFUSED_BEETROOT = REGISTRATE.block("nickel_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.NICKEL_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    REDSTONE_INFUSED_BEETROOT = REGISTRATE.block("redstone_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.REDSTONE_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register(),

    ZINC_INFUSED_BEETROOT = REGISTRATE.block("zinc_infused_beetroot", p -> new HeftyBeetrootBlock(p, DestroyItems.ZINC_INFUSED_BEETROOT))
        .initialProperties(HEFTY_BEETROOT)
        .register();

    // FOOD

    public static final BlockEntry<Block> MASHED_POTATO_BLOCK = REGISTRATE.block("mashed_potato_block", Block::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .color(MaterialColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
        ).item()
        .build()
        .register();

    public static final BlockEntry<RotatedPillarBlock> RAW_FRIES_BLOCK = REGISTRATE.block("raw_fries_block", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .color(MaterialColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
        ).blockstate((c, p) -> p.axisBlock(c.get(), p.modLoc("block/raw_fries_block_side"), p.modLoc("block/raw_fries_block_end")))
        .register();

    // static {
    //     REGISTRATE.startSection(AllSections.UNASSIGNED);
    // };

    // UNCATEGORISED

    public static final BlockEntry<HalfTransparentBlock> AGAR_BLOCK = REGISTRATE.block("agar_block", HalfTransparentBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .color(MaterialColor.COLOR_LIGHT_BLUE)
            .friction(1.1f)
            .noOcclusion()
            .sound(SoundType.SLIME_BLOCK)
        ).transform(TagGen.tagBlockAndItem("storage_blocks/fluorite"))
        .build()
        .register();

    public static final BlockEntry<HalfTransparentBlock> YEAST_COVERED_AGAR_BLOCK = REGISTRATE.block("yeast_covered_agar_block", HalfTransparentBlock::new)
        .initialProperties(AGAR_BLOCK)
        .item()
        .build()
        .register();

    public static final Material PUMPJACK_MATERIAL = new Material(MaterialColor.COLOR_GRAY, false, true, true, false, false, false, PushReaction.BLOCK);

    public static void register() {};
}