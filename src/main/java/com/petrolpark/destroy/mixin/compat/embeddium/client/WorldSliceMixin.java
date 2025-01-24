package com.petrolpark.destroy.mixin.compat.embeddium.client;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WorldSlice.class)
public class WorldSliceMixin {

    @Shadow @Final public ClientLevel world;

    @Overwrite
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        return this.world.getBlockTint(pos, resolver);
    }
}
