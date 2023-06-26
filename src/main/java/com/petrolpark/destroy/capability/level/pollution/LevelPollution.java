package com.petrolpark.destroy.capability.level.pollution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.client.gui.DestroyIcons;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class LevelPollution {

    private Map<PollutionType, Integer> levels;
    private float outdoorTemperature; // In kelvins

    private boolean hasPollutionEverBeenMaxed;
    private boolean hasPollutionEverBeenFullyReduced;

    public LevelPollution() {
        levels = new HashMap<>();
        List.of(PollutionType.values()).forEach(p -> {
            levels.put(p, 0);
        });
        hasPollutionEverBeenMaxed = false;
        hasPollutionEverBeenFullyReduced = true;
        outdoorTemperature = 289.0f; // 16ºC
    };

    /**
     * Get the value of the given type of Pollution in this Level.
     */
    public int get(PollutionType pollutionType) {
        if (pollutionType == null) return 0;
        return levels.get(pollutionType);
    };

    /**
     * Get the global outdoor temperature, not accounting for the biome.
     * @return Temperature in kelvins
     * @see LevelPollution#getLocalTemperature Get the temperature accounting for the biome.
     */
    public float getGlobalTemperature() {
        return outdoorTemperature;
    };

    /**
     * Get the outdoor ("room") temperature at the given position, accounting for the change in temperature due to pollution
     * and the natural heat of the Biome at that position.
     * @param level
     * @param pos
     * @return Temperature in kelvins
     * @see LevelPollution#getGlobalTemperature Get the temperature not accounting for the Biome
     */
    public static float getLocalTemperature(Level level, BlockPos pos) {
        return level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(pollution -> {
            return pollution.outdoorTemperature + (10f * level.getBiome(pos).get().getBaseTemperature());
        }).orElse(289f);
    };

    /**
     * Set the value of the given type of Pollution in this Level.
     * This does not broadcast the change to clients or reward advancements.
     * @param pollutionType
     * @param value Will be set within the {@link PollutionType bounds}
     * @return The actual value to which the Pollution level was set
     */
    public int set(PollutionType pollutionType, int value) {
        if (pollutionType == null) return 0;
        value = Mth.clamp(value, 0, pollutionType.max);
        levels.replace(pollutionType, value);

        if (
            levels.get(PollutionType.ACID_RAIN) == PollutionType.ACID_RAIN.max
            && levels.get(PollutionType.OZONE_DEPLETION) == PollutionType.OZONE_DEPLETION.max
            && levels.get(PollutionType.SMOG) == PollutionType.SMOG.max
            && levels.get(PollutionType.GREENHOUSE) == PollutionType.GREENHOUSE.max
            //currently no radioactivity
        ) {
            hasPollutionEverBeenMaxed = true;
        } else if (
            hasPollutionEverBeenMaxed
            && levels.get(PollutionType.ACID_RAIN) == 0
            && levels.get(PollutionType.OZONE_DEPLETION) == 0
            && levels.get(PollutionType.SMOG) == 0
            && levels.get(PollutionType.GREENHOUSE) == 0
            //currently no radioactivity
        ) {
            hasPollutionEverBeenFullyReduced = true;
        };

        updateTemperature();
        return value;
    };

    /**
     * Increase the value of the given type of Pollution in this level by the given amount, within the {@link PollutionType bounds} of that type of Pollution.
     * This does not broadcast the change to clients or reward advancements.
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
        tag.putBoolean("EverMaxed", hasPollutionEverBeenMaxed);
        tag.putBoolean("EverReduced", hasPollutionEverBeenFullyReduced);
    };
  
    public void loadNBTData(CompoundTag tag) {
        levels.keySet().forEach((pollutionType) -> {
            levels.replace(pollutionType, tag.getInt(pollutionType.name()));
        });
        hasPollutionEverBeenMaxed = tag.getBoolean("EverMaxed");
        hasPollutionEverBeenFullyReduced = tag.getBoolean("EverReduced");
        updateTemperature();
    };

    public void updateTemperature() {
        outdoorTemperature = 289f
            + (levels.get(PollutionType.GREENHOUSE) / PollutionType.GREENHOUSE.max) * 20f
            + (levels.get(PollutionType.OZONE_DEPLETION) / PollutionType.OZONE_DEPLETION.max) * 4f;
    };

    /**
     * Whether all Pollution Types have had their maximum value at any point in the past or present.
     */
    public boolean hasPollutionEverBeenMaxed() {
        return hasPollutionEverBeenMaxed;
    };

    /**
     * Whether every Pollution Type is at 0, where before they were all maxed.
     */
    public boolean hasPollutionEverBeenFullyReduced() {
        return hasPollutionEverBeenFullyReduced;
    };

    public enum PollutionType implements INamedIconOptions {

        GREENHOUSE(DestroyIcons.GREENHOUSE, 65536),

        OZONE_DEPLETION(DestroyIcons.OZONE_DEPLETION, 65536),

        SMOG(DestroyIcons.SMOG, 65536),

        ACID_RAIN(DestroyIcons.ACID_RAIN, 65536),

        RADIOACTIVITY(DestroyIcons.RADIOACTIVITY, 65536);

        private final AllIcons icon;
        // Min is always 0
        public final int max;

        PollutionType(AllIcons icon, int max) {
            this.icon = icon;
            this.max = max;
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        };

        @Override
        public String getTranslationKey() {
            return "destroy.pollution."+Lang.asId(name());
        };

    };
};
