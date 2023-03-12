package com.petrolpark.destroy.events;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.capability.level.pollution.ClientLevelPollutionData;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.simibubi.create.foundation.utility.Color;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Destroy.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DestroyColorHandler {

    private static final int brown = 4858625;
    private static float smogProportion;

    private static int cachedStartColor = 0;
    private static int cachedTransformedColor = 0;

    /**
     * Override all the color generators to account for the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType smog level}.
     * @param event
     */
    @SubscribeEvent
    public static void changeBlockColors(RegisterColorHandlersEvent.Block event) {
        Destroy.LOGGER.info("Look at me loading block colors");
        event.register((state, blockAndTintGetter, pos, tintIndex) -> {
                return blockAndTintGetter != null && pos != null ? withSmogTint(BiomeColors.getAverageGrassColor(blockAndTintGetter, pos))  : GrassColor.get(0.5D, 1.0D);
            }, DestroyBlocks.MASHED_POTATO_BLOCK.get(), Blocks.GRASS_BLOCK);
    };

    private static int withSmogTint(int color) {
        if (color == cachedStartColor) return cachedTransformedColor; // To avoid calculating the same color transformation over and over, store the last transformed color, and if the color to be transformed is the same as before, use the previous transformation result
        Destroy.LOGGER.info("Switching thing now");
        cachedStartColor = color;
        refreshSmogLevel();
        cachedTransformedColor = Color.mixColors(color, brown, smogProportion);
        return cachedTransformedColor;
    };

    /**
     * Let the Client know that the level of Smog in the Level has changed.
     */
    public static void refreshSmogLevel() {
        LevelPollution levelPollution = ClientLevelPollutionData.getLevelPollution();
        smogProportion = levelPollution == null ? 0f : (float) levelPollution.get(PollutionType.SMOG) / PollutionType.SMOG.max;
    };
};
