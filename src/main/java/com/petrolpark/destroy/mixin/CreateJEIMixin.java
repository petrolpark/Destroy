package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.recipe.DestroyMysteriousItemConversions;
import com.simibubi.create.compat.jei.CreateJEI;

@Mixin(CreateJEI.class)
public class CreateJEIMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/compat/jei/CreateJEI;loadCategories()V",
        at = @At("HEAD"),
        remap = false
    )
    public void inLoadCategories(CallbackInfo ci) {
        DestroyMysteriousItemConversions.register();
    };
};
