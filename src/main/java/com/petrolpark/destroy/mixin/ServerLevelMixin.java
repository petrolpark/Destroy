package com.petrolpark.destroy.mixin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.pollution.Pollution;
import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.petrolpark.destroy.core.pollution.SyncChunkPollutionS2CPacket;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;
import net.createmod.catnip.data.Couple;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    protected ServerLevelMixin(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
        throw new AssertionError();
    };

    @Inject(
        method = "tickChunk",
        at = @At("RETURN")
    )
    public void inTickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (!PollutionHelper.pollutionEnabled()) return;
        ChunkPos pos = chunk.getPos();
        chunk.getCapability(Pollution.CAPABILITY).ifPresent(pollution -> {

            // Decrease pollution in this chunk
            for (PollutionType pollutionType : PollutionType.values()) if (pollutionType.local && getRandom().nextFloat() <= DestroyAllConfigs.SERVER.pollution.pollutionDecreaseRates.get(pollutionType).getF()) pollution.change(pollutionType, -1);
            
            // Spread pollution to adjacent chunks
            if (Math.abs(pos.x) % 2 != Math.abs(pos.z) % 2) return; // Only spread to/from chunks in a checkerboard fashion, as it is really the Chunk boundaries we want to tick
            List<ChunkPos> adjacentPositions = new ArrayList<>(List.of(new ChunkPos(pos.x - 1, pos.z), new ChunkPos(pos.x + 1, pos.z), new ChunkPos(pos.x, pos.z - 1), new ChunkPos(pos.x, pos.z + 1)));
            Collections.shuffle(adjacentPositions);
            for (ChunkPos otherPos : adjacentPositions) {
                LevelChunk otherChunk = (LevelChunk)getChunk(otherPos.x, otherPos.z, ChunkStatus.FULL, false);
                if (otherChunk == null) continue;
                otherChunk.getCapability(Pollution.CAPABILITY).ifPresent(otherPollution -> {
                    boolean changeOccured = false;
                    spreadEachType: for (PollutionType pollutionType : PollutionType.values()) {
                        if (!pollutionType.local || getRandom().nextFloat() >= DestroyAllConfigs.SERVER.pollution.pollutionSpreadingRates.get(pollutionType).getF()) continue spreadEachType;
                        Couple<Pollution> pollutions = Couple.create(pollution, otherPollution); // The first is the more polluted chunk
                        if (otherPollution.get(pollutionType) > pollution.get(pollutionType)) pollutions = pollutions.swap();
                        int transfer = (int)(0.5f + (pollutions.getFirst().get(pollutionType) - pollutions.getSecond().get(pollutionType)) * 0.5f * DestroyAllConfigs.SERVER.pollution.pollutionSpreadingAmounts.get(pollutionType).getF());
                        if (transfer == 0) continue spreadEachType;
                        pollutions.getFirst().change(pollutionType, -transfer);
                        pollutions.getSecond().change(pollutionType, transfer);
                        changeOccured = true;
                    };
                    if (changeOccured) DestroyMessages.sendToClientsTrackingChunk(new SyncChunkPollutionS2CPacket(otherPos, otherPollution), otherChunk);
                });
            };
            
            // Sync to Clients
            DestroyMessages.sendToClientsTrackingChunk(new SyncChunkPollutionS2CPacket(pos, pollution), chunk);
        });
    };
};
