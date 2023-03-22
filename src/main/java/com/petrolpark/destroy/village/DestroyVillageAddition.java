package com.petrolpark.destroy.village;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/**
 * Pretty much all copied from <a href="https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.19/blob/38-villageStructure/src/main/java/net/kaupenjoe/tutorialmod/world/village/VillageAddition.java">here</a>.
*/
public class DestroyVillageAddition {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registry.PROCESSOR_LIST_REGISTRY, new ResourceLocation("minecraft", "empty"));
    
    public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, ResourceLocation poolResourceLocation, String nbtPieceResourceLocation, int weight) {
        Holder<StructureProcessorList> emptyProcessorList = processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        StructureTemplatePool pool = templatePoolRegistry.get(poolResourceLocation);
        if (pool == null) return;
        SinglePoolElement piece = SinglePoolElement.legacy(nbtPieceResourceLocation, emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);
        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        };

        // For compatibility safety 
        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    };
}
