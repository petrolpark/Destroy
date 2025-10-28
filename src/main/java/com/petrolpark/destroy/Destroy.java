package com.petrolpark.destroy;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.destroy.chemistry.api.Chemistry;
import com.petrolpark.destroy.chemistry.forge.event.ForgeChemistryEventFirer;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGenericReactions;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyGroupFinder;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyReactions;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyTopologies;
import com.petrolpark.destroy.client.DestroyMenuTypes;
import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.petrolpark.destroy.compat.createbigcannons.CreateBigCannons;
import com.petrolpark.destroy.compat.curios.DestroyCurios;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitPatternHandler;
import com.petrolpark.destroy.content.processing.trypolithography.CircuitPuncherHandler;
import com.petrolpark.destroy.core.chemistry.hazard.ContaminatedItemTooltipModifier;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;
import com.petrolpark.destroy.core.data.DestroyTagDatagen;
import com.petrolpark.destroy.core.item.tooltip.IDynamicItemDescription;
import com.petrolpark.destroy.core.item.tooltip.TempramentalItemDescription;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(Destroy.MOD_ID)
public class Destroy {
    public static final String MOD_ID = "destroy";
    public static final String NAME = "Destroy";

    // Utility
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean datagen = false;

    // Registrate
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    // Level-attached managers
    public static final CircuitPuncherHandler CIRCUIT_PUNCHER_HANDLER = new CircuitPuncherHandler();
    public static final CircuitPatternHandler CIRCUIT_PATTERN_HANDLER = new CircuitPatternHandler();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    // Really early stuff
    static {
        // Tooltips
		REGISTRATE.setTooltipModifierFactory(item -> {
			return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
				.andThen(TooltipModifier.mapNull(KineticStats.create(item)))
                .andThen(TooltipModifier.mapNull(IDynamicItemDescription.create(item)))
                .andThen(new TempramentalItemDescription())
                .andThen(new ContaminatedItemTooltipModifier());
		});
	};

    // Initiation
    public Destroy() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);

        // Mod objects
        if (!datagen) DestroySoundEvents.prepare(); // Sound datagen is broken and I can't be bothered to fix it
        DestroyCreativeModeTabs.register(modEventBus);
        DestroyTags.register();
        DestroyBlockEntityTypes.register();
        DestroyBlocks.register();
        DestroyMobEffects.register(modEventBus);
        DestroyPotions.register(modEventBus);
        DestroyItems.register();
        DestroyMenuTypes.register();
        DestroyRecipeTypes.register(modEventBus);
        DestroyParticleTypes.register(modEventBus);
        DestroyFluids.register();
        DestroyCropMutations.register();
        DestroyEntityTypes.register();
        DestroyVillagers.register(modEventBus);
        DestroyLoot.register(modEventBus);
        DestroyDamageTypes.register();
        DestroyStats.register(modEventBus);
        DestroyAttributes.register(modEventBus);
        DestroyMovementChecks.register();
        DestroyDisplaySources.register();

        // Events
        MinecraftForge.EVENT_BUS.register(this);

        // Config
        DestroyAllConfigs.register(modLoadingContext);

        // Initiation Events
        modEventBus.addListener(Destroy::init);
        modEventBus.addListener(Destroy::onRegister);
        if (!datagen) modEventBus.addListener(DestroySoundEvents::register);
        modEventBus.addListener(DestroyClient::clientInit);
        modEventBus.addListener(EventPriority.LOWEST, Destroy::gatherData);

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DestroyClient.clientCtor(modEventBus, forgeEventBus));

        // Optional compatibility mods. According to the Create main class doing the same thing, this isn't thread safe
        CompatMods.BIG_CANNONS.executeIfInstalled(() -> () -> CreateBigCannons.init(modEventBus, forgeEventBus));
        CompatMods.CURIOS.executeIfInstalled(() -> () -> DestroyCurios.init(modEventBus, forgeEventBus));
    };

    // Initiation Events

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Chemistry.initiate(new ForgeChemistryEventFirer(), Destroy.LOGGER::info);
            DestroyMessages.register();
            DestroyCompostables.register();
        });
        DestroyStats.register();
        VatMaterial.registerDestroyVatMaterials();
        DestroyOpenEndedPipeEffectHandlers.register();
        DestroyAdvancementTrigger.register();
        DestroyBlockExtrusions.register();

        // Chemistry
        DestroyGroupFinder.register();
        DestroyTopologies.register();
        DestroyMolecules.register();
        DestroyReactions.register();
        DestroyGenericReactions.register();

        // Config
        GogglesItem.addIsWearingPredicate(player -> player.isCreative() && DestroyAllConfigs.SERVER.automaticGoggles.get());
    };


    public static void onRegister(final RegisterEvent event) {
        DestroyItemAttributeTypes.init();
        DestroyPotatoProjectileEntityHitActions.init();
        DestroyPotatoProjectileBlockHitActions.init();
    }

    // Datagen
    public static void gatherData(GatherDataEvent event) {
        DestroyTagDatagen.addGenerators();
    };
};
