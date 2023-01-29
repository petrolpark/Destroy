package com.petrolpark.destroy.ponder;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;

import net.minecraft.core.Direction;

public class CentrifugeScenes {

    public static void separate(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("centrifuge", "Separating Fluids with the Centrifuge");
        scene.configureBasePlate(0, 1, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.idle(60);
    };

};
