package com.petrolpark.destroy.sound;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllSoundEvents.SoundEntry;
import com.simibubi.create.AllSoundEvents.SoundEntryBuilder;

import net.minecraft.sounds.SoundSource;

public class DestroySoundEvents {

    public static final SoundEntry

    AGING_BARREL_BALLOON = create("aging_barrel_balloon")
        .category(SoundSource.BLOCKS)
        .build(),

    AGING_BARREL_SHUT = create("aging_barrel_shut")
        .category(SoundSource.BLOCKS)
        .build(),

    AGING_BARREL_OPEN = create("aging_barrel_open")
        .category(SoundSource.BLOCKS)
        .build(),

    COOLER_BREAK = create("cooler_break")
        .category(SoundSource.BLOCKS)
        .build(),

    COOLER_PLACE = create("cooler_place")
        .category(SoundSource.BLOCKS)
        .build(),

    DISTILLATION_TOWER_BOIL = create("distillate_boil")
        .category(SoundSource.BLOCKS)
        .build(),

    DISTILLATION_TOWER_CONDENSE = create("distillate_condense")
        .category(SoundSource.BLOCKS)
        .build(),

    DYNAMO_CRACKLE = create("dynamo_crackle")
        .category(SoundSource.AMBIENT)
        .build(),
    
    MUSIC_DISC_SPECTRUM = create("music_disc_spectrum")
        .category(SoundSource.RECORDS)
        .build(),

    OIL_GURGLE = create("oil_gurgle")
        .category(SoundSource.BLOCKS)
        .build(),

    PUMPJACK_CREAK_1 = create("pumpjack_creak_1")
        .category(SoundSource.BLOCKS)
        .build(),

    PUMPJACK_CREAK_2 = create("pumpjack_creak_2")
        .category(SoundSource.BLOCKS)
        .build(),

    SPRAY_BOTTLE_SPRAYS = create("spray_bottle_spray")
        .category(SoundSource.PLAYERS)
        .build(),

    URINATE = create("urinate")
        .category(SoundSource.PLAYERS)
        .build();
    
    private static SoundEntryBuilder create(String name) {
        return AllSoundEvents.create(Destroy.asResource(name));
    };

    public static void prepare() {
		AllSoundEvents.ALL.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(Destroy.MOD_ID)).forEach(entry -> entry.getValue().prepare());
	};
};
