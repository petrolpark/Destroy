package com.petrolpark.destroy.block.partial;

import com.jozufozu.flywheel.core.PartialModel;
import com.petrolpark.destroy.Destroy;

public class DestroyBlockPartials {

    public static final PartialModel CENTRIFUGE_COG = block("centrifuge/inner"),
    DYNAMO_COG = block("dynamo/inner");

    private static PartialModel block(String path) { //copied from Create source code
        return new PartialModel(Destroy.asResource("block/"+path));
    };

    public static void init() {};
    
}
