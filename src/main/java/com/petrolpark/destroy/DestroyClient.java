package com.petrolpark.destroy;

import com.petrolpark.destroy.client.*;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventoryClientHandler;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class DestroyClient {
    
    public static final FogHandler FOG_HANDLER = new FogHandler();
    public static final ExtendedInventoryClientHandler EXTENDED_INVENTORY_HANDLER = new ExtendedInventoryClientHandler();

    static {
        DestroyItemDisplayContexts.register();
    };

    public static void clientInit(final FMLClientSetupEvent event) {
        // Work which must be done on main thread
        event.enqueueWork(DestroyItemProperties::register);
        //DestroyPonderTags.register();
        //DestroyPonderScenes.register();
        PonderIndex.addPlugin(new DestroyPonderPlugin());
    };

    public static void clientCtor(IEventBus modEventBus, IEventBus forgeEventBus) {
        DestroySpriteSource.register();
        DestroyPartials.init();
        modEventBus.addListener(DestroyParticleTypes::registerProviders);
    };
};
