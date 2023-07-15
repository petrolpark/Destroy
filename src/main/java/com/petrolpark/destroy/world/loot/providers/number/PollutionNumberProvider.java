package com.petrolpark.destroy.world.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.util.PollutionHelper;

import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class PollutionNumberProvider implements NumberProvider {

    private final PollutionType pollutionType;
    private float scale;
    private boolean invert;

    public PollutionNumberProvider(PollutionType pollutionType) {
        this.pollutionType = pollutionType;
        scale = 1f;
        invert = false;
    };

    /**
     * Returns a float representing the proportion {@code 0.0} to {@code 1.0} of how full
     * the given type of Pollution is in the level the loot was generated.
     * <p>If {@code invert} is true, this proportion will be reversed (for example, {@code 8000} out of {@code 10000} max Pollution
     * will return 0.2).</p>
     * <p>The result is then multiplied by {@code scale}.</p>
     * <p>Example usage:</p>
     * <pre>
     * {
     *      "type": "destroy:pollution",
     *      "pollution_type": "SMOG",
     *      "scale": 10.0
     *      "invert": false
     * }
     * </pre>
     */
    @Override
    public float getFloat(LootContext lootContext) {
        float rawProportion = (float)PollutionHelper.getPollution(lootContext.getLevel(), pollutionType) / (float)pollutionType.max;
        if (invert) rawProportion = 1f - rawProportion;
        return rawProportion * scale;
    };

    @Override
    public LootNumberProviderType getType() {
        return DestroyNumberProviders.POLLUTION.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<PollutionNumberProvider> {

        @Override
        public void serialize(JsonObject json, PollutionNumberProvider numberProvider, JsonSerializationContext serializationContext) {
            json.addProperty("pollution_type", numberProvider.toString());
            json.addProperty("scale", numberProvider.scale);
            json.addProperty("invert", numberProvider.invert);
        };

        @Override
        public PollutionNumberProvider deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            PollutionNumberProvider numberProvider = new PollutionNumberProvider(PollutionType.valueOf(GsonHelper.getAsString(json, "pollution_type")));
            numberProvider.scale = GsonHelper.getAsFloat(json, "scale", 1.0f);
            numberProvider.invert = GsonHelper.getAsBoolean(json, "invert", false);
            return numberProvider;
        };

    };

};
