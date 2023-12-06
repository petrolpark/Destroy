package com.petrolpark.destroy.registrate;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.badge.Badge;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.world.item.crafting.Ingredient;

@MoveToPetrolparkLibrary
public class BadgeBuilder<T extends Badge, P> extends AbstractBuilder<Badge, T, P, BadgeBuilder<T, P>> {

    private final NonNullSupplier<T> factory;

    protected Ingredient duplicationIngredient;

    public static <T extends Badge, P> BadgeBuilder<T, P> create(DestroyRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        return new BadgeBuilder<>(owner, parent, name, callback, factory);
    };

    public BadgeBuilder(DestroyRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, Badge.BADGE_REGISTRY_KEY);
        this.factory = factory;

        duplicationIngredient = Ingredient.EMPTY;
    };

    public BadgeBuilder<T, P> duplicationIngredient(Ingredient ingredient) {
        duplicationIngredient = ingredient;
        return this;
    };

    @Override
    protected @NonnullType T createEntry() {
        T badge = factory.get();
        badge.setDuplicationItem(duplicationIngredient);
        return badge;
    };
    
};
