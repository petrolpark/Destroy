package com.petrolpark.destroy.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.registrate.BadgeBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

@MoveToPetrolparkLibrary
@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
  
    /**
     * Add Advancements associated with Badges, which aren't defined in datapacks but generated in code.
     */
    @Inject(method = "apply", at = @At("RETURN"))
    public void inApply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler, CallbackInfo ci) {
        ((ServerAdvancementManager)(Object)this).advancements.add(BadgeBuilder.getAdvancements());
    };
};
