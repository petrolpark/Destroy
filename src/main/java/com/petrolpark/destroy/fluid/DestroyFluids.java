package com.petrolpark.destroy.fluid;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.fluid.MixtureFluid.MixtureFluidType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;

public class DestroyFluids {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();
    
    public static final FluidEntry<MixtureFluid> MIXTURE = REGISTRATE.virtualFluid("mixture", MixtureFluidType::new, MixtureFluid::new)
        .lang("Mixture")
        .register();

    public static void register() {}

};
