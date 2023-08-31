package com.petrolpark.destroy.config;

public class DestroyPollutionConfigs extends DestroyConfigBase {

    public final ConfigBool enablePollution = b(true, "enablePollution", Comments.enablePollution);

    public final ConfigGroup configGroup = group(0, Comments.visualChanges);
    public final ConfigBool smog = b(true, "smog", Comments.smog);
    public final ConfigBool rainColorChanges = b(true, "rainColorChanges", Comments.rainColorChanges);

    public final ConfigGroup gameplayChanges = group(0, Comments.gameplayChanges);
    public final ConfigBool villagersIncreasePrices = b(true, "villagersIncreasePrices", Comments.villagersIncreasePrices);
    public final ConfigBool fishingAffected = b(true, "fishingAffected", Comments.fishingAffected);
    public final ConfigBool breedingAffected = b(true, "breedingAffected", Comments.breedingAffected);
    public final ConfigBool growingAffected = b(true, "growingAffected", Comments.growingAffected);
    public final ConfigBool rainBreaksBlocks = b(true, "rainBreaksBlocks", Comments.rainBreaksBlocks);
    public final ConfigBool temperatureAffected = b(true, "temperatureAffected", Comments.temperatureAffected);
    public final ConfigBool ozoneDepletionGivesCancer = b(true, "ozoneDepletionGivesCancer", Comments.ozoneDepletionGivesCancer);
    
    @Override
	public String getName() {
		return "pollution";
	};

    private static class Comments {
        static String
        enablePollution = "Releasing chemicals increases pollution",

        visualChanges = "Visual Changes",
        smog = "The sky and grass turn browner the higher the Smog level",
        rainColorChanges = "The rain turns greener the higher the Acid Rain level",

        gameplayChanges = "Gameplay Changes",
        villagersIncreasePrices = "Villagers increase their prices the higher the Smog level",
        fishingAffected = "Fishing yields fewer fish and more junk the higher the Smog level",
        breedingAffected = "Mobs will be more likely to fail to breed the higher the Smog level",
        growingAffected = "Crops are less likely to grow the higher the Smog, Greenhouse Gas and Acid Rain levels",
        rainBreaksBlocks = "Rain is more likely to kill plants and grass the higher the Acid Rain level",
        temperatureAffected = "Outdoor temperature (which affects Distillation Towers and Vats) increases with Greenhouse Gas and Ozone Depletion levels",
        ozoneDepletionGivesCancer = "The likelihood of getting the cancer awareness pop-up from the sun increases with the Ozone Depletion level";
    };
};
