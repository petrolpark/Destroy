package com.petrolpark.destroy.world;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.worldgen.AllOreFeatureConfigEntries;
import com.simibubi.create.foundation.worldgen.OreFeatureConfigEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.ForgeConfigSpec;

public class DestroyOreFeatureConfigEntries {

    public static final OreFeatureConfigEntry NICKEL_ORE = 
        create("nickel_ore", 5, 10, -63, 40)
        .standardDatagenExt()
        .withBlocks(Couple.create(DestroyBlocks.NICKEL_ORE, DestroyBlocks.DEEPSLATE_NICKEL_ORE))
        .biomeTag(BiomeTags.IS_OVERWORLD)
        .parent();

    public static final OreFeatureConfigEntry FLUORITE_ORE = 
        create("nickel_ore", 5, 5, -63, 16)
        .standardDatagenExt()
        .withBlocks(Couple.create(DestroyBlocks.FLUORITE_ORE, DestroyBlocks.DEEPSLATE_FLUORITE_ORE))
        .biomeTag(BiomeTags.IS_OVERWORLD)
        .parent();

    public static final OreFeatureConfigEntry END_FLUORITE_ORE = 
        create("end_fluorite_ore", 5, 10, -63, 320)
        .standardDatagenExt()
        .withBlock(DestroyBlocks.END_FLUORITE_ORE)
        .biomeTag(BiomeTags.IS_END)
        .parent();

    private static OreFeatureConfigEntry create(String name, int clusterSize, float frequency,
		int minHeight, int maxHeight) {
		ResourceLocation id = Destroy.asResource(name);
		return new OreFeatureConfigEntry(id, clusterSize, frequency, minHeight, maxHeight);
	};

    public static void init() {};
};
