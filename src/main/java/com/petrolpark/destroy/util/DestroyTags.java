package com.petrolpark.destroy.util;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DestroyTags {

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
		};

        @SuppressWarnings("deprecation") // Create does it therefore so can I
        public boolean matches(Item item) {
            return item.builtInRegistryHolder().containsTag(tag);
        };

        public static void init() {};
    };

    public static void register() {
        DestroyItemTags.init();
    };
}
