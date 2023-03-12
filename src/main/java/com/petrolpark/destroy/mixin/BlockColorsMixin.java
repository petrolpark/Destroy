package com.petrolpark.destroy.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.jozufozu.flywheel.util.Color;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.util.DestroyTags.DestroyBlockTags;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    private static final int brown = 4858625;
    private static Float cachedSmogLevel; // null means something has gone wrong, so don't apply
    private static int cachedColor = 0;
    private static int cachedTransformedColor = 0;
    
    @Overwrite
    public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
        BlockColor blockColor = ((BlockColorsAccessor)this).getBlockColors().get(net.minecraftforge.registries.ForgeRegistries.BLOCKS.getDelegateOrThrow(pState.getBlock()));
        if (blockColor == null) return -1;
        int color = blockColor.getColor(pState, pLevel, pPos, pTintIndex);
        if (getCachedSmogLevel(pLevel) != null) {
            if (pState.is(DestroyBlockTags.AFFECTED_BY_SMOG.tag)) {
                color = transformColor(color);
            };
        };
        return color;
    };

    /*
     * To make this even a tiny bit efficient, store a color.
     * If the currently-processing color matches the stored color, return the stored transformed color.
     * This way, the mixed color does not have to be recalculated every time.
     */
    private static int transformColor(int color) {
        if (color == cachedColor) return cachedTransformedColor;
        Destroy.LOGGER.info("Switched rendering colors");
        cachedColor = color;
        cachedTransformedColor = Color.mixColors(color, brown, cachedSmogLevel);
        return cachedTransformedColor;
    };

    private static Float getCachedSmogLevel(BlockAndTintGetter blockAndTintGetter) {
        if (cachedSmogLevel == null) { // If we need to check the smog level
            Destroy.LOGGER.info("Time to check smog level");
            if (blockAndTintGetter != null) Destroy.LOGGER.info("Looks like we';re in a "+blockAndTintGetter.getClass().getName());
            if (blockAndTintGetter instanceof RenderChunkRegion renderChunkRegion) {
                Level level = ((RenderChunkRegionAccessor) renderChunkRegion).getLevel();
                if (level != null) {
                    cachedSmogLevel = level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
                        Destroy.LOGGER.info("Time to get thge smog level looks like it's "+levelPollution.get(PollutionType.SMOG));
                        return levelPollution.get(PollutionType.SMOG) / (float)PollutionType.SMOG.max;
                    }).orElse(null);
                };
            };
        };
        return cachedSmogLevel;
    };
};
