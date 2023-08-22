package com.petrolpark.destroy.block.model;

import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.simibubi.create.foundation.utility.Lang;

public class DestroyPartials {

    public static final PartialModel
    
    // Kinetics
    CENTRIFUGE_COG = block("centrifuge/inner"),
    DYNAMO_COG = block("dynamo/inner"),

    // Pumpjack
    PUMPJACK_CAM = block("pumpjack/cam"),
    PUMPJACK_LINKAGE = block("pumpjack/linkage"),
    PUMPJACK_BEAM = block("pumpjack/beam"),
    PUMPJACK_PUMP = block("pumpjack/pump"),

    // Pollutometer
    POLLUTOMETER_ANEMOMETER = block("pollutometer/anemometer"),
    POLLUTOMETER_WEATHERVANE = block("pollutometer/weathervane"),

    // Vat
    VAT_SIDE_PIPE = block("vat_side/pipe"),
    VAT_SIDE_BAROMETER = block("vat_side/barometer"),
    VAT_SIDE_THERMOMETER = block("vat_side/thermometer"),

    // Double Cardan Shaft
    DCS_CENTER_SHAFT = block("double_cardan_shaft/center_shaft"),
    DCS_SIDE_SHAFT = block("double_cardan_shaft/side_shaft"),
    DCS_SIDE_GRIP = block("double_cardan_shaft/side_grip"),
    DCS_GIMBAL = block("double_cardan_shaft/gimbal"),

    // Planetary Gearset
    PG_SUN_GEAR = block("planetary_gearset/sun_gear"),
    PG_PLANET_GEAR = block("planetary_gearset/planet_gear"),
    PG_RING_GEAR = block("planetary_gearset/ring_gear"),

    // Miscellaneous
    GAS_MASK = block("gas_mask"),
    STRAY_SKULL = block("cooler/skull");

    // Atoms
    static {
        for (Element element : Element.values()) {
            element.setPartial(atom(Lang.asId(element.name())));
        };
    };

    // Bonds
    static {
        for (BondType bondType : BondType.values()) {
            bondType.setPartial(bond(Lang.asId(bondType.name())));
        };
    }

    private static PartialModel block(String path) { //copied from Create source code
        return new PartialModel(Destroy.asResource("block/"+path));
    };

    private static PartialModel atom(String path) {
        return new PartialModel(Destroy.asResource("chemistry/atom/"+path));
    };

    private static PartialModel bond(String path) {
        return new PartialModel(Destroy.asResource("chemistry/bond/"+path));
    };

    public static void init() {};
    
}
