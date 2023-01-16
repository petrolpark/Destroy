package com.petrolpark.destroy;

import com.mojang.logging.LogUtils;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.partial.DestroyBlockPartials;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Destroy.MOD_ID)
public class Destroy {
    public static final String MOD_ID = "destroy";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(MOD_ID);

    public Destroy()
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Mod objects
        DestroyBlockEntities.register();
        DestroyBlocks.register(modEventBus);
        DestroyMobEffects.register(modEventBus);
        DestroyItems.register(modEventBus);
        DestroyRecipeTypes.register(modEventBus);
        DestroyFluids.register();

        // Chemistry
        // DestroyGroupFinder.register();
        // DestroyMolecules.register();
        // DestroyReactions.register();
        // DestroyGenericReactions.register();

        // Events
        MinecraftForge.EVENT_BUS.register(this);

        // Config
        DestroyAllConfigs.register(modLoadingContext);

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DestroyBlockPartials::init);
    };

    public static CreateRegistrate registrate() {
        return REGISTRATE.get();
    };

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    };
}
