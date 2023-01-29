package com.petrolpark.destroy.util;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import static com.simibubi.create.AllTags.forgeItemTag;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.providers.ProviderType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DestroyTags {

    // Mostly all copied from Create source code

    public enum DestroyItemTags {

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
        SECONDARY_EXPLOSIVE("explosive/secondary"),
        SEISMOGRAPH,
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

        public TagKey<Item> get() {
            return this.tag;
        };

        public boolean matches(ItemStack stack) {
            return stack.is(tag);
        };

        public void add(Item... items) {
            REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag).add(items));
        };

        public void includeIn(TagKey<Item> superTag) {
			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(superTag).addTag(tag));
		};

        public void includeAll(TagKey<Item> subTag) {
			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.tag(tag).addTag(subTag));
		}
    };

    public static void register() {
        DestroyItemTags.DESTROY_INGOTS.includeIn(ItemTags.BEACON_PAYMENT_ITEMS);
        DestroyItemTags.FERTILIZER.add(Items.BONE_MEAL);
        DestroyItemTags.PAPER_PULPABLE.add(Items.PAPER, Items.SUGAR);
        DestroyItemTags.SEISMOGRAPH.includeIn(DestroyItemTags.PAPER_PULPABLE.tag);
        DestroyItemTags.SPRAY_BOTTLE.includeIn(AllItemTags.UPRIGHT_ON_BELT.tag);
        DestroyItemTags.SYRINGE.includeIn(AllItemTags.UPRIGHT_ON_BELT.tag);
        DestroyItemTags.VULCANIZER.includeAll(forgeItemTag("raw_materials/sulfur"));
    };
}
