package com.petrolpark.destroy.config;

public class DestroyServerConfigs extends DestroyConfigBase {

    public final ConfigGroup infrastructure = group(0, "infrastructure", Comments.infrastructure);
    public final DestroyContraptionsConfigs contraptions = nested(0, DestroyContraptionsConfigs::new, Comments.contraptions);
    public final DestroySubstancesConfigs substances = nested(0, DestroySubstancesConfigs::new, Comments.substances);
    
    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
        static String infrastructure = "Behind the magic";
        static String contraptions = "Destroy's processing machines";
        static String substances = "Destroy's drugs and medicines";
    };
}
