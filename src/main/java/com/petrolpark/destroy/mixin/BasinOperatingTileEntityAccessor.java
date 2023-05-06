package com.petrolpark.destroy.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.contraptions.processing.BasinOperatingTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;

@Mixin(BasinOperatingTileEntity.class)
public interface BasinOperatingTileEntityAccessor {
    
    @Invoker("getRecipeCacheKey")
    public Object invokeGetRecipeCacheKey();

    @Invoker("getBasin")
    public Optional<BasinTileEntity> invokeGetBasin();
};
