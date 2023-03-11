package com.petrolpark.destroy.config;

public class DestroySubstancesConfigs extends DestroyConfigBase {

    public final ConfigGroup babyBlue = group(0, "babyBlue", Comments.babyBlue);
    public final ConfigBool enableBabyBlue = b(true, "enableBabyBlue", Comments.enableBabyBlue);
    public final ConfigInt maxAddictionLevel = i(590, 0, "maxAddictionLevel", Comments.toDisable, Comments.maxAddictionLevel);

    public final ConfigGroup alcohol = group(0, "alcohol", Comments.alcohol);
    public final ConfigInt inebriationDuration = i(1200, 0, "inebriationDuration", Comments.inTicks, Comments.inebriationDuration);
    public final ConfigInt hangoverDuration = i(1200, 0, "hangoverDuration", Comments.inTicks, Comments.hangoverDuration);
    public final ConfigFloat drunkenSlipping = f(0.7f, 0f, 1.0f, "drunkenSlipping", Comments.drunkenSlipping);

    public final ConfigGroup chorusWine = group(0, "chorusWine", Comments.chorusWine);
    public final ConfigInt teleportTime = i(20, 1, 60, "teleportTime", Comments.inSeconds, Comments.teleportTime);
    
    @Override
    public String getName() {
        return "substances";
    };

    private static class Comments {
        static String toDisable = "[0 to disable this feature]";
        static String inTicks = "[in ticks]";
        static String inSeconds = "[inSeconds]";

        static String babyBlue = "Baby Blue";
        static String enableBabyBlue = "Allow the production of Baby Blue.";
        static String maxAddictionLevel = "Each level corresponds to an additional second of withdrawal.";

        static String alcohol = "Alcohol";
        static String inebriationDuration = "How long each additional level of inebriation adds.";
        static String hangoverDuration = "How long each level of inebriation adds to the Hangover effect.";
        static String drunkenSlipping = "How much Entities will slip when inebriated.";

        static String chorusWine = "Chorus Wine";
        static String teleportTime = "How far in the past your position will be set when drinking Chorus Wine.";
    };
}
