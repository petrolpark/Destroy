package com.petrolpark.destroy.capability.level.pollution;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientLevelPollutionData {
    private static LevelPollution levelPollution;

    public static void setLevelPollution(LevelPollution levelPollution) {
        ClientLevelPollutionData.levelPollution = levelPollution;
    };

    public static LevelPollution getLevelPollution() {
        return levelPollution;
    };
}
