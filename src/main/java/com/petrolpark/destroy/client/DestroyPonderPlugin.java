package com.petrolpark.destroy.client;

import com.petrolpark.destroy.Destroy;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.resources.ResourceLocation;

public class DestroyPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Destroy.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        DestroyPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        DestroyPonderTags.register(helper);
    }
}
