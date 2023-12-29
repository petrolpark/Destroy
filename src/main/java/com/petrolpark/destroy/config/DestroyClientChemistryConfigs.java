package com.petrolpark.destroy.config;

import com.petrolpark.destroy.util.DestroyLang.TemperatureUnit;

public class DestroyClientChemistryConfigs extends DestroyConfigBase {

    public final ConfigBool iupacNames = b(false, "iupacNames", Comments.iupacNames, Comments.reloadRequired);
    public final ConfigEnum<TemperatureUnit> temperatureUnit = e(TemperatureUnit.DEGREES_CELCIUS, "temperatureUnit", Comments.temperatureUnit, Comments.reloadRequired);
    public final ConfigBool nerdMode = b(false, "nerdMode", Comments.nerdMode);

    @Override
    public String getName() {
        return "clientChemistry";
    };

    private static class Comments {
        static String
        iupacNames = "Show IUPAC systematic names rather than common names",
        temperatureUnit = "Units of temperature to display by default",
        nerdMode = "Display additional technical details in some tooltips",
        reloadRequired = "[Reload may be required to take full effect]";
    };
}
