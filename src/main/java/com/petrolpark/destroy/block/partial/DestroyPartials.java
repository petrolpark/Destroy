package com.petrolpark.destroy.block.partial;

import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.Element;
import com.simibubi.create.foundation.utility.Lang;

public class DestroyPartials {

    public static final PartialModel
    
    CENTRIFUGE_COG = block("centrifuge/inner"),
    DYNAMO_COG = block("dynamo/inner"),
    POLLUTOMETER_ANEMOMETER = block("pollutometer/anemometer"),
    POLLUTOMETER_WEATHERVANE = block("pollutometer/weathervane"),
    GAS_MASK = block("gas_mask"),
    STRAY_SKULL = block("cooler/skull");

    static {
        for (Element element : Element.values()) {
            element.setPartial(atom(Lang.asId(element.name())));
        };
    };

    private static PartialModel block(String path) { //copied from Create source code
        return new PartialModel(Destroy.asResource("block/"+path));
    };

    private static PartialModel atom(String path) {
        return new PartialModel(Destroy.asResource("chemistry/atom/"+path));
    };

    public static void init() {};
    
}
