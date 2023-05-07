package com.petrolpark.destroy.util;

import static com.simibubi.create.AllTags.forgeItemTag;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DestroyTags {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();

    // Mostly all copied from Create source code

    public enum DestroyItemTags {

        ALCOHOLIC_DRINK,
        BEETROOT_ASHES,
        CHEMICAL_PROTECTION_HEAD,
        CHEMICAL_PROTECTION_TORSO,
        CHEMICAL_PROTECTION_LEGS,
        CHEMICAL_PROTECTION_FEET,
        DESTROY_INGOTS,
        DIRTY_SILICA,
        FERTILIZER,
        HEFTY_BEETROOT,
        PAPER_PULPABLE,
        PLASTIC,
        PRILL,
        PRIMARY_EXPLOSIVE("explosive/primary"),
        SALT,
        SCHEMATICANNON_FUEL,
        SECONDARY_EXPLOSIVE("explosive/secondary"),
        SPRAY_BOTTLE,
        SYRINGE,
        VULCANIZER,
        YEAST
        ;

        public final TagKey<Item> tag;

        DestroyItemTags() {
            this(null);
        };

        DestroyItemTags(String path) {
			ResourceLocation id = Destroy.asResource(path == null ? Lang.asId(name()) : path);
			tag = ItemTags.create(id);
			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag));
		};

        @SuppressWarnings("deprecation") // Create does it therefore so can I
        public boolean matches(Item item) {
            return item.builtInRegistryHolder().containsTag(tag);
        };

        public static void init() {};
    };

    private static void generateItemTags(RegistrateTagsProvider<Item> provider) {

        provider.tag(ItemTags.BEACON_PAYMENT_ITEMS)
            .addTag(DestroyItemTags.DESTROY_INGOTS.tag);
        provider.tag(DestroyItemTags.FERTILIZER.tag)
            .add(Items.BONE_MEAL);
        provider.tag(DestroyItemTags.PAPER_PULPABLE.tag)
            .add(Items.PAPER, Items.SUGAR);
        provider.tag(DestroyItemTags.SCHEMATICANNON_FUEL.tag)
            .add(Items.GUNPOWDER)
            .addTag(DestroyItemTags.PRIMARY_EXPLOSIVE.tag)
            .addTag(DestroyItemTags.SECONDARY_EXPLOSIVE.tag);
        provider.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
            .addTag(DestroyItemTags.ALCOHOLIC_DRINK.tag)
            .addTag(DestroyItemTags.SPRAY_BOTTLE.tag)
            .addTag(DestroyItemTags.SYRINGE.tag);
        provider.tag(DestroyItemTags.VULCANIZER.tag)
            .addTag(forgeItemTag("raw_materials/sulfur"));

        for (DestroyItemTags tagEnum : DestroyItemTags.values()) {
            provider.getOrCreateRawBuilder(tagEnum.tag);
        };
    };

    public enum DestroyBlockTags {

        AFFECTED_BY_SMOG;

        public final TagKey<Block> tag;

        DestroyBlockTags() {
            this(null);
        };

        DestroyBlockTags(String path) {
			ResourceLocation id = Destroy.asResource(path == null ? Lang.asId(name()) : path);
			tag = BlockTags.create(id);
			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.tag(tag));
		};

        public static void init() {};

    };

    private static void generateBlockTags(RegistrateTagsProvider<Block> provider) {

        provider.tag(DestroyBlockTags.AFFECTED_BY_SMOG.tag)
            .addTag(BlockTags.LEAVES)
            .add(Blocks.GRASS, Blocks.GRASS_BLOCK, Blocks.TALL_GRASS, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM, Blocks.SUGAR_CANE);

        for (DestroyBlockTags tagEnum : DestroyBlockTags.values()) {
            provider.getOrCreateRawBuilder(tagEnum.tag);
        };
    };

    public static void register() {
        DestroyItemTags.init();
    };

    public static void datagen() {
        REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, DestroyTags::generateItemTags);
        REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, DestroyTags::generateBlockTags);
    };
}
