package com.petrolpark.destroy.util;

import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.networking.DestroyMessages;
import com.petrolpark.destroy.networking.packet.LevelPollutionS2CPacket;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class PollutionHelper {

    /**
     * Gets the level of pollution of the given Type in the given Level.
     * @param level
     * @param pollutionType
     * @return 0 if the Level does not have the Level Pollution capability
     */
    public static int getPollution(Level level, PollutionType pollutionType) {
        return level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> levelPollution.get(pollutionType)).orElse(0);
    };

    /**
     * Sets the level of pollution of the given Type in the given Level.
     * The change is broadcast to all clients (Avoid this by using the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution#set set()} method instead).
     * @param level
     * @param pollutionType
     * @param value Will be set within the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType bounds}.
     * @return The actual value to which the level of pollution was set (0 if there was no Capability)
     */
    public static int setPollution(Level level, PollutionType pollutionType, int value) {
        return level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
            int oldValue = levelPollution.get(pollutionType);
            int newValue = levelPollution.set(pollutionType, value); // Actually set the Pollution level
            if (oldValue != newValue) { // If there has been a change (which needs to be broadcast to clients)
                DestroyMessages.sendToAllClients(new LevelPollutionS2CPacket(levelPollution));
            };
            return newValue;
        }).orElse(0);
    };

    /**
     * Changes the level of pollution of the given Type in the given Level by the given amount.
     * The change is broadcast to all clients (Avoid this by using the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution#change change()} method instead).
     * @param level
     * @param pollutionType
     * @param change Can be positive or negative; will be set within the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType bounds}.
     * @return The actual value to which the level of pollution was set (0 if there was no Capability)
     */
    public static int changePollution(Level level, PollutionType pollutionType, int change) {
        return setPollution(level, pollutionType, Mth.clamp(getPollution(level, pollutionType) + change, 0, pollutionType.max));
    };
};
