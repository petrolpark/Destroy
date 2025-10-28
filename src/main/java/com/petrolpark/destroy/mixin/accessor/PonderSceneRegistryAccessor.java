package com.petrolpark.destroy.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.google.common.collect.Multimap;

import net.createmod.ponder.api.registration.StoryBoardEntry;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.minecraft.resources.ResourceLocation;

@Mixin(PonderSceneRegistry.class)
public interface PonderSceneRegistryAccessor {
    @Accessor(
            value = "scenes",
            remap = false
    )
    public Multimap<ResourceLocation, StoryBoardEntry> getScenes();
}
