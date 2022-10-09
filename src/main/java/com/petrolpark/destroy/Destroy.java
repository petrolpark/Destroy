package com.petrolpark.destroy;

import com.mojang.logging.LogUtils;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.item.DestroyItems;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Destroy.MOD_ID)
public class Destroy
{
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "destroy";

    public Destroy()
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DestroyBlocks.register(eventBus);
        DestroyMobEffects.register(eventBus);
        DestroyItems.register(eventBus);

        DestroyAllConfigs.register(modLoadingContext);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
