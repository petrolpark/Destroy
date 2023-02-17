package com.petrolpark.destroy.world;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.worldgen.OreFeatureConfigEntry;

import net.minecraft.tags.BiomeTags;

public class DestroyOreFeatureConfigEntries {

    public static final OreFeatureConfigEntry NICKEL_ORE = 
        createOreFeature("nickel_ore", 5, 10, -63, 16)
        .standardDatagenExt()
        .withBlocks(Couple.create(DestroyBlocks.NICKEL_ORE, DestroyBlocks.DEEPSLATE_NICKEL_ORE))
        .biomeTag(BiomeTags.IS_OVERWORLD)
        .parent();

    public static final OreFeatureConfigEntry FLUORITE_ORE = 
        createOreFeature("fluorite_ore", 5, 5, -63, 16)
        .standardDatagenExt()
        .withBlocks(Couple.create(DestroyBlocks.FLUORITE_ORE, DestroyBlocks.DEEPSLATE_FLUORITE_ORE))
        .biomeTag(BiomeTags.IS_OVERWORLD)
        .parent();

    public static final OreFeatureConfigEntry END_FLUORITE_ORE = 
        createEndOreFeature("end_fluorite_ore", 7, 60, -63, 320)
        .endDatagenExt()
        .withEndBlock(DestroyBlocks.END_FLUORITE_ORE)
        .biomeTag(BiomeTags.IS_END)
        .parent();

    private static OreFeatureConfigEntry createOreFeature(String name, int clusterSize, float frequency, int minHeight, int maxHeight) {
		return new OreFeatureConfigEntry(Destroy.asResource(name), clusterSize, frequency, minHeight, maxHeight);
	};

    private static EndOreFeatureConfigEntry createEndOreFeature(String name, int clusterSize, float frequency, int minHeight, int maxHeight) {
        return new EndOreFeatureConfigEntry(Destroy.asResource(name), clusterSize, frequency, minHeight, maxHeight);
    };

    public static void init() {};

};
