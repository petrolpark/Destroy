package com.petrolpark.destroy.util;

import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;

import net.minecraft.world.level.Level;

public class PollutionHelper {

    /**
     * Sets the level of pollution of the given Type in the given Level.
     * If the Level does not have the Pollution Capability, it will be created.
     * The change is broadcast to all clients.
     * Avoid both of these secondary effects by using the {@link com.petrolpark.destroy.capability.LevelPollution#set() set()} instead.
     * @param level
     * @param pollutionType
     * @return The actual value to which the level of pollution was set (0 if there was no capability)
     */
    public static int setPollution(Level level, PollutionType pollutionType, int value) {
        return 0;
        //TODO unfinished
    };
}
