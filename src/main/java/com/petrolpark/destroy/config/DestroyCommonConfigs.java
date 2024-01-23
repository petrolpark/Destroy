package com.petrolpark.destroy.config;

public class DestroyCommonConfigs extends DestroyConfigBase {

	public final DestroySubstancesConfigs substances = nested(0, DestroySubstancesConfigs::new, Comments.substances);

	@Override
	public String getName() {
		return "common";
	};

	private static class Comments {
		static String

		substances = "Destroy's drugs and medicines";
	};
}
