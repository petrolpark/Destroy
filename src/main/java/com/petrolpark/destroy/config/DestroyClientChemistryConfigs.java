package com.petrolpark.destroy.config;

public class DestroyClientChemistryConfigs extends DestroyConfigBase {

    public final ConfigBool iupacNames = b(false, "iupacNames", Comments.iupacNames, Comments.reloadRequired);

    @Override
    public String getName() {
        return "clientChemistry";
    };

    private static class Comments {
        static String
        iupacNames = "Show IUPAC systematic names rather than common names",
        reloadRequired = "[Reload required to take effect]";
    };
}
