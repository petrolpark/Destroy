package com.petrolpark.destroy.registrate;

import com.petrolpark.destroy.badge.Badge;
import com.petrolpark.destroy.badge.DestroyBadges;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class BadgeBuilder<T extends Badge, P> extends AbstractBuilder<Badge, T, P, BadgeBuilder<T, P>> {

    private final NonNullSupplier<T> factory;

    public BadgeBuilder(DestroyRegistrate owner, P parent, String name, BuilderCallback callback, NonNullSupplier<T> factory) {
        super(owner, parent, name, callback, Badge.class);
        this.factory = factory;
    };

    @Override
    protected @NonnullType T createEntry() {
        return factory.get();
    };
    
};
