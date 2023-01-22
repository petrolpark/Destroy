package com.petrolpark.destroy.block;

import java.util.function.Supplier;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.DestroyCreativeModeTabs;
import com.petrolpark.destroy.item.DestroyItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyBlocks {
    
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Destroy.MOD_ID);

    private static final CreateRegistrate REGISTRATE = Destroy.registrate().creativeModeTab(() -> DestroyCreativeModeTabs.TAB_DESTROY);

    // CONTRAPTIONS - this stuff is registered with Registrate

    public static final BlockEntry<AgingBarrelBlock> AGING_BARREL = REGISTRATE.block("aging_barrel", AgingBarrelBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_BROWN)
            .noOcclusion()
        ).item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<BreezeBurnerBlock> BREEZE_BURNER = REGISTRATE.block("breeze_burner", BreezeBurnerBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.DEEPSLATE)
            .noOcclusion()
        ).item()
        .transform(customItemModel())
        .register();

    public static final BlockEntry<CentrifugeBlock> CENTRIFUGE = REGISTRATE.block("centrifuge", CentrifugeBlock::new)
        .initialProperties(SharedProperties::stone)
        .properties(p -> p
            .color(MaterialColor.COLOR_ORANGE)
            .noOcclusion()
        ).blockstate((c,p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c,p)))
        .transform(BlockStressDefaults.setImpact(8.0))
        .item()
        .transform(customItemModel())
        .register();

    // Everything below is not registered with Registrate (yet)
    //TODO register with Registrate

    // STORAGE BLOCKS

    public static final RegistryObject<Block> FLUORITE_BLOCK = registerBlock("fluorite_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.COLOR_PURPLE)
            .strength(4.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> RAW_NICKEL_BLOCK = registerBlock("raw_nickel_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.STONE, MaterialColor.SAND)
            .strength(4.5f)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> NICKEL_BLOCK = registerBlock("nickel_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.SAND)
            .strength(5.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> PALLADIUM_BLOCK = registerBlock("palladium_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.METAL)
            .strength(5.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> PLATINUM_BLOCK = registerBlock("platinum_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE)
            .strength(5.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> RHODIUM_BLOCK = registerBlock("rhodium_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.COLOR_PURPLE)
            .strength(5.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> ZIRCONIUM_BLOCK = registerBlock("zirconium_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.METAL, MaterialColor.METAL)
            .strength(5.0f)
            .sound(SoundType.METAL)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    // ORES

    public static final RegistryObject<Block> FLUORITE_ORE = registerBlock("fluorite_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties
            .of(Material.STONE)
            .strength(3.0f)
            .requiresCorrectToolForDrops()
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> DEEPSLATE_FLUORITE_ORE = registerBlock("deepslate_fluorite_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties
            .of(Material.STONE, MaterialColor.DEEPSLATE)
            .strength(4.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> NICKEL_ORE = registerBlock("nickel_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties
            .of(Material.STONE)
            .strength(3.0f)
            .requiresCorrectToolForDrops()
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> DEEPSLATE_NICKEL_ORE = registerBlock("deepslate_nickel_ore",
        () -> new DropExperienceBlock(BlockBehaviour.Properties
            .of(Material.STONE, MaterialColor.DEEPSLATE)
            .strength(4.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    // FOOD

    public static final RegistryObject<Block> MASHED_POTATO_BLOCK = registerBlock("mashed_potato_block",
        () -> new Block(BlockBehaviour.Properties
            .of(Material.CLAY, MaterialColor.COLOR_YELLOW)
            .strength(0.5f)
            .sound(SoundType.SLIME_BLOCK)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> RAW_FRIES_BLOCK = registerBlock("raw_fries_block",
        () -> new RotatedPillarBlock(BlockBehaviour.Properties
            .of(Material.CLAY, MaterialColor.COLOR_YELLOW)
            .strength(0.5f)
            .sound(SoundType.SLIME_BLOCK)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    // UNCATEGORISED

    public static final RegistryObject<Block> AGAR_BLOCK = registerBlock("agar_block",
        () -> new HalfTransparentBlock(BlockBehaviour.Properties
            .of(Material.CLAY, MaterialColor.COLOR_LIGHT_BLUE)
            .friction(0.9F)
            .noOcclusion()
            .sound(SoundType.SLIME_BLOCK)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );

    public static final RegistryObject<Block> YEAST_COVERED_AGAR_BLOCK = registerBlock("yeast_covered_agar_block",
        () -> new HalfTransparentBlock(BlockBehaviour.Properties
            .of(Material.CLAY, MaterialColor.COLOR_LIGHT_BLUE)
            .friction(0.9F)
            .noOcclusion()
            .sound(SoundType.SLIME_BLOCK)
        ), DestroyCreativeModeTabs.TAB_DESTROY
    );


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return DestroyItems.ITEMS.register(name, () -> new BlockItem(block.get(),
            new Item.Properties().tab(tab)));
    };

    public static void register(IEventBus eventBus) {
        //register the usual stuff
        BLOCKS.register(eventBus);
        //register the Registrate stuff
        Create.registrate().addToSection(CENTRIFUGE, AllSections.KINETICS);
    }
}