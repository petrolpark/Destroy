package com.petrolpark.destroy.client;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.content.oil.OilPonderScenes;
import com.petrolpark.destroy.content.processing.ProcessingPonderScenes;
import com.petrolpark.destroy.content.processing.dynamo.DynamoPonderScenes;
import com.petrolpark.destroy.content.processing.trypolithography.TrypolithographyPonderScenes;
import com.petrolpark.destroy.content.product.periodictable.PeriodicTableBlock;
import com.petrolpark.destroy.core.chemistry.ChemistryPonderScenes;
import com.petrolpark.destroy.core.explosion.ExplosivesPonderScenes;
import com.petrolpark.destroy.core.pollution.PollutionPonderScenes;
import com.petrolpark.destroy.mixin.accessor.PonderSceneRegistryAccessor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.simibubi.create.infrastructure.ponder.scenes.fluid.PumpScenes;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class DestroyPonderScenes {

    private static PonderSceneRegistrationHelper<ResourceLocation> helper = null;
    private static PonderSceneRegistrationHelper<ItemProviderEntry<?>> CREATE_HELPER = null;

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        DestroyPonderScenes.helper = helper;
        PonderSceneRegistrationHelper<ItemProviderEntry<?>>HELPER = helper.withKeyFunction(RegistryEntry::getId);
        CREATE_HELPER = helper.withKeyFunction((s) -> ResourceLocation.tryParse(Create.ID));

        // Aging Barrel
        HELPER.forComponents(DestroyBlocks.AGING_BARREL)
            .addStoryBoard("processing/aging_barrel", ProcessingPonderScenes::agingBarrel);

        // Basin
        HELPER.forComponents(AllBlocks.BASIN)
            .addStoryBoard("reactions", ChemistryPonderScenes::reactions, DestroyPonderTags.CHEMISTRY)
            .addStoryBoard("pollution/basins_and_vats", PollutionPonderScenes::basinsAndVats);

        // Blacklight
        HELPER.forComponents(DestroyBlocks.BLACKLIGHT)
            .addStoryBoard("vat/uv", ChemistryPonderScenes::vatUVWithBlackLight);

        // Blaze Burner
        HELPER.forComponents(AllBlocks.BLAZE_BURNER)
            .addStoryBoard("vat/interaction", DestroyMiscPonderScenes::vatInteraction, DestroyPonderTags.CHEMISTRY);

        // Blowpipe
        HELPER.forComponents(DestroyBlocks.BLOWPIPE)
            .addStoryBoard("processing/blowpipe", ProcessingPonderScenes::blowpipe)
            .addStoryBoard("processing/blowpipe_automation", ProcessingPonderScenes::blowpipeAutomation);

        // Bubble Cap
        HELPER.forComponents(DestroyBlocks.BUBBLE_CAP)
            .addStoryBoard("processing/bubble_cap/generic", ProcessingPonderScenes::bubbleCapGeneric)
            .addStoryBoard("processing/bubble_cap/mixtures", ProcessingPonderScenes::bubbleCapMixtures, DestroyPonderTags.CHEMISTRY)
            .addStoryBoard("pollution/room_temperature", ChemistryPonderScenes::roomTemperature);

        // Catalytic Converter
        HELPER.forComponents(DestroyBlocks.CATALYTIC_CONVERTER)
            .addStoryBoard("pollution/catalytic_converter", PollutionPonderScenes::catalyticConverter);

        // Centrifuge
        HELPER.forComponents(DestroyBlocks.CENTRIFUGE)
            .addStoryBoard("processing/centrifuge/generic", ProcessingPonderScenes::centrifugeGeneric)
            .addStoryBoard("processing/centrifuge/mixture", ProcessingPonderScenes::centrifugeMixture, DestroyPonderTags.CHEMISTRY);

        // Colorimeter
        HELPER.forComponents(DestroyBlocks.COLORIMETER)
            .addStoryBoard("colorimeter", ChemistryPonderScenes::colorimeter);

        // Cooler
        HELPER.forComponents(DestroyBlocks.COOLER)
            .addStoryBoard("processing/cooler", ProcessingPonderScenes::cooler)
            .addStoryBoard("vat/temperature", ChemistryPonderScenes::vatTemperature, DestroyPonderTags.CHEMISTRY);

        // Creative Pump
        CREATE_HELPER.forComponents(DestroyBlocks.CREATIVE_PUMP)
            .addStoryBoard("mechanical_pump/flow", PumpScenes::flow);

        // Custom Explosive Mix
        HELPER.forComponents(DestroyBlocks.CUSTOM_EXPLOSIVE_MIX)
            .addStoryBoard("explosives/custom_explosive_mix", (s, u) -> ExplosivesPonderScenes.filling(s, u, DestroyBlocks.CUSTOM_EXPLOSIVE_MIX::asStack))
            .addStoryBoard("explosives/custom_explosive_mix_explosion", ExplosivesPonderScenes::exploding)
            .addStoryBoard("explosives/custom_explosive_mix", (s, u) -> ExplosivesPonderScenes.dyeing(s, u, DestroyBlocks.CUSTOM_EXPLOSIVE_MIX::asStack))
            .addStoryBoard("explosives/custom_explosive_mix", ExplosivesPonderScenes::naming);
        
        // Dynamo
        HELPER.forComponents(DestroyBlocks.DYNAMO)
            .addStoryBoard("processing/dynamo/redstone", DynamoPonderScenes::dynamoRedstone)
            .addStoryBoard("processing/dynamo/charging", DynamoPonderScenes::dynamoCharging, AllCreatePonderTags.KINETIC_APPLIANCES)
            .addStoryBoard("processing/dynamo/electrolysis", DynamoPonderScenes::dynamoElectrolysis)
            .addStoryBoard("processing/dynamo/arc_furnace", DynamoPonderScenes::arcFurnace);

        // Extrusion Die
        HELPER.forComponents(DestroyBlocks.EXTRUSION_DIE)
            .addStoryBoard("processing/extrusion_die", ProcessingPonderScenes::extrusionDie);

        // Hyperaccumulating Fertilizer
        HELPER.forComponents(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .addStoryBoard("processing/phytomining", ProcessingPonderScenes::phytomining)
            .addStoryBoard("pollution/crop_growth_failure", PollutionPonderScenes::cropGrowthFailure);

        // Circuit Mask and Keypunch
        HELPER.forComponents(DestroyItems.CIRCUIT_MASK, DestroyBlocks.KEYPUNCH)
            .addStoryBoard("trypolithography/intro", TrypolithographyPonderScenes::intro)
            .addStoryBoard("trypolithography/rotating", TrypolithographyPonderScenes::rotating)
            .addStoryBoard("trypolithography/flipping", TrypolithographyPonderScenes::flipping);

        // Mechanical Mixer
        HELPER.forComponents(AllBlocks.MECHANICAL_MIXER)
            .addStoryBoard("reactions", DestroyMiscPonderScenes::reactions, DestroyPonderTags.CHEMISTRY)
            .addStoryBoard("pollution/basins_and_vats", PollutionPonderScenes::basinsAndVats);

        // Mechanical Sieve
        HELPER.forComponents(DestroyBlocks.MECHANICAL_SIEVE)
            .addStoryBoard("processing/mechanical_sieve", ProcessingPonderScenes::mechanicalSieve);
        
        // Pollution
        HELPER.forComponents(DestroyItems.POLLUTION_SYMBOL)
            .addStoryBoard("pollution/tanks", PollutionPonderScenes::pipesAndTanks)
            .addStoryBoard("pollution/basins_and_vats", PollutionPonderScenes::basinsAndVats)
            .addStoryBoard("pollution/smog", PollutionPonderScenes::smog)
            .addStoryBoard("pollution/crop_growth_failure", PollutionPonderScenes::cropGrowthFailure)
            .addStoryBoard("pollution/fishing_failure", PollutionPonderScenes::fishingFailure)
            .addStoryBoard("blank_3x3", PollutionPonderScenes::breedingFailure)
            .addStoryBoard("pollution/smog", PollutionPonderScenes::villagerPriceIncrease)
            .addStoryBoard("pollution/cancer", PollutionPonderScenes::cancer)
            .addStoryBoard("vat/uv", ChemistryPonderScenes::vatUVWithoutBlackLight)
            .addStoryBoard("pollution/smog", PollutionPonderScenes::acidRain)
            .addStoryBoard("pollution/room_temperature", ChemistryPonderScenes::roomTemperature)
            .addStoryBoard("pollution/reduction", PollutionPonderScenes::reduction)
            .addStoryBoard("blank_3x3", PollutionPonderScenes::lightning)
            .addStoryBoard("pollution/catalytic_converter", PollutionPonderScenes::catalyticConverter);

        // Pumpjack
        HELPER.forComponents(DestroyBlocks.PUMPJACK)
            .addStoryBoard("oil/seismometer", OilPonderScenes::seismometer)
            .addStoryBoard("oil/seismograph", OilPonderScenes::seismograph)
            .addStoryBoard("oil/pumpjack", OilPonderScenes::pumpjack, AllCreatePonderTags.KINETIC_APPLIANCES);

        // Redstone Programmer
        HELPER.forComponents(DestroyBlocks.REDSTONE_PROGRAMMER)
            .addStoryBoard("redstone_programmer", DestroyMiscPonderScenes::redstoneProgrammer);

        // Seismograph
        HELPER.forComponents(DestroyItems.SEISMOGRAPH)
            .addStoryBoard("oil/seismometer", OilPonderScenes::seismometer)
            .addStoryBoard("oil/seismograph", OilPonderScenes::seismograph);

        // Seismometer
        HELPER.forComponents(DestroyItems.SEISMOMETER)
            .addStoryBoard("oil/seismometer", OilPonderScenes::seismometer)
            .addStoryBoard("oil/seismograph", OilPonderScenes::seismograph);

        // Siphon
        HELPER.forComponents(DestroyBlocks.SIPHON)
            .addStoryBoard("processing/siphon", ProcessingPonderScenes::siphon);

        // Tree Tap
        HELPER.forComponents(DestroyBlocks.TREE_TAP)      
            .addStoryBoard("processing/tree_tap", ProcessingPonderScenes::treeTap);

        // Vat Controller
        HELPER.forComponents(DestroyBlocks.VAT_CONTROLLER)
            .addStoryBoard("vat/construction", ChemistryPonderScenes::vatConstruction)
            .addStoryBoard("vat/fluids", ChemistryPonderScenes::vatFluids, AllCreatePonderTags.FLUIDS)
            .addStoryBoard("vat/items", ChemistryPonderScenes::vatItems)
            .addStoryBoard("reactions", ChemistryPonderScenes::reactions, DestroyPonderTags.CHEMISTRY)
            .addStoryBoard("vat/temperature", ChemistryPonderScenes::vatTemperature)
            //.addStoryBoard("bunsen_burner", ChemistryScenes::bunsenBurner)
            .addStoryBoard("pollution/room_temperature", ChemistryPonderScenes::roomTemperature)
            .addStoryBoard("vat/pressure", ChemistryPonderScenes::vatPressure)
            .addStoryBoard("pollution/basins_and_vats", PollutionPonderScenes::basinsAndVats)
            .addStoryBoard("vat/reading", ChemistryPonderScenes::vatReading)
            .addStoryBoard("colorimeter", ChemistryPonderScenes::colorimeter)
            .addStoryBoard("vat/uv", ChemistryPonderScenes::vatUVWithBlackLight);
    };

    private static final ResourceLocation periodicTableSchematicLocation = Destroy.asResource("periodic_table");

    @SuppressWarnings("deprecation")
    public static void refreshPeriodicTableBlockScenes() {
        PeriodicTableBlock.ELEMENTS.forEach(entry -> {
            entry.blocks().forEach(block -> {
                ResourceLocation rl = BuiltInRegistries.ITEM.getKey(block.asItem());
                if(PonderIndex.getSceneAccess().doScenesExistForId(rl)) {
                    ((PonderSceneRegistryAccessor) PonderIndex.getSceneAccess()).getScenes().get(rl).removeIf(storyBoard -> storyBoard.getSchematicLocation().equals(periodicTableSchematicLocation));
                }

                helper.forComponents(rl).addStoryBoard(periodicTableSchematicLocation, ChemistryPonderScenes::periodicTable);
            });
        });
    };
};
