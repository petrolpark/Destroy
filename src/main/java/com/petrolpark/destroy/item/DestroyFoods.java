package com.petrolpark.destroy.item;

import java.util.function.Supplier;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class DestroyFoods {

    // Effect Suppliers
    public static Supplier<MobEffectInstance> methHighShort = () -> {
        return new MobEffectInstance(DestroyMobEffects.METH_HIGH.get(), 2400, 0);
    };

    // Foods
    public static final FoodProperties BUTTER = new FoodProperties.Builder().nutrition(6).saturationMod(0.1f).build();
    public static final FoodProperties RAW_FRIES = new FoodProperties.Builder().nutrition(2).saturationMod(0.6f).build();
    public static final FoodProperties UNSEASONED_FRIES = new FoodProperties.Builder().nutrition(6).saturationMod(1.0f).build();
    public static final FoodProperties FRIES = new FoodProperties.Builder().nutrition(8).saturationMod(1.8f).build();
    public static final FoodProperties MASHED_POTATO = new FoodProperties.Builder().nutrition(7).saturationMod(1.4f).build();
    public static final FoodProperties CRUSHED_METHAMPHETAMINE = new FoodProperties.Builder().nutrition(0).saturationMod(0.0F).effect(methHighShort, 1.0f).build();
}
