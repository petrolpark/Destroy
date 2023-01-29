package com.petrolpark.destroy.fluid;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.fluid.MixtureFluid.MixtureFluidType;
import com.tterrag.registrate.util.entry.FluidEntry;

public class DestroyFluids {
    
    public static final FluidEntry<MixtureFluid> MIXTURE = REGISTRATE.virtualFluid("mixture", MixtureFluidType::new, MixtureFluid::new)
        .lang("Mixture")
        .register();

    public static void register() {};
}
