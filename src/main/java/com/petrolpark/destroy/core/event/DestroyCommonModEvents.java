package com.petrolpark.destroy.core.event;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAttributes;
import com.petrolpark.destroy.content.processing.trypolithography.RegenerateCircuitPatternCommand.CircuitPatternIdArgument;
import com.petrolpark.destroy.content.processing.trypolithography.recipe.CircuitDeployerApplicationRecipe;
import com.petrolpark.destroy.content.processing.trypolithography.recipe.CircuitPatternIngredient;

import com.simibubi.create.foundation.pack.ModFilePackResources;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DestroyCommonModEvents {
    
    @SubscribeEvent
    public static final void onCreateAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, DestroyAttributes.EXTRA_INVENTORY_SIZE.get());
        event.add(EntityType.PLAYER, DestroyAttributes.EXTRA_HOTBAR_SLOTS.get());
    };

    /**
     * Copied from the {@link com.simibubi.create.events.CommonEvents.ModBusEvents#addPackFinders Create source code}.
     * Add the Schematicannon Tooltip resource pack, which replaces the text of tooltips in Schematicannons
     * to reflect that they can accept any Destroy explosive, not just gunpowder.
     */
    @SubscribeEvent
    public static final void addPackFinders(AddPackFindersEvent event) {
        IModFileInfo modFileInfo = ModList.get().getModFileById(Destroy.MOD_ID);
        if (modFileInfo == null) {
            Destroy.LOGGER.error("Could not find Destroy mod file info; built-in resource packs will be missing!");
            return;
        };
        IModFile modFile = modFileInfo.getFile();
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            // Resource packs
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(Destroy.asResource("create_patches").toString(), Component.literal("Destroy Patches For Create"), true, id -> new ModFilePackResources(id, modFile, "resourcepacks/create_patches"), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
                if (pack != null) consumer.accept(pack);
            });
        } else {
            // Datapacks
            event.addRepositorySource(consumer -> {
                Pack pack = Pack.readMetaAndCreate(Destroy.asResource("tfmg_compat").toString(), Component.literal("Destroy Compat With Create: TFMG"), false, id -> new ModFilePackResources(id, modFile, "datapacks/tfmg_compat"), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.DEFAULT);
                if (pack != null) consumer.accept(pack);
            });
        };
    };

    @SubscribeEvent
    public static final void registerIngredientTypes(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            // Ingredient types
            CraftingHelper.register(Destroy.asResource("circuit_pattern_item"), CircuitPatternIngredient.SERIALIZER);
            CraftingHelper.register(Destroy.asResource("example_circuit_mask"), CircuitDeployerApplicationRecipe.ExampleMaskIngredient.SERIALIZER); // Should never actually appear in a JSON recipe
        };
    };

    @SubscribeEvent
    public static final void registerCommandArgumentTypes(RegisterEvent event) {
        event.register(Registries.COMMAND_ARGUMENT_TYPE, Destroy.asResource("circuit_pattern_resource_location"), () -> {
            return ArgumentTypeInfos.registerByClass(CircuitPatternIdArgument.class, SingletonArgumentInfo.contextFree(CircuitPatternIdArgument::create));
        });
    };
};
