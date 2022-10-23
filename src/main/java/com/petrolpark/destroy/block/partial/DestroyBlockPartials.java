package com.petrolpark.destroy.block.partial;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;

public class DestroyBlockPartials {

    public static final PartialModel CENTRIFUGE_COG = block("centrifuge/inner");
    public static final PartialModel CENTRIFUGE_BASE = block("centrifuge/block");

    private static PartialModel block(String path) { //copied from Create source code
        return new PartialModel(Create.asResource("block/"+ path));
    }
    
}
