package com.petrolpark.destroy.badge;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.tterrag.registrate.util.entry.RegistryEntry;

public class DestroyBadges {

    public static final RegistryEntry<Badge>
    
    EARLY_BIRD = REGISTRATE.badge("early_bird")
        .register();

    public static void register() {};
};
