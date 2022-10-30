package com.petrolpark.destroy.config;

import com.simibubi.create.foundation.config.ConfigBase;

import net.minecraftforge.common.ForgeConfigSpec;

public class DestroyConfigBase extends ConfigBase {

    //copied from Create source code
    protected void registerAll(ForgeConfigSpec.Builder builder) {
        for (CValue<?, ?> cValue : allValues)
			cValue.register(builder);
    };

    @Override
    public String getName() {
        return "thisnameshouldnotexist";
    };
}
