package com.petrolpark.destroy;

import com.mojang.logging.LogUtils;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.partial.DestroyBlockPartials;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.client.particle.DestroyParticleTypes;
import com.petrolpark.destroy.client.ponder.DestroySceneIndex;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.DestroyPotatoCannonProjectileTypes;
import com.petrolpark.destroy.item.HyperaccumulatingFertilizerItem;
import com.petrolpark.destroy.networking.DestroyMessages;
import com.petrolpark.destroy.recipe.DestroyCropMutations;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.DestroyTags;
import com.petrolpark.destroy.world.DestroyOreFeatureConfigEntries;
import com.petrolpark.destroy.world.DestroyWorldGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Destroy.MOD_ID)
public class Destroy {
    public static final String MOD_ID = "destroy";

    // Utility

    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(MOD_ID);

    public static final CreateRegistrate registrate() {
        return REGISTRATE.get();  
    };

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    };

    // Initiation

    public Destroy() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Mod objects
        DestroyTags.register();
        DestroyBlockEntities.register();
        DestroyBlocks.register();
        DestroyMobEffects.register(modEventBus);
        DestroyItems.register();
        DestroyRecipeTypes.register(modEventBus);
        DestroyParticleTypes.register(modEventBus);
        DestroyFluids.register();
        DestroyOreFeatureConfigEntries.init();
        DestroySoundEvents.register(modEventBus);
        DestroyCropMutations.register();
        DestroyWorldGen.register(modEventBus);

        // Chemistry
        // DestroyGroupFinder.register();
        // DestroyMolecules.register();
        // DestroyReactions.register();
        // DestroyGenericReactions.register();

        // Events
        MinecraftForge.EVENT_BUS.register(this);

        // Config
        DestroyAllConfigs.register(modLoadingContext);

        // Initiation Events
        modEventBus.addListener(Destroy::init);
        modEventBus.addListener(Destroy::clientInit);
        modEventBus.addListener(DestroyParticleTypes::registerProviders);
        modEventBus.addListener(EventPriority.LOWEST, Destroy::gatherData);

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DestroyBlockPartials::init);
    };

    //Initiation Events

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DestroyMessages.register();
        });
        DestroyPotatoCannonProjectileTypes.register();
        HyperaccumulatingFertilizerItem.registerDispenserBehaviour();
    };

    public static void clientInit(final FMLClientSetupEvent event) {
        DestroySceneIndex.register();
    };


    public static void gatherData(GatherDataEvent event) {
        DestroyTags.datagen();
    };
}
