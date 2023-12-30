package com.petrolpark.destroy.mixin.accessor;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

@Mixin(BasinOperatingBlockEntity.class)
public interface BasinOperatingBlockEntityAccessor {
    
    @Invoker(
        value = "getRecipeCacheKey",
        remap = false
    )
    public Object invokeGetRecipeCacheKey();

    @Invoker(
        value = "getBasin",
        remap = false
    )
    public Optional<BasinBlockEntity> invokeGetBasin();
};
