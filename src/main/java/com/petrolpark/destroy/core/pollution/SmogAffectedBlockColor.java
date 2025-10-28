package com.petrolpark.destroy.core.pollution;

import java.util.function.BiFunction;
import java.util.function.IntSupplier;

import javax.annotation.Nullable;

import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;

import net.createmod.catnip.theme.Color;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;

public class SmogAffectedBlockColor implements BlockColor {

    public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
    public static final ColorResolver FOLIAGE_COLOR_RESOLVER = (b, x, z) -> b.getFoliageColor();
    public static final ColorResolver WATER_COLOR_RESOLVER = (b, x, z) -> b.getWaterColor();

    public static final int getAverageGrassColor(BlockAndTintGetter level, BlockPos pos) {
        return level.getBlockTint(pos, GRASS_COLOR_RESOLVER);
    };

    public static final int getAverageFoliageColor(BlockAndTintGetter level, BlockPos pos) {
        return level.getBlockTint(pos, FOLIAGE_COLOR_RESOLVER);
    };

    public static final int getAverageWaterColor(BlockAndTintGetter level, BlockPos pos) {
        return level.getBlockTint(pos, WATER_COLOR_RESOLVER);
    };

    public static final BlockColor

    GRASS = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageGrassColor, GrassColor::getDefaultColor),
    DOUBLE_TALL_GRASS = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageGrassColor, GrassColor::getDefaultColor),
    PINK_PETALS = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageGrassColor, GrassColor::getDefaultColor),
    FOLIAGE = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageFoliageColor, FoliageColor::getDefaultColor),
    BIRCH = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageFoliageColor, FoliageColor::getBirchColor),
    SPRUCE = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageFoliageColor, FoliageColor::getEvergreenColor),
    WATER = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageWaterColor, () -> 4159204),
    SUGAR_CANE = new SmogAffectedBlockColor(SmogAffectedBlockColor::getAverageGrassColor, () -> -1);

    private final IntSupplier fallback;
    private final BlockColor wrapped;

    public SmogAffectedBlockColor(BiFunction<BlockAndTintGetter, BlockPos, Integer> levelAndPosBlockColor, IntSupplier fallback) {
        this((state, level, pos, tintIndex) -> level != null && pos != null ? levelAndPosBlockColor.apply(level, pos) : fallback.getAsInt(), fallback);
    };

    public SmogAffectedBlockColor(BlockColor wrapped, IntSupplier fallback) {
        this.wrapped = wrapped;
        this.fallback = fallback;
    };

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level != null && level instanceof PonderLevel ponder) return getColor(fallback.getAsInt(), ponder.getCapability(Pollution.CAPABILITY));
        return wrapped.getColor(state, level, pos, tintIndex);
    };

    /**
     * Get the color of a Block due to Smog, not accounting for the colors of any Blocks surrounding it.
     */
    public static int getColor(int originalColor, @Nullable BlockPos pos, BlockAndTintGetter level) {
        if (pos != null) {
            Minecraft mc = Minecraft.getInstance();
            ChunkPos chunkPos = new ChunkPos(pos); 
            LevelChunk chunk = mc.level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);
            if (chunk != null) return getColor(originalColor, chunk.getCapability(Pollution.CAPABILITY));
        };
        return originalColor;
    };

    public static int getColor(int originalColor, LazyOptional<Pollution> pollutionOp) {
        if (!pollutionOp.isPresent()) return originalColor;
        return Color.mixColors(originalColor, brown, (float) pollutionOp.resolve().get().get(PollutionType.SMOG) / PollutionType.SMOG.max);
    };
    
    private static final int brown = 0xFF3F332A;
    
};

