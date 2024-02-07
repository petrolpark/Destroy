package com.petrolpark.destroy.fluid;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.fluid.MixtureFluid.MixtureFluidType;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.VirtualFluid;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class DestroyFluids {

    public static FluidStack air(int amount, float temperature) {
        return MixtureFluid.of(amount, MixtureFluid.airMixture(temperature), "fluid.destroy.air");
    };
    
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
    CHORUS_WINE = REGISTRATE.virtualFluid("chorus_wine", new ResourceLocation("destroy", "fluid/swirling"), new ResourceLocation("destroy", "fluid/swirling"), (properties, stillTexture, flowingTexture) -> new ColoredFluidType(properties, stillTexture, flowingTexture, 0x808000c0), VirtualFluid::new)
        .register(),
    CREAM = virtualFluid("cream")
        .register(),
    CRUDE_OIL = virtualFluid("crude_oil")
        .tag(AllTags.forgeFluidTag("crude_oil"))
        .bucket()
        .tag(AllTags.forgeItemTag("buckets/crude_oil"))
        .build()
        .register(),
    MOLTEN_CINNABAR = virtualFluid("molten_cinnabar")
        .register(),
    NAPALM_SUNDAE = virtualFluid("napalm_sundae")
        .register(),
    ONCE_DISTILLED_MOONSHINE = coloredWaterFluid("once_distilled_moonshine", 0xE0684F31)
        .register(),
    PERFUME = REGISTRATE.virtualFluid("perfume", new ResourceLocation("destroy", "fluid/swirling"), new ResourceLocation("destroy", "fluid/swirling"), (properties, stillTexture, flowingTexture) -> new ColoredFluidType(properties, stillTexture, flowingTexture, 0x80ffcff7), VirtualFluid::new)
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
        return REGISTRATE.virtualFluid(name, (properties, stillTexture, flowingTexture) -> new ColoredFluidType(properties, new ResourceLocation("minecraft", "block/water_still"), new ResourceLocation("minecraft", "block/water_flow"), color), VirtualFluid::new);
    };

    public static boolean isMixture(FluidStack stack) {
        return !stack.isEmpty() && isMixture(stack.getFluid()) && stack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND);
    };

    public static boolean isMixture(Fluid fluid) {
        return fluid.isSame(MIXTURE.get());
    };

    public static void register() {}

};
