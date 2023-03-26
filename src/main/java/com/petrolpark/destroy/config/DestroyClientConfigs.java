package com.petrolpark.destroy.config;

public class DestroyClientConfigs extends DestroyConfigBase {

    public final ConfigGroup ahhh = group(0, "ahhh", "Pee");
    public final DestroyClientChemistryConfigs chemistry = nested(0, DestroyClientChemistryConfigs::new, Comments.chemistry);
    
    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
        static String chemistry = "Many many molecules";
    };
}
