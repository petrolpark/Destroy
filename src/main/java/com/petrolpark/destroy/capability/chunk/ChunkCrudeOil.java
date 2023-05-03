package com.petrolpark.destroy.capability.chunk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
 
public class ChunkCrudeOil {

    public static final long SALT = 5252525252l;

    private boolean generated = false; // Whether the amount of Crude Oil in this Chunk has already been determined
    private int amount; // Amount of Crude Oil in the Chunk in mB

    public void generate(LevelChunk chunk, @Nullable Player player) {
        if (generated) return;
        if (chunk.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = chunk.getPos();
            // Get the seeded randomizer for this level
            RandomSource random = RandomSource.create(level.getSeed() ^ SALT);
            // Generate the noise value for this Chunk
            double value = (PerlinNoise.create(random, -2, 1d).getValue(pos.x * 0.7, pos.z * 0.7, 0));
            // Don't generate any oil if the value is less than a threshold
            amount = value < 0.3d ? 0 : (int)(value * 100000d);
        
            generated = true;
        };
    };

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<ChunkCrudeOil> CHUNK_CRUDE_OIL = CapabilityManager.get(new CapabilityToken<ChunkCrudeOil>() {});

        private ChunkCrudeOil crudeOil = null;
        private final LazyOptional<ChunkCrudeOil> optional = LazyOptional.of(this::createChunkCrudeOil);

        private ChunkCrudeOil createChunkCrudeOil() {
            if (crudeOil == null) {
                crudeOil = new ChunkCrudeOil();
            }
            return crudeOil;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("Generated", createChunkCrudeOil().generated);
            tag.putInt("Amount", createChunkCrudeOil().amount);
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            createChunkCrudeOil().amount = tag.getInt("Amount");
            createChunkCrudeOil().generated = tag.getBoolean("Generated");
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == CHUNK_CRUDE_OIL) {
                return optional.cast();
            };

            return LazyOptional.empty();
        };

    };
};
