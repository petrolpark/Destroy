package com.petrolpark.destroy.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

@Mixin(BasinOperatingBlockEntity.class)
public interface BasinOperatingBlockEntityAccessor {
    
    @Invoker("getRecipeCacheKey")
    public Object invokeGetRecipeCacheKey();

    @Invoker("getBasin")
    public Optional<BasinBlockEntity> invokeGetBasin();
};
