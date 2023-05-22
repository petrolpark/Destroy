package com.petrolpark.destroy.world.worldgen;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.ConfigDrivenOreFeatureConfiguration;
import com.simibubi.create.infrastructure.worldgen.OreFeatureConfigEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class EndOreFeatureConfigEntry extends OreFeatureConfigEntry {

    public static final RuleTest END_STONE = new BlockMatchTest(Blocks.END_STONE);

    private DatagenExtension datagenExt;

    public EndOreFeatureConfigEntry(ResourceLocation id, int clusterSize, float frequency, int minHeight, int maxHeight) {
        super(id, clusterSize, frequency, minHeight, maxHeight);
        datagenExt = new EndDatagenExtension();
    };

    @Override
    public DatagenExtension datagenExt() {
        return datagenExt;
    };

	public EndDatagenExtension endDatagenExt() {
		if (datagenExt == null) {
			datagenExt = new StandardDatagenExtension();
		}
		if (datagenExt instanceof EndDatagenExtension end) {
			return end;
		}
		return null;
	}

    public class EndDatagenExtension extends DatagenExtension {

        public NonNullSupplier<? extends Block> endBlock;

        public EndDatagenExtension withEndBlock(NonNullSupplier<? extends Block> block) {
			this.endBlock = block;
			return this;
		};

        @Override
        public EndDatagenExtension biomeTag(TagKey<Biome> biomes) {
            super.biomeTag(biomes);
            return this;
        };

        @Override
        public ConfiguredFeature<?, ?> createConfiguredFeature(RegistryAccess registryAccess) {
            List<TargetBlockState> targetStates = new ArrayList<>();
            if (endBlock != null) {
                targetStates.add(OreConfiguration.target(END_STONE, endBlock.get().defaultBlockState()));
            };
            ConfigDrivenOreFeatureConfiguration config = new ConfigDrivenOreFeatureConfiguration(EndOreFeatureConfigEntry.this, 0, targetStates);
			return new ConfiguredFeature<>(AllFeatures.STANDARD_ORE.get(), config);
        }

    };
    
};
