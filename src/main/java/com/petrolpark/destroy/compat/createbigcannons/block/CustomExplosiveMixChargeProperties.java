package com.petrolpark.destroy.compat.createbigcannons.block;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.petrolpark.destroy.core.explosion.mixedexplosive.ExplosiveProperties.ExplosiveProperty;
import net.createmod.catnip.lang.Lang;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.config.BigCannonPropellantPropertiesComponent;
import rbasamoyai.createbigcannons.munitions.config.BlockPropertiesTypeHandler;

public record CustomExplosiveMixChargeProperties(BigCannonPropellantPropertiesComponent basePropellantProperties, EnumMap<ExplosiveProperty, BigCannonPropellantPropertiesComponent> propellantPropertyModifiers) {

    public static final CustomExplosiveMixChargeProperties DEFAULT = new CustomExplosiveMixChargeProperties(BigCannonPropellantPropertiesComponent.DEFAULT, new EnumMap<ExplosiveProperty, BigCannonPropellantPropertiesComponent>(Arrays.stream(ExplosiveProperty.values()).collect(Collectors.toMap(p -> p, p -> BigCannonPropellantPropertiesComponent.DEFAULT))));
    
    public static class Handler extends BlockPropertiesTypeHandler<CustomExplosiveMixChargeProperties> {

        @Override
        protected CustomExplosiveMixChargeProperties parseJson(ResourceLocation location, JsonObject obj) throws JsonParseException {
            return new CustomExplosiveMixChargeProperties(BigCannonPropellantPropertiesComponent.fromJson(location.toString(), obj), new EnumMap<ExplosiveProperty, BigCannonPropellantPropertiesComponent>(Arrays.stream(ExplosiveProperty.values()).collect(Collectors.toMap(p -> p, p -> BigCannonPropellantPropertiesComponent.fromJson(location.toString(), GsonHelper.getAsJsonObject(obj, Lang.asId(p.name())))))));
        };

        @Override
        protected CustomExplosiveMixChargeProperties readPropertiesFromNetwork(Block type, FriendlyByteBuf buf) {
            EnumMap<ExplosiveProperty, BigCannonPropellantPropertiesComponent> modifiers = new EnumMap<>(ExplosiveProperty.class);
            for (ExplosiveProperty property : ExplosiveProperty.values()) modifiers.put(property, BigCannonPropellantPropertiesComponent.fromNetwork(buf));
            return new CustomExplosiveMixChargeProperties(BigCannonPropellantPropertiesComponent.fromNetwork(buf), modifiers);
        };

        @Override
        protected void writePropertiesToNetwork(CustomExplosiveMixChargeProperties properties, FriendlyByteBuf buf) {
            for (BigCannonPropellantPropertiesComponent modifier : properties.propellantPropertyModifiers().values()) modifier.toNetwork(buf);
            properties.basePropellantProperties().toNetwork(buf);
        };

        @Override
        protected CustomExplosiveMixChargeProperties getNoPropertiesValue() {
            return DEFAULT;
        };

    };
};
