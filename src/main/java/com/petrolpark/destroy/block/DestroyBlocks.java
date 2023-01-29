package com.petrolpark.destroy.block;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.item.DestroyCreativeModeTabs;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AllLangPartials;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.tags.BlockTags;
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

    static {
        REGISTRATE.creativeModeTab(() -> DestroyCreativeModeTabs.TAB_DESTROY);
    };

    static {
        REGISTRATE.startSection(AllSections.KINETICS);
    };

    // CONTRAPTIONS

    public static final BlockEntry<BreezeBurnerBlock> BREEZE_BURNER = REGISTRATE.block("breeze_burner", BreezeBurnerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.DEEPSLATE)
            .noOcclusion()
        ).transform(AllTags.pickaxeOnly())
        .item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.block("centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).blockstate((c,p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c,p)))
        .transform(AllTags.pickaxeOnly())
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
        ).transform(AllTags.axeOnly())
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
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/fluorite"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> RAW_NICKEL_BLOCK = REGISTRATE.block("raw_nickel_block", Block::new)
        .initialProperties(() -> Blocks.RAW_IRON_BLOCK)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/raw_nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> NICKEL_BLOCK = REGISTRATE.block("nickel_block", Block::new)
        .initialProperties(() -> Blocks.IRON_BLOCK)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_STONE_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/nickel"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> PALLADIUM_BLOCK = REGISTRATE.block("palladium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.DIRT)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/palladium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> PLATINUM_BLOCK = REGISTRATE.block("platinum_block", Block::new)
        .initialProperties(() -> Blocks.DIAMOND_BLOCK)
        .properties(p -> p
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/platinum"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> RHODIUM_BLOCK = REGISTRATE.block("rhodium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.TERRACOTTA_LIGHT_BLUE)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/rhodium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    public static final BlockEntry<Block> ZIRCONIUM_BLOCK = REGISTRATE.block("zirconium_block", Block::new)
        .initialProperties(() -> Blocks.NETHERITE_BLOCK)
        .properties(p -> p
            .color(MaterialColor.STONE)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .tag(BlockTags.NEEDS_DIAMOND_TOOL)
        .tag(Tags.Blocks.STORAGE_BLOCKS)
        .tag(BlockTags.BEACON_BASE_BLOCKS)
        .transform(AllTags.tagBlockAndItem("storage_blocks/zirconium"))
        .tag(Tags.Items.STORAGE_BLOCKS)
        .build()
        .register();

    // ORES

    public static final BlockEntry<Block> FLUORITE_ORE = REGISTRATE.block("fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(AllTags.tagBlockAndItem("ores/fluorite", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_FLUORITE_ORE = REGISTRATE.block("deepslate_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ).transform(AllTags.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(AllTags.tagBlockAndItem("ores/fluorite", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> END_FLUORITE_ORE = REGISTRATE.block("end_fluorite_ore", Block::new)
        .initialProperties(() -> Blocks.END_STONE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.FLUORITE.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(AllTags.tagBlockAndItem("ores/fluorite", "ores_in_ground/end_stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> NICKEL_ORE = REGISTRATE.block("nickel_ore", Block::new)
        .initialProperties(() -> Blocks.GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.SAND)
            .requiresCorrectToolForDrops()
        ).transform(AllTags.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(AllTags.tagBlockAndItem("ores/nickel", "ores_in_ground/stone"))
        .tag(Tags.Items.ORES)
        .build()
        .register();

    public static final BlockEntry<Block> DEEPSLATE_NICKEL_ORE = REGISTRATE.block("deepslate_nickel_ore", Block::new)
        .initialProperties(() -> Blocks.DEEPSLATE_GOLD_ORE)
        .properties(p -> p
            .color(MaterialColor.COLOR_PURPLE)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ).transform(AllTags.pickaxeOnly())
        .loot((lt, b) -> lt.add(b,
			RegistrateBlockLootTables.createSilkTouchDispatchTable(b,
				RegistrateBlockLootTables.applyExplosionDecay(b, LootItem.lootTableItem(DestroyItems.RAW_NICKEL.get())
				.apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
            )
        )).tag(BlockTags.NEEDS_IRON_TOOL)
        .tag(Tags.Blocks.ORES)
        .transform(AllTags.tagBlockAndItem("ores/fluorite", "ores_in_ground/deepslate"))
        .tag(Tags.Items.ORES)
        .build()
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
        ).register();

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