package com.petrolpark.destroy.config;

public class DestroyClientConfigs extends DestroyConfigBase {

    public final DestroyClientChemistryConfigs chemistry = nested(0, DestroyClientChemistryConfigs::new, Comments.chemistry);
    public final DestroyMenuButtonConfig configurationButtons = nested(0, DestroyMenuButtonConfig::new, Comments.configurationButtons);
    
    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
        static String
        chemistry = "Many many molecules",
        configurationButtons = "The buttons to open Destroy's configurations which appear on the main menu and pause menu";
    };
}
