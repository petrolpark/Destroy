package com.petrolpark.destroy;

import com.mojang.logging.LogUtils;
import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.badge.Badge;
import com.petrolpark.destroy.badge.DestroyBadges;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.model.DestroyPartials;
import com.petrolpark.destroy.chemistry.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.index.DestroyReactions;
import com.petrolpark.destroy.chemistry.index.DestroyTopologies;
import com.petrolpark.destroy.client.particle.DestroyParticleTypes;
import com.petrolpark.destroy.client.ponder.DestroyPonderIndex;
import com.petrolpark.destroy.client.ponder.DestroyPonderTags;
import com.petrolpark.destroy.compat.jei.DestroyJEI;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.entity.DestroyEntityTypes;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.fluid.pipeEffectHandler.DestroyOpenEndedPipeEffects;
import com.petrolpark.destroy.item.DestroyItemProperties;
import com.petrolpark.destroy.item.DestroyItems;
import com.petrolpark.destroy.item.compostable.DestroyCompostables;
import com.petrolpark.destroy.item.creativeModeTab.DestroyCreativeModeTabs;
import com.petrolpark.destroy.item.potatoCannonProjectileType.DestroyPotatoCannonProjectileTypes;
import com.petrolpark.destroy.item.tooltip.ContaminatedItemDescription;
import com.petrolpark.destroy.item.tooltip.IDynamicItemDescription;
import com.petrolpark.destroy.item.tooltip.TempramentalItemDescription;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.recipe.DestroyCropMutations;
import com.petrolpark.destroy.recipe.DestroyExtrusions;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.registrate.DestroyRegistrate;
import com.petrolpark.destroy.sound.DestroySoundEvents;
import com.petrolpark.destroy.util.DestroyTags;
import com.petrolpark.destroy.util.vat.VatMaterial;
import com.petrolpark.destroy.world.damage.DestroyDamageTypes;
import com.petrolpark.destroy.world.loot.DestroyLoot;
import com.petrolpark.destroy.world.village.DestroyVillagers;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryBuilder;

import org.slf4j.Logger;

@Mod(Destroy.MOD_ID)
public class Destroy {
    public static final String MOD_ID = "destroy";

    // Utility
    public static final Logger LOGGER = LogUtils.getLogger();

    @MoveToPetrolparkLibrary
    public static final DestroyRegistrate PETROLPARK_REGISTRATE = new DestroyRegistrate("petrolpark");
    public static final DestroyRegistrate REGISTRATE = new DestroyRegistrate(MOD_ID);

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    };

    // Tooltips
    static {
		REGISTRATE.setTooltipModifierFactory(item -> {
			return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                .andThen(TooltipModifier.mapNull(IDynamicItemDescription.create(item)))
                .andThen(new TempramentalItemDescription())
                .andThen(new ContaminatedItemDescription());
		});
	};

    // Registries
    public static ResourceKey<Registry<Badge>> BADGE_REGISTRY_KEY = PETROLPARK_REGISTRATE.makeRegistry("badge", RegistryBuilder::new);

    // Initiation
    public Destroy() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        PETROLPARK_REGISTRATE.registerEventListeners(modEventBus);
        REGISTRATE.registerEventListeners(modEventBus);

        // Mod objects
        DestroySoundEvents.prepare();
        DestroyBadges.register();
        DestroyCreativeModeTabs.register(modEventBus);
        DestroyTags.register();
        DestroyBlockEntityTypes.register();
        DestroyBlocks.register();
        DestroyMobEffects.register(modEventBus);
        DestroyItems.register();
        DestroyRecipeTypes.register(modEventBus);
        DestroyParticleTypes.register(modEventBus);
        DestroyFluids.register();
        DestroyCropMutations.register();
        DestroyEntityTypes.register();
        DestroyVillagers.register(modEventBus);
        DestroyLoot.register(modEventBus);
        DestroyDamageTypes.register();

        // Events
        MinecraftForge.EVENT_BUS.register(this);

        // Config
        DestroyAllConfigs.register(modLoadingContext);

        // Initiation Events
        modEventBus.addListener(Destroy::init);
        modEventBus.addListener(DestroySoundEvents::register);
        modEventBus.addListener(Destroy::clientInit);
        modEventBus.addListener(EventPriority.LOWEST, Destroy::gatherData);

        // JEI compat
        if (FMLLoader.getLoadingModList().getModFileById("jei") != null) {
            forgeEventBus.register(DestroyJEI.ClientEvents.class);
        };

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientCtor(modEventBus, forgeEventBus));
    };

    //Initiation Events

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DestroyMessages.register();
            DestroyCompostables.register();
        });
        VatMaterial.registerDestroyVatMaterials();
        DestroyOpenEndedPipeEffects.register();
        DestroyAdvancements.register();
        DestroyPotatoCannonProjectileTypes.register();
        DestroyExtrusions.register();

        // Chemistry
        DestroyGroupFinder.register();
        DestroyTopologies.register();
        DestroyMolecules.register();
        DestroyReactions.register();
        DestroyGenericReactions.register();

        // Config
        GogglesItem.addIsWearingPredicate(player -> player.isCreative() && DestroyAllConfigs.COMMON.automaticGoggles.get());
    };

    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DestroyItemProperties.register();
        });
        DestroyPonderTags.register();
        DestroyPonderIndex.register();
    };

    public static void clientCtor(IEventBus modEventBus, IEventBus forgeEventBus) {
        DestroyPartials.init();
        modEventBus.addListener(DestroyParticleTypes::registerProviders);
    };

    public static void gatherData(GatherDataEvent event) {
    };
}
