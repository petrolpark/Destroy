package com.petrolpark.destroy.sound;

import com.petrolpark.destroy.Destroy;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroySoundEvents {
  
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Destroy.MOD_ID);

    public static final RegistryObject<SoundEvent>
    
    MUSIC_DISC_SPECTRUM = registerSoundEvent("music_disc_spectrum");
    
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(Destroy.asResource(name)));
    };

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    };
};
