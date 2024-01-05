package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.Create;
import com.simibubi.create.AllSoundEvents.SoundEntry;
import com.simibubi.create.AllSoundEvents.SoundEntryBuilder;

import net.minecraft.resources.ResourceLocation;

@Mixin(SoundEntryBuilder.class)
public class SoundEntryBuilderMixin {
    
    @Inject(
        method = "Lcom/simibubi/create/AllSoundEvents$SoundEntryBuilder;build()Lcom/simibubi/create/AllSoundEvents$SoundEntry;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;put"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        cancellable = true,
        remap = false
    )
    public void inBuild(CallbackInfoReturnable<SoundEntry> cir, SoundEntry entry) {
        ResourceLocation id = entry.getId();
        if (id != null && id.getNamespace() != Create.ID) {
            cir.setReturnValue(entry);
            cir.cancel();
        };
    };
};
