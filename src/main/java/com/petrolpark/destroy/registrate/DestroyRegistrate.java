package com.petrolpark.destroy.registrate;

import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.badge.Badge;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

@MoveToPetrolparkLibrary
public class DestroyRegistrate extends CreateRegistrate {

    public DestroyRegistrate(String modid) {
        super(modid);
    };

    public BadgeBuilder<Badge, DestroyRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeBuilder<T, DestroyRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeBuilder<T, DestroyRegistrate>) entry(name, c -> BadgeBuilder.create(this, this, name, c, factory));
	};
    
};
