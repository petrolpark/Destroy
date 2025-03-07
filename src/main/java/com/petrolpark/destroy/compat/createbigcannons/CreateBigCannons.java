package com.petrolpark.destroy.compat.createbigcannons;

import com.petrolpark.destroy.compat.createbigcannons.block.CreateBigCannonsBlocks;
import com.petrolpark.destroy.compat.createbigcannons.block.entity.CreateBigCannonBlockEntityTypes;
import com.petrolpark.destroy.compat.createbigcannons.entity.CreateBigCannonsEntityTypes;
import com.petrolpark.destroy.compat.createbigcannons.event.CreateBigCannonsClientModEvents;
import com.petrolpark.destroy.compat.createbigcannons.ponder.CreateBigCannonsPonderPlugin;
import com.petrolpark.destroy.compat.createbigcannons.ponder.CreateBigCannonsPonderScenes;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CreateBigCannons {
    
    public static void init(IEventBus modEventBus, IEventBus forgeEventBus) {
        DestroyMunitionPropertiesHandlers.init();
        CreateBigCannonsBlocks.register();
        CreateBigCannonBlockEntityTypes.register();
        CreateBigCannonsEntityTypes.register();

        // Initiation events
        modEventBus.addListener(CreateBigCannons::onClientSetup);
        forgeEventBus.addListener(CreateBigCannons::onCommonSetup);
        
        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.register(CreateBigCannonsClientModEvents.class));
    };

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        DestroyBlobEffects.registerBlobEffects();
    };

    public static void onClientSetup(FMLClientSetupEvent event) {
        //CreateBigCannonsPonderScenes.register();
        //CreateBigCannonsPonderScenes.registerTags();
        PonderIndex.addPlugin(new CreateBigCannonsPonderPlugin());
    };
};
