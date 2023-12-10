package com.petrolpark.destroy.registrate;

import java.util.HashMap;
import java.util.Map;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.advancement.BadgeAdvancementRewards;
import com.petrolpark.destroy.advancement.SimpleDestroyTrigger;
import com.petrolpark.destroy.badge.Badge;
import com.petrolpark.destroy.item.BadgeItem;
import com.petrolpark.destroy.item.creativeModeTab.DestroyCreativeModeTabs;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

@MoveToPetrolparkLibrary
public class BadgeBuilder<T extends Badge, P> extends AbstractBuilder<Badge, T, P, BadgeBuilder<T, P>> {
    
    private final NonNullSupplier<T> factory;

    protected ItemEntry<BadgeItem> item;
    protected Ingredient duplicationIngredient;

    public static <T extends Badge, P> BadgeBuilder<T, P> create(DestroyRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        return new BadgeBuilder<>(owner, parent, name, callback, factory);
    };

    public BadgeBuilder(DestroyRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, Destroy.BADGE_REGISTRY_KEY);
        this.factory = factory;

        duplicationIngredient = Ingredient.EMPTY;
        item = getOwner().item("badge/"+getName(), p -> new BadgeItem(p, () -> this.getEntry()))
            .tab(null)
            .register();
        DestroyCreativeModeTabs.DestroyDisplayItemsGenerator.excludedItems.add(item);
    };

    public BadgeBuilder<T, P> duplicationIngredient(Ingredient ingredient) {
        duplicationIngredient = ingredient;
        return this;
    };

    @Override
    protected @NonnullType T createEntry() {
        T badge = factory.get();

        badge.setId(new ResourceLocation(getOwner().getModid(), getName()));

        SimpleDestroyTrigger advancementTrigger = new SimpleDestroyTrigger("get_badge_"+getOwner().getModid()+"_"+getName());
        badge.setAdvancementTrigger(advancementTrigger);
        CriteriaTriggers.register(advancementTrigger);

        badge.setBadgeItem(item);

        badge.setDuplicationItem(duplicationIngredient);

        return badge;
    };

    public static Map<ResourceLocation, Advancement.Builder> getAdvancements() {
        Map<ResourceLocation, Advancement.Builder> advancements = new HashMap<>();
        for (Badge badge : Badge.badgeRegistry().getValues()) {
            Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
            advancementBuilder
               .parent(new ResourceLocation("petrolpark", "badge_root"))
                    // .display(
                    //     badge::getItem,
                    //     badge.getName(),
                    //     badge.getDescription(),
                    //     null,
                    //     FrameType.CHALLENGE,
                    //     false,
                    //     false,
                    //     true
                    // )
                    .rewards(new BadgeAdvancementRewards(badge))
                    .addCriterion("get_badge", badge.advancementTrigger.instance())
                    .requirements(new String[][]{new String[]{"get_badge"}}); 
            advancements.put(new ResourceLocation(badge.getId().getNamespace(), "badge/"+badge.getId().getPath()), advancementBuilder);
        };
        return advancements;
    };
    
};
