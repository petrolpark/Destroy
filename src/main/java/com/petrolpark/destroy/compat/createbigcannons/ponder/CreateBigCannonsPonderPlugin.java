package com.petrolpark.destroy.compat.createbigcannons.ponder;

import com.petrolpark.destroy.Destroy;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CreateBigCannonsPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return Destroy.MOD_ID;
    }


    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        CreateBigCannonsPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        CreateBigCannonsPonderScenes.registerTags(helper);
    }
}
