package com.petrolpark.destroy.config;

import com.simibubi.create.foundation.config.ConfigBase;

import net.minecraftforge.common.ForgeConfigSpec;

public class DestroyConfigBase extends ConfigBase {

    public void callRegisterAll(final ForgeConfigSpec.Builder builder) {
        registerAll(builder);
    };

    @Override
    public String getName() {
        return "thisnameshouldnotexist";
    };
}
