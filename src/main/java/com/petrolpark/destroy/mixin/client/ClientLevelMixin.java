package com.petrolpark.destroy.mixin.client;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.block.color.SmogAffectedBlockColor;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed,int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        throw new AssertionError();
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void inInit(ClientPacketListener pConnection, ClientLevel.ClientLevelData pClientLevelData, ResourceKey<Level> pDimension, Holder<DimensionType> pDimensionType, int pViewDistance, int pServerSimulationDistance, Supplier<ProfilerFiller> pProfiler, LevelRenderer pLevelRenderer, boolean pIsDebug, long pBiomeZoomSeed, CallbackInfo ci) {
        getTintCaches().put(SmogAffectedBlockColor.GRASS_COLOR_RESOLVER, new BlockTintCache(pos -> {
            return thisClientLevel().calculateBlockTint(pos, (b, x, z) -> {
                BlockPos nextPos = new BlockPos((int)x, pos.getY(), (int)z);
                return SmogAffectedBlockColor.getColor(thisClientLevel().getBlockTint(nextPos, BiomeColors.GRASS_COLOR_RESOLVER), nextPos, thisClientLevel());
            });
        }));
        getTintCaches().put(SmogAffectedBlockColor.FOLIAGE_COLOR_RESOLVER, new BlockTintCache(pos -> {
            return thisClientLevel().calculateBlockTint(pos, (b, x, z) -> {
                BlockPos nextPos = new BlockPos((int)x, pos.getY(), (int)z);
                return SmogAffectedBlockColor.getColor(thisClientLevel().getBlockTint(nextPos, BiomeColors.FOLIAGE_COLOR_RESOLVER), nextPos, thisClientLevel());
            });
        }));
        getTintCaches().put(SmogAffectedBlockColor.WATER_COLOR_RESOLVER, new BlockTintCache(pos -> {
            return thisClientLevel().calculateBlockTint(pos, (b, x, z) -> {
                BlockPos nextPos = new BlockPos((int)x, pos.getY(), (int)z);
                return SmogAffectedBlockColor.getColor(thisClientLevel().getBlockTint(nextPos, BiomeColors.WATER_COLOR_RESOLVER), nextPos, thisClientLevel());
            });
        }));
    };

    private ClientLevel thisClientLevel() {
        return (ClientLevel)(Object)this;
    };

    @Accessor("tintCaches")
    public abstract Object2ObjectArrayMap<ColorResolver, BlockTintCache> getTintCaches();
    
};
