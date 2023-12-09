package com.petrolpark.destroy.badge;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.advancement.SimpleDestroyTrigger;
import com.petrolpark.destroy.item.BadgeItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class Badge {

    public static ForgeRegistry<Badge> badgeRegistry() {
        return RegistryManager.ACTIVE.getRegistry(Destroy.BADGE_REGISTRY_KEY);
    };

    protected ResourceLocation id;
    protected ItemEntry<BadgeItem> itemEntry;

    protected Ingredient duplicationIngredient;
    public SimpleDestroyTrigger advancementTrigger;

    public Badge() {
        duplicationIngredient = Ingredient.EMPTY;
        advancementTrigger = null;
    };

    public void setDuplicationItem(Ingredient ingredient) {
        if (duplicationIngredient != Ingredient.EMPTY) throw new UnsupportedOperationException("Cannot modify Badge's duplication Item");
        duplicationIngredient = ingredient;
    };

    public Ingredient getDuplicationIngredient() {
        return duplicationIngredient;
    };

    public void setAdvancementTrigger(SimpleDestroyTrigger trigger) {
        if (advancementTrigger != null) throw new UnsupportedOperationException("Cannot reset Badge advancement trigger");
        advancementTrigger = trigger;
    };

    public void grantAdvancement(Player player) {
        if (advancementTrigger != null && player instanceof ServerPlayer serverPlayer) {
            advancementTrigger.trigger(serverPlayer);
        };
    };

    public void setId(ResourceLocation id) {
        if (this.id != null) throw new UnsupportedOperationException("Cannot change a Badge's ID");
        this.id = id;
    };

    public void setBadgeItem(ItemEntry<BadgeItem> entry) {
        if (itemEntry != null) throw new UnsupportedOperationException("Cannot reset a Badge's Item");
        itemEntry = entry;
    };

    public Component getName() {
        return Component.translatable(Util.makeDescriptionId("badge", getId()));
    };

    public Component getDescription() {
        return Component.translatable(Util.makeDescriptionId("badge", getId()) + ".description");
    };

    public ResourceLocation getId() {
        return id;
    };

    public BadgeItem getItem() {
        return itemEntry.get();
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Badge badge && getId() != null && badge.getId() == getId();
    };

    @Nullable
    public static Badge getBadge(String namespace, String name) {
        return getBadge(new ResourceLocation(namespace, name));
    };
    
    @Nullable
    public static Badge getBadge(ResourceLocation id) {
        return badgeRegistry().getValue(id);
    };

};
