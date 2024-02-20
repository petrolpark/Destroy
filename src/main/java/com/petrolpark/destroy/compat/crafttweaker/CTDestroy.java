package com.petrolpark.destroy.compat.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import org.apache.logging.log4j.Logger;

public class CTDestroy {
    private static final Logger logger = CraftTweakerAPI.getLogger("Destroy");

    public static Logger getLogger() {
        return logger;
    }
}
