package com.petrolpark.destroy.content.oil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem;

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

    /**
     * Get the amount of oil generated in this chunk. This does not account for if oil has been pumped out.
     * @param level
     * @param chunkX
     * @param chunkZ
     */
    public static int getTheoreticalOil(ServerLevel level, int chunkX, int chunkZ) {
        // Get the seeded randomizer for this level
        RandomSource random = RandomSource.create(level.getSeed() ^ SALT);
        // Generate the noise value for this Chunk
        double value = (PerlinNoise.create(random, -2, 1d).getValue(chunkX * 1.5d, chunkZ * 1.5d, 0));
        // Don't generate any oil if the value is less than a threshold
        return value < 0.3d ? 0 : (int)(value * 10000000d);
    };

    /**
     * Check whether a chunk would theoretically have any oil, regardless of whether its been pumped out.
     * @param level
     * @param chunkX
     * @param chunkZ
     * @return Whether the amount of oil in the chunk is greater than {@code 0}.
     */
    public static boolean hasOil(ServerLevel level, int chunkX, int chunkZ) {
        return getTheoreticalOil(level, chunkX, chunkZ) > 0;
    };

    /**
     * To make prospecting a bit less straightforward, some random chunks without oil also show as 'seismically active'. This generates,
     * completely randomly, some chunks to show as seismically active. This does not filter for false positives.
     * @param level
     * @param chunkX
     * @param chunkY
     * @return {@code true} for roughly a quarter of all chunks
     */
    public static boolean randomSeismicActivity(ServerLevel level, int chunkX, int chunkZ) {
        RandomSource random = RandomSource.create(level.getSeed() ^ SALT ^ chunkX ^ chunkZ);
        random.nextInt();
        boolean signal = random.nextInt(4) == 0;
        return signal;
    };

    private static boolean debug = false;

    /**
     * Get the 'signals' in a line (the "long axis") which are used for the seismograph nonogram.
     * @param level
     * @param chunkX Any chunk co-ordinate - the line of chunks will always start on a multiple of eight
     * @param chunkZ See above
     * @param xNotZ {@code true} if the long axis is X, {@code false} if its Z
     * @return A byte where each bit is {@code 1} if we show a signal on that chunk, starting on the multiple of eight and ascending
     */
    public static byte getSignals(ServerLevel level, int chunkX, int chunkZ, boolean xNotZ) {
        boolean[][] oil = new boolean[10][3];
        boolean[][] redHerring = new boolean[10][3];
        int widthAxis = xNotZ ? chunkZ : chunkX;
        int lengthAxis = xNotZ ? chunkX : chunkZ;
        for (int width = 0; width < 3; width++) {
            for (int length = 0; length < 10; length++) {
                int lengthCoordinate = SeismographItem.mapChunkLowerCorner(lengthAxis) - 1 + length;
                int widthCoordinate = widthAxis - 1 + width;
                int x = xNotZ ? lengthCoordinate : widthCoordinate;
                int z = xNotZ ? widthCoordinate : lengthCoordinate;
                oil[length][width] = hasOil(level, x, z);
                redHerring[length][width] = randomSeismicActivity(level, x, z);
            };
        };
        byte signals = 0;
        for (int length = 1; length <= 8; length++) {
            boolean oilInSurroundings = false; // Start by assuming we have red herrings on all eight sides
            boolean surroundedByHerrings = true; // Whether we have oil or an adjacent one does
            for (int lengthOffset = -1; lengthOffset <= 1; lengthOffset++) {
                checkAllSides: for (int widthOffset = -1; widthOffset <= 1; widthOffset++) {
                    if (lengthOffset != 0 && widthOffset != 0) continue checkAllSides; // Don't check corners
                    if (oil[length + lengthOffset][1 + widthOffset]) oilInSurroundings = true;
                    if (!redHerring[length + lengthOffset][1 + widthOffset] && lengthOffset != 0 && widthOffset != 0) surroundedByHerrings = false; // If there's anything which isn't a red herring to our side (not including ourselves)
                    if (oilInSurroundings) break checkAllSides; // If there's nothing more to look for, stop looking
                };
            };

            /* 
             * If there is any oil, either in us or an adjacent chunk, this chunk should show seismic activity.
             * If we are surrounded on four sides by red herrings, we don't want to show this too as that would lead to a false positive (plusses indicate oil).
             * Otherwise, if we are a red herring, show that.
             */
            if (oilInSurroundings || (!surroundedByHerrings && redHerring[length][1])) {
                signals |= 1 << (length - 1);
            };
            
        };
        if (debug) {
            Destroy.LOGGER.info("Oil: ");
            for (int i = 0; i <= 2; i++) {
                String string = "";
                for (boolean[] slice : oil) {
                    string += slice[i] ? "O" : "_";
                };
                Destroy.LOGGER.info((xNotZ ? " X " : " Z ") + string);
            };
            Destroy.LOGGER.info("Herrings: ");
            for (int i = 0; i <= 2; i++) {
                String string = "";
                for (boolean[] slice : redHerring) {
                    string += slice[i] ? "O" : "_";
                };
                Destroy.LOGGER.info((xNotZ ? " X " : " Z ") + string);
            };
            String string = Integer.toBinaryString(signals);
            string = string.substring(Math.max(0, string.length() - 8));
            Destroy.LOGGER.info((xNotZ ? " X " : " Z ") + "signals: "+string);
        };
        return signals;
    };

    public void generate(LevelChunk chunk, @Nullable Player player) {
        if (generated) return;
        if (chunk.getLevel() instanceof ServerLevel level) {
            ChunkPos pos = chunk.getPos();
            amount = getTheoreticalOil(level, pos.x, pos.z);
            //TODO check for Player luck
            generated = true;
        };
    };

    public boolean isGenerated() {
        return generated;
    };

    public int getAmount() {
        return amount;
    };

    public int setAmount(int amount) {
        this.amount = Math.max(0, amount);
        return amount;
    };

    public int decreaseAmount(int decrease) {
        amount = (int)Math.max(0, amount - decrease);
        return amount;
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
