package com.petrolpark.destroy.config;

public class DestroyClientConfigs extends DestroyConfigBase {

    public final ConfigGroup ahhh = group(0, "ahhh", "Pee");
    
    public final ConfigGroup pee = group(1, "pee", Comments.pee);
    public final ConfigFloat tempX = f(0.1f, 0f, "tempX", "Temp X");
    public final ConfigFloat tempY = f(0.5f, 0f, "tempY", "Temp Y");
    public final ConfigFloat tempZ = f(0.1f, 0f, "tempZ", "Temp Z");
    
    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
        static String pee = "pee";
    };
}
