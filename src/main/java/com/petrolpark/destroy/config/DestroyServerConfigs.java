package com.petrolpark.destroy.config;

public class DestroyServerConfigs extends DestroyConfigBase {

    public final DestroyContraptionsConfigs contraptions = nested(0, DestroyContraptionsConfigs::new, Comments.contraptions);
	public final DestroyPollutionConfigs pollution = nested(0, DestroyPollutionConfigs::new, Comments.pollution);
    public final ConfigBool automaticGoggles = b(true, Comments.autoGoggles);
    
    @Override
    public String getName() {
        return "server";
    };

    private static class Comments {
        static String

        contraptions = "Destroy's processing machines",

		pollution = "Change the effects of pollution on the world",

		autoGoggles = "Players in Creative mode are treated as if they are wearing Engineer's Goggles even if they are not";
    };
}
