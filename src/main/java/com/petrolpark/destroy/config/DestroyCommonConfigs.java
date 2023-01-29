package com.petrolpark.destroy.config;

public class DestroyCommonConfigs extends DestroyConfigBase {

    public final DestroyWorldGenerationConfigs worldGen = nested(0, DestroyWorldGenerationConfigs::new, Comments.worldGen);

	@Override
	public String getName() {
		return "common";
	};

	private static class Comments {
		static String worldGen = "Modify the generation of Ores added by Destroy";
	};
}
