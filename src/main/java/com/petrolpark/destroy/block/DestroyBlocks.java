package com.petrolpark.destroy.block;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyCreativeModeTabs;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
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
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraftforge.common.Tags;

public class DestroyBlocks {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();

    static {
        REGISTRATE.creativeModeTab(() -> DestroyCreativeModeTabs.TAB_DESTROY);
    };

    static {
        REGISTRATE.startSection(AllSections.KINETICS);
    };

    // CONTRAPTIONS

    // public static final BlockEntry<BreezeBurnerBlock> BREEZE_BURNER = REGISTRATE.block("breeze_burner", BreezeBurnerBlock::new)
    //     .initialProperties(SharedProperties::stone)
    //     .properties(p -> p
    //         .color(MaterialColor.DEEPSLATE)
    //         .noOcclusion()
    //     ).transform(TagGen.pickaxeOnly())
    //     .item()
    //     .transform(customItemModel())
    //     .register();

    public static final BlockEntry<BubbleCapBlock> BUBBLE_CAP = REGISTRATE.block("bubble_cap", BubbleCapBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).transform(TagGen.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.block("centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).blockstate((c,p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c,p)))
        .transform(TagGen.pickaxeOnly())
        .transform(BlockStressDefaults.setImpact(8.0))
        .item()
        .transform(customItemModel())
        .register();

    static {
        REGISTRATE.startSection(AllSections.CURIOSITIES);
    };

    public static final BlockEntry<AgingBarrelBlock> AGING_BARREL = REGISTRATE.block("aging_barrel", AgingBarrelBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_BROWN)
            .noOcclusion()
        ).transform(TagGen.axeOnly())
        .item()
        .transform(customItemModel())
        .register();

    static {
        REGISTRATE.startSection(AllSections.MATERIALS);
    };

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
        ).register();

    public static final BlockEntry<RotatedPillarBlock> RAW_FRIES_BLOCK = REGISTRATE.block("raw_fries_block", RotatedPillarBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .color(MaterialColor.COLOR_YELLOW)
            .sound(SoundType.SLIME_BLOCK)
        ).blockstate((c, p) -> p.axisBlock(c.get(), p.modLoc("block/raw_fries_block_side"), p.modLoc("block/raw_fries_block_end")))
        .register();

    static {
        REGISTRATE.startSection(AllSections.UNASSIGNED);
    };

    // UNCATEGORISED

    public static final BlockEntry<HalfTransparentBlock> AGAR_BLOCK = REGISTRATE.block("agar_block", HalfTransparentBlock::new)
        .initialProperties(() -> Blocks.CLAY)
        .properties(p -> p
            .color(MaterialColor.COLOR_LIGHT_BLUE)
            .friction(1.1f)
            .noOcclusion()
            .sound(SoundType.SLIME_BLOCK)
        ).item()
        .build()
        .register();

    public static final BlockEntry<HalfTransparentBlock> YEAST_COVERED_AGAR_BLOCK = REGISTRATE.block("yeast_covered_agar_block", HalfTransparentBlock::new)
        .initialProperties(AGAR_BLOCK)
        .item()
        .build()
        .register();

    public static void register() {};
}