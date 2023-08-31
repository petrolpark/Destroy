package com.petrolpark.destroy.events;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.capability.level.pollution.ClientLevelPollutionData;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DestroyColorHandler {

    private static final int brown = 0x382515;
    private static float smogProportion;

    /**
     * Override all the color generators to account for the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType smog level}.
     * @param event
     */
    @SubscribeEvent
    public static void changeBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(SmogAffectedBlockColor.GRASS, Blocks.GRASS, Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.TALL_GRASS);
        event.register(SmogAffectedBlockColor.DOUBLE_TALL_GRASS, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        event.register(SmogAffectedBlockColor.PINK_PETALS, Blocks.PINK_PETALS);
        event.register(SmogAffectedBlockColor.FOLIAGE, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE, Blocks.MANGROVE_LEAVES);
        event.register(SmogAffectedBlockColor.BIRCH, Blocks.BIRCH_LEAVES);
        event.register(SmogAffectedBlockColor.SPRUCE, Blocks.SPRUCE_LEAVES);
        event.register(SmogAffectedBlockColor.WATER, Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.WATER_CAULDRON);
        event.register(SmogAffectedBlockColor.SUGAR_CANE, Blocks.SUGAR_CANE);
    };

    public static class SmogAffectedBlockColor implements BlockColor {

        private static SmogAffectedBlockColor
        GRASS = new SmogAffectedBlockColor((state, level, pos, tintIndex) ->  level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor()),
        DOUBLE_TALL_GRASS = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageGrassColor(level, state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos) : GrassColor.getDefaultColor()),
        PINK_PETALS = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> tintIndex == 0 ? -1 : (level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : GrassColor.getDefaultColor())),
        FOLIAGE = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor()),
        BIRCH = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getBirchColor()),
        SPRUCE = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getEvergreenColor()),
        WATER = new SmogAffectedBlockColor((state, level, pos, tintIndex) -> level != null && pos != null ? BiomeColors.getAverageWaterColor(level, pos) : -1),
        SUGAR_CANE = new SmogAffectedBlockColor((state, level, pos, tintIndex) ->  level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : -1);

        private final BlockColor originalColor;

        public SmogAffectedBlockColor(BlockColor originalColor) {
            this.originalColor = originalColor;
        };

        @Override
        public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            return level != null && pos != null ? withSmogTint(originalColor.getColor(state, level, pos, tintIndex)) : originalColor.getColor(state, level, pos, tintIndex);
        };
        
    };

    private static int withSmogTint(int color) {
        // Refresh the Smog Level
        if (!DestroyClientEvents.smogEnabled()) return color;
        LevelPollution levelPollution = ClientLevelPollutionData.getLevelPollution();
        smogProportion = levelPollution == null ? 0f : (float) levelPollution.get(PollutionType.SMOG) / PollutionType.SMOG.max;
        return Color.mixColors(color, brown, smogProportion);
    };
};
