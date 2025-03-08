package com.petrolpark.destroy.mixin.accessor;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ValueSettingsScreen.class, remap = false)
public interface ValueSettingsScreenAccessor {

    @Accessor(
            value = "netId",
            remap = false
    )
    int getNetId();
}
