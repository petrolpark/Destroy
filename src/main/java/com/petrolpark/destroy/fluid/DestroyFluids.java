package com.petrolpark.destroy.fluid;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.fluid.MixtureFluid.MixtureFluidType;
import com.petrolpark.destroy.fluid.UrineFluid.UrineFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.resources.ResourceLocation;

public class DestroyFluids {

    private static CreateRegistrate REGISTRATE = Destroy.registrate();
    
    public static final FluidEntry<MixtureFluid> MIXTURE = REGISTRATE.virtualFluid("mixture", new ResourceLocation("minecraft", "block/water_still"), new ResourceLocation("minecraft", "block/water_flow"), MixtureFluidType::new, MixtureFluid::new)
        .lang("Mixture")
        .register();

    public static final FluidEntry<UrineFluid> URINE = REGISTRATE.virtualFluid("urine", new ResourceLocation("minecraft", "block/water_still"), new ResourceLocation("minecraft", "block/water_flow"), UrineFluidType::new, UrineFluid::new)
        .register();

    public static final FluidEntry<VirtualFluid>
    
    CHORUS_WINE = REGISTRATE.virtualFluid("chorus_wine")
        .register(),
    CREAM = virtualFluid("cream")
        .register(),
    CRUDE_OIL = virtualFluid("crude_oil")
        .register(),
    DIESEL = REGISTRATE.virtualFluid("diesel")
        .register(),
    FUEL_OIL = REGISTRATE.virtualFluid("fuel_oil")
        .register(),
    KEROSENE = REGISTRATE.virtualFluid("kerosene")
        .register(),
    LPG = REGISTRATE.virtualFluid("lpg")
        .register(),
    MOLTEN_CINNABAR = REGISTRATE.virtualFluid("molten_cinnabar")
        .register(),
    NAPALM_SUNDAE = REGISTRATE.virtualFluid("napalm_sundae")
        .register(),
    NAPTHA = REGISTRATE.virtualFluid("naptha")
        .register(),
    ONCE_DISTILLED_MOONSHINE = REGISTRATE.virtualFluid("once_distilled_moonshine")
        .register(),
    PETROL = REGISTRATE.virtualFluid("petrol")
        .register(),
    SKIMMED_MILK = REGISTRATE.virtualFluid("skimmed_milk")
        .register(),
    THRICE_DISTILLED_MOONSHINE = REGISTRATE.virtualFluid("thrice_distilled_moonshine")
        .register(),
    TWICE_DISTILLED_MOONSHINE = REGISTRATE.virtualFluid("twice_distilled_moonshine")
        .register(),
    UNDISTILLED_MOONSHINE = REGISTRATE.virtualFluid("undistilled_moonshine")
        .register();

    private static FluidBuilder<VirtualFluid, CreateRegistrate> virtualFluid(String name) {
        return REGISTRATE.virtualFluid(name, Destroy.asResource("fluid/"+name), Destroy.asResource("fluid/"+name));
    };

    public static void register() {}

};
