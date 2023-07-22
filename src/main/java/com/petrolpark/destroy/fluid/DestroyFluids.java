package com.petrolpark.destroy.fluid;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.fluid.MixtureFluid.MixtureFluidType;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.resources.ResourceLocation;

public class DestroyFluids {
    
    public static final FluidEntry<MixtureFluid> MIXTURE = REGISTRATE.virtualFluid("mixture",
        new ResourceLocation("destroy", "fluid/mixture_still"),
        new ResourceLocation("destroy", "fluid/mixture_flow"),
        MixtureFluidType::new,
        MixtureFluid::new
    )
        .lang("Mixture")
        .register();

    public static final FluidEntry<VirtualFluid>
    
    URINE = virtualFluid("urine")
        .register(),
    CHORUS_WINE = virtualFluid("chorus_wine")
        .register(),
    CREAM = virtualFluid("cream")
        .register(),
    CRUDE_OIL = virtualFluid("crude_oil")
        .register(),
    DIESEL = virtualFluid("diesel")
        .register(),
    FUEL_OIL = virtualFluid("fuel_oil")
        .register(),
    KEROSENE = virtualFluid("kerosene")
        .register(),
    LPG = virtualFluid("lpg")
        .register(),
    MOLTEN_CINNABAR = virtualFluid("molten_cinnabar")
        .register(),
    NAPALM_SUNDAE = virtualFluid("napalm_sundae")
        .register(),
    NAPTHA = virtualFluid("naptha")
        .register(),
    ONCE_DISTILLED_MOONSHINE = coloredWaterFluid("once_distilled_moonshine", 0xE0684F31)
        .register(),
    PETROL = virtualFluid("petrol")
        .register(),
    SKIMMED_MILK = coloredWaterFluid("skimmed_milk", 0xE0FFFFFF)
        .register(),
    THRICE_DISTILLED_MOONSHINE = coloredWaterFluid("thrice_distilled_moonshine", 0xC0A18666)
        .register(),
    TWICE_DISTILLED_MOONSHINE = coloredWaterFluid("twice_distilled_moonshine", 0xD08C6B46)
        .register(),
    UNDISTILLED_MOONSHINE = coloredWaterFluid("undistilled_moonshine", 0xF053330D)
        .register();

    private static FluidBuilder<VirtualFluid, CreateRegistrate> virtualFluid(String name) {
        return REGISTRATE.virtualFluid(name, Destroy.asResource("fluid/"+name), Destroy.asResource("fluid/"+name));
    };

    private static FluidBuilder<VirtualFluid, CreateRegistrate> coloredWaterFluid(String name, int color) {
        return REGISTRATE.virtualFluid(name, (properties, stillTexture, flowingTexture) -> new ColoredWaterFluidType(properties, color), VirtualFluid::new);
    };

    public static void register() {}

};
