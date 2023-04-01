package com.petrolpark.destroy.world.village;

import java.lang.reflect.InvocationTargetException;

import com.google.common.collect.ImmutableSet;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DestroyVillagers {

    private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, Destroy.MOD_ID);
    private static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, Destroy.MOD_ID);

    public static final RegistryObject<PoiType> AGING_BARREL_POI = POI_TYPES.register("aging_barrel_poi", () -> new PoiType(ImmutableSet.copyOf(DestroyBlocks.AGING_BARREL.get().getStateDefinition().getPossibleStates()), 1, 1));
    
    public static final RegistryObject<VillagerProfession> INNKEEPER = VILLAGER_PROFESSIONS.register("innkeeper", () -> new VillagerProfession(
        "innkeeper",
        poi -> poi.get() == AGING_BARREL_POI.get(),
        poi -> poi.get() == AGING_BARREL_POI.get(),
        ImmutableSet.of(),
        ImmutableSet.of(),
        SoundEvents.VILLAGER_WORK_SHEPHERD
    ));
    
    public static void registerPOIs() {
        try {
            ObfuscationReflectionHelper.findMethod(PoiType.class, "registerBlockStates", PoiType.class).invoke(null, AGING_BARREL_POI.get());
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        };
    };
    
    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    };

};
