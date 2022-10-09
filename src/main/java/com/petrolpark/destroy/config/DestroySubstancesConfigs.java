package com.petrolpark.destroy.config;

public class DestroySubstancesConfigs extends DestroyConfigBase {

    public final ConfigGroup methamphetamine = group(1, "methamphetamine", Comments.methamphetamine);
    public final ConfigBool enableMethamphetamine = b(true, "enableMethamphetamine", Comments.enableMethamphetamine);
    public final ConfigInt maxAddictionLevel = i(590, 0, "maxAddictionLevel", Comments.maxAddictionLevel);

    public final ConfigGroup alcohol = group(1, "alcohol", Comments.alcohol);
    public final ConfigInt inebriationDuration = i(1200, 0, "inebriationDuration", Comments.inebriationDuration);
    public final ConfigFloat drunkenSlipping = f(0.7f, 0f, 1.0f, "drunkenSlipping", Comments.drunkenSlipping);
    
    @Override
    public String getName() {
        return "substances";
    };

    private static class Comments {
        static String methamphetamine = "Methamphetamine";
        static String enableMethamphetamine = "Allow the production of Methamphetamine";
        static String maxAddictionLevel = "Each level corresponds to an additional second of withdrawal (0 to disable addiction).";

        static String alcohol = "Alcohol";
        static String inebriationDuration = "How long (in ticks) each additional level of inebriation adds.";
        static String drunkenSlipping = "How much Entities will slip when inebriated.";
    };
}
