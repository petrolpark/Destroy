package com.petrolpark.destroy.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.util.ForgeSoundType;

public class DestroySoundTypes {
    public static final ForgeSoundType
    
    COOLER = new ForgeSoundType(1, 1,
        () -> DestroySoundEvents.COOLER_BREAK.getMainEvent(),
        () -> SoundEvents.STONE_STEP,
        () -> DestroySoundEvents.COOLER_PLACE.getMainEvent(),
        () -> SoundEvents.STONE_HIT,
        () -> SoundEvents.STONE_FALL);
};
