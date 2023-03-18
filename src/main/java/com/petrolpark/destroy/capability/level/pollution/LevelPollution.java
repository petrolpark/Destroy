package com.petrolpark.destroy.capability.level.pollution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class LevelPollution {

    private Map<PollutionType, Integer> levels;

    public LevelPollution() {
        levels = new HashMap<>();
        List.of(PollutionType.values()).forEach(p -> {
            levels.put(p, 0);
        });
    };

    /**
     * Get the value of the given type of Pollution in this Level
     */
    public int get(PollutionType pollutionType) {
        if (pollutionType == null) return 0;
        return levels.get(pollutionType);
    };

    /**
     * Set the value of the given type of Pollution in this Level.
     * This does not broadcast the change to clients.
     * @param pollutionType
     * @param value Will be set within the {@link PollutionType bounds}
     * @return The actual value to which the Pollution level was set
     */
    public int set(PollutionType pollutionType, int value) {
        if (pollutionType == null) return 0;
        value = Mth.clamp(value, 0, pollutionType.max);
        levels.replace(pollutionType, value);
        return value;
    };

    /**
     * Increase the value of the given type of Pollution in this level by the given amount, within the {@link PollutionType bounds} of that type of Pollution.
     * This does not broadcast the change to clients.
     * @param pollutionType
     * @param change Can be positive or negative
     * @return The actual value to which the Pollution level was set
     */
    public int change(PollutionType pollutionType, int change) {
        if (pollutionType == null) return 0;
        return set(pollutionType, levels.get(pollutionType) + change);
    };
    
    public void saveNBTData(CompoundTag tag) {
        levels.forEach((pollutionType, value) -> {
            tag.putInt(pollutionType.name(), value);
        });
    };
  
    public void loadNBTData(CompoundTag tag) {
        levels.keySet().forEach((pollutionType) -> {
            levels.replace(pollutionType, tag.getInt(pollutionType.name()));
        });
    };

    public enum PollutionType {

        GREENHOUSE(65536),

        OZONE_DEPLETION(65536),

        SMOG(65536),

        ACID_RAIN(65536),

        RADIOACTIVITY(65536);

        // Min is always 0
        public int max;

        PollutionType(int max) {
            this.max = max;
        };

    };
};
