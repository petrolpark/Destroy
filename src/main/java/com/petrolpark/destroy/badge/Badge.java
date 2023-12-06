package com.petrolpark.destroy.badge;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class Badge {

    public static final ResourceKey<Registry<Badge>> BADGE_REGISTRY_KEY = ResourceKey.createRegistryKey(Destroy.asResource("badge"));
    public static final ForgeRegistry<Badge> BADGE_REGISTRY = RegistryManager.ACTIVE.getRegistry(BADGE_REGISTRY_KEY);

    protected Ingredient duplicationIngredient;

    public Badge() {
        duplicationIngredient = Ingredient.EMPTY;
    };

    public void setDuplicationItem(Ingredient ingredient) {
        if (duplicationIngredient != Ingredient.EMPTY) throw new UnsupportedOperationException("Cannot modify Badge's duplication Item");
        duplicationIngredient = ingredient;
    };

    public Ingredient getDuplicationIngredient() {
        return duplicationIngredient;
    };

    public Component getName() {
        return Component.translatable(Util.makeDescriptionId("badge", getId(this)));
    };

    public Component getDescription() {
        return Component.translatable(Util.makeDescriptionId("badge", getId(this)) + ".description");
    };

    @Nullable
    public static Badge getBadge(String namespace, String name) {
        return getBadge(new ResourceLocation(namespace, name));
    };
    
    @Nullable
    public static Badge getBadge(ResourceLocation id) {
        return BADGE_REGISTRY.getValue(id);
    };

    public static ResourceLocation getId(Badge badge) {
        return BADGE_REGISTRY.getKey(badge);
    };

};
