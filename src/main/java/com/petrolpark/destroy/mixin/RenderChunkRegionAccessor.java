package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.world.level.Level;

@Mixin(RenderChunkRegion.class)
public interface RenderChunkRegionAccessor {

    @Accessor("level")
    public Level getLevel();
};
