package com.petrolpark.destroy.config;

// Copied from com.simibubi.create.infrastructure.config.CKinetics;

import net.createmod.catnip.config.ConfigBase;

public class DestroyKineticsConfigs extends ConfigBase {
    @Override
    public String getName() {
        return "kinetics";
    }
    public final DestroyStressConfigs stressValues = nested(1, DestroyStressConfigs::new, "Fine tune the kinetic stats of individual components");
}
