package com.petrolpark.destroy;

import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlockEntity;
import com.petrolpark.destroy.core.chemistry.MixtureContentsDisplaySource;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.observation.colorimeter.ColorimeterBlockEntity;
import com.petrolpark.destroy.core.pollution.pollutometer.PollutometerDisplaySource;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

public class DestroyDisplaySources {
    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_CONTROLLER_ALL = REGISTRATE.displaySource("vat_controller_all_contents", VatControllerBlockEntity.VatDisplaySource::createAllSource)
            //.associate(DestroyBlocks.VAT_CONTROLLER.get())
            .register();
    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_CONTROLLER_SOLUTION = REGISTRATE.displaySource("vat_controller_solution_contents", VatControllerBlockEntity.VatDisplaySource::createSolutionSource)
            //.associate(DestroyBlocks.VAT_CONTROLLER.get())
            .register();
    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_CONTROLLER_GAS = REGISTRATE.displaySource("vat_controller_gas_contents", VatControllerBlockEntity.VatDisplaySource::createGasSource)
            //.associate(DestroyBlocks.VAT_CONTROLLER.get())
            .register();

    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_SIDE_ALL = REGISTRATE.displaySource("vat_side_all_contents", VatControllerBlockEntity.VatDisplaySource::createAllSource)
            //.associate(DestroyBlocks.VAT_SIDE.get())
            .register();
    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_SIDE_SOLUTION = REGISTRATE.displaySource("vat_side_solution_contents", VatControllerBlockEntity.VatDisplaySource::createSolutionSource)
            //.associate(DestroyBlocks.VAT_SIDE.get())
            .register();
    public static final RegistryEntry<VatControllerBlockEntity.VatDisplaySource> VAT_SIDE_GAS = REGISTRATE.displaySource("vat_side_gas_contents", VatControllerBlockEntity.VatDisplaySource::createGasSource)
            //.associate(DestroyBlocks.VAT_SIDE.get())
            .register();

    public static final RegistryEntry<BubbleCapBlockEntity.BubbleCapDisplaySource> BUBBLE_CAP = REGISTRATE.displaySource("bubble_cap", BubbleCapBlockEntity.BubbleCapDisplaySource::new)
            //.associate(DestroyBlocks.BUBBLE_CAP.get())
            .register();

    public static final RegistryEntry<PollutometerDisplaySource> POLLUTOMETER = REGISTRATE.displaySource("pollutometer", PollutometerDisplaySource::new)
            //.associate(DestroyBlocks.POLLUTOMETER.get())
            .register();

    public static final RegistryEntry<CentrifugeBlockEntity.CentrifugeDisplaySource> CENTRIFUGE_INPUT = REGISTRATE.displaySource("centrifuge_input", CentrifugeBlockEntity.CentrifugeDisplaySource::createInput)
            //.associate(DestroyBlocks.CENTRIFUGE.get())
            .register();
    public static final RegistryEntry<CentrifugeBlockEntity.CentrifugeDisplaySource> CENTRIFUGE_DENSE_OUTPUT = REGISTRATE.displaySource("centrifuge_dense_output", CentrifugeBlockEntity.CentrifugeDisplaySource::createDenseOutput)
            //.associate(DestroyBlocks.CENTRIFUGE.get())
            .register();
    public static final RegistryEntry<CentrifugeBlockEntity.CentrifugeDisplaySource> CENTRIFUGE_LIGHT_OUTPUT = REGISTRATE.displaySource("centrifuge_light_output", CentrifugeBlockEntity.CentrifugeDisplaySource::createLightOutput)
            //.associate(DestroyBlocks.CENTRIFUGE.get())
            .register();

    public static final RegistryEntry<ColorimeterBlockEntity.ColorimeterDisplaySource> COLORIMETER = REGISTRATE.displaySource("colorimeter", ColorimeterBlockEntity.ColorimeterDisplaySource::new)
            //.associate(DestroyBlocks.COLORIMETER.get())
            .register();

    public static void register() {
    }
}
