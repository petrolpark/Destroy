package com.petrolpark.destroy.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;

public class DestroySoundTypes {
    public static final ForgeSoundType
    
    //TODO fix once sounds are actually properly implemented
    COOLER = new ForgeSoundType(1, 1,
        () -> SoundEvents.STONE_BREAK, //() -> DestroySoundEvents.COOLER_BREAK.getMainEvent(),
        () -> SoundEvents.STONE_STEP,
        () -> SoundEvents.STONE_PLACE, //() -> DestroySoundEvents.COOLER_PLACE.getMainEvent(),
        () -> SoundEvents.STONE_HIT,
        () -> SoundEvents.STONE_FALL);
};
