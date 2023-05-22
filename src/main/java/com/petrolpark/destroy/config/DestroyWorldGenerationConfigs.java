package com.petrolpark.destroy.config;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.infrastructure.worldgen.AllOreFeatureConfigEntries;

import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class DestroyWorldGenerationConfigs extends DestroyConfigBase {
    
	/**
	 * Increment this number if all worldgen config entries should be overwritten
	 * in this update. Worlds from the previous version will overwrite potentially
	 * changed values with the new defaults.
     * (This was in the Create source code so it's probably a good idea to put it here).
	 */
	public static final int FORCED_UPDATE_VERSION = 0;

	public final ConfigBool disable = b(false, "disableWorldGen", Comments.disable);

	@Override
	public void registerAll(Builder builder) {
		super.registerAll(builder);
		AllOreFeatureConfigEntries.fillConfig(builder, Destroy.MOD_ID);
	};

	@Override
	public String getName() {
		return "worldGen";
	};

	private static class Comments {
		static String disable = "Prevents Destroy Ores from generating";
	};
};
