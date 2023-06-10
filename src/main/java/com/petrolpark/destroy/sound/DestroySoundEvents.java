package com.petrolpark.destroy.sound;

import com.petrolpark.destroy.Destroy;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllSoundEvents.SoundEntry;
import com.simibubi.create.AllSoundEvents.SoundEntryBuilder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DestroySoundEvents {
  
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Destroy.MOD_ID);

    public static final SoundEntry


    AGING_BARREL_BALLOON = create("aging_barrel_balloon")
        .category(SoundSource.BLOCKS)
        .build(),

    AGING_BARREL_SHUT = create("aging_barrel_shut")
        .playExisting(SoundEvents.BARREL_CLOSE)
        .playExisting(SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT)
        .category(SoundSource.BLOCKS)
        .build(),

    AGING_BARREL_OPEN = create("aging_barrel_open")
        .playExisting(SoundEvents.BARREL_OPEN)
        .category(SoundSource.BLOCKS)
        .build(),

    COOLER_BREAK = create("cooler_break")
        .playExisting(SoundEvents.STONE_BREAK)
        .playExisting(SoundEvents.SKELETON_STEP)
        .category(SoundSource.BLOCKS)
        .build(),

    COOLER_PLACE = create("cooler_place")
        .playExisting(SoundEvents.STONE_PLACE)
        .playExisting(SoundEvents.SKELETON_STEP)
        .category(SoundSource.BLOCKS)
        .build(),

    DISTILLATION_TOWER_BOIL = create("distillate_boil")
        .playExisting(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 1f, 1.45f)
        .category(SoundSource.BLOCKS)
        .build(),

    DISTILLATION_TOWER_CONDENSE = create("distillate_condense")
        .playExisting(SoundEvents.FIRE_EXTINGUISH)
        .playExisting(SoundEvents.BUCKET_EMPTY)
        .category(SoundSource.BLOCKS)
        .build(),

    DYNAMO_CRACKLE = create("dynamo_crackle")
        .category(SoundSource.AMBIENT)
        .build(),
    
    MUSIC_DISC_SPECTRUM = create("music_disc_spectrum")
        .noSubtitle()
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
        .build();
    
    private static SoundEntryBuilder create(String name) {
        return AllSoundEvents.create(Destroy.asResource(name));
    };
};
