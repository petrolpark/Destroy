package com.petrolpark.destroy.world.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.petrolpark.destroy.world.explosion.SmartExplosion;
import com.petrolpark.destroy.world.loot.DestroyLootContextParams;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ObliterationCondition implements LootItemCondition {

    private static final ObliterationCondition INSTANCE = new ObliterationCondition();

    @Override
    public boolean test(LootContext context) {
        SmartExplosion explosion = context.getParamOrNull(DestroyLootContextParams.SMART_EXPLOSION);
        if (explosion != null) {
            return explosion.shouldDoSpecialDrops();
        } else {
            return false;
        }
    };

    @Override
    public LootItemConditionType getType() {
        return DestroyLootConditions.OBLITERATION.get();
    };

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ObliterationCondition> {

        @Override
        public void serialize(JsonObject json, ObliterationCondition value, JsonSerializationContext serializationContext) {};

        @Override
        public ObliterationCondition deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            return INSTANCE;
        };

    };
    
};
