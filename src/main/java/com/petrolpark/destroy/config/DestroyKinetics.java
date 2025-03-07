package com.petrolpark.destroy.config;

// Copied from com.simibubi.create.infrastructure.config.CKinetics;

import net.createmod.catnip.config.ConfigBase;

public class DestroyKinetics extends ConfigBase {
    @Override
    public String getName() {
        return "kinetics";
    }
    public final DestroyStress stressValues = nested(1, DestroyStress::new, "Fine tune the kinetic stats of individual components");
}
