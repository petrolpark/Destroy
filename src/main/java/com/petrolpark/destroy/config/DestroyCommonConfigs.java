package com.petrolpark.destroy.config;

public class DestroyCommonConfigs extends DestroyConfigBase {

    public final DestroyWorldGenerationConfigs worldGen = nested(0, DestroyWorldGenerationConfigs::new, Comments.worldGen);
	public final DestroySubstancesConfigs substances = nested(0, DestroySubstancesConfigs::new, Comments.substances);
	public final DestroyPollutionConfigs pollution = nested(0, DestroyPollutionConfigs::new, Comments.pollution);

	@Override
	public String getName() {
		return "common";
	};

	private static class Comments {
		static String

		worldGen = "Modify the generation of Ores added by Destroy",
		substances = "Destroy's drugs and medicines",
		pollution = "Change the effects of pollution on the world";

	};
}
