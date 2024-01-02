package com.petrolpark.destroy.block.model;

import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.resources.ResourceLocation;

public class DestroyPartials {

    public static final PartialModel

    AIR = new PartialModel(new ResourceLocation("minecraft", "block/air")),
    
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
    VAT_SIDE_VENT = block("vat_side/vent"),
    VAT_SIDE_VENT_BAR = block("vat_side/vent_bar"),

    // Double Cardan Shaft
    DCS_CENTER_SHAFT = block("double_cardan_shaft/center_shaft"),
    DCS_SIDE_SHAFT = block("double_cardan_shaft/side_shaft"),
    DCS_SIDE_GRIP = block("double_cardan_shaft/side_grip"),
    DCS_GIMBAL = block("double_cardan_shaft/gimbal"),

    // Planetary Gearset
    PG_SUN_GEAR = block("planetary_gearset/sun_gear"),
    PG_PLANET_GEAR = block("planetary_gearset/planet_gear"),
    PG_RING_GEAR = block("planetary_gearset/ring_gear"),

    // Differential
    DIFFERENTIAL_RING_GEAR = block("differential/ring_gear"),
    DIFFERENTIAL_INPUT_GEAR = block("differential/input_gear"),
    DIFFERENTIAL_CONTROL_GEAR = block("differential/control_gear"),
    DIFFERENTIAL_EAST_GEAR = block("differential/east_gear"),
    DIFFERENTIAL_WEST_GEAR = block("differential/west_gear"),
    DIFFERENTIAL_INPUT_SHAFT = block("differential/input_shaft"),
    DIFFERENTIAL_CONTROL_SHAFT = block("differential/control_shaft"),

    // Miscellaneous
    GAS_MASK = block("gas_mask"),
    STRAY_SKULL = block("cooler/skull");

    // Atoms
    static {
        for (Element element : Element.values()) {
            if (element != Element.R_GROUP) element.setPartial(atom(Lang.asId(element.name())));
        };
    };

    // Bonds
    static {
        for (BondType bondType : BondType.values()) {
            bondType.setPartial(bond(Lang.asId(bondType.name())));
        };
    }

    // R-Groups
    public static final PartialModel R_GROUP = rGroup("generic");
    public static final List<PartialModel> rGroups = new ArrayList<>(10);
    static {
        rGroups.add(R_GROUP);
        for (int i = 1; i < 10; i++) {
            rGroups.add(rGroup(String.valueOf(i)));
        };
    };

    private static PartialModel block(String path) { //copied from Create source code
        return new PartialModel(Destroy.asResource("block/"+path));
    };

    private static PartialModel atom(String path) {
        return new PartialModel(Destroy.asResource("chemistry/atom/"+path));
    };

    private static PartialModel bond(String path) {
        return new PartialModel(Destroy.asResource("chemistry/bond/"+path));
    };

    private static PartialModel rGroup(String path) {
        return new PartialModel(Destroy.asResource("chemistry/r_group/"+path));
    };

    public static void init() {};
    
}
