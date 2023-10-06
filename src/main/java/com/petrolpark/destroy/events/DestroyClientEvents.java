package com.petrolpark.destroy.events;

import javax.annotation.Nonnull;

import com.jozufozu.flywheel.util.Color;
import com.petrolpark.destroy.block.renderer.BlockEntityBehaviourRenderer;
import com.petrolpark.destroy.capability.level.pollution.ClientLevelPollutionData;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.item.renderer.SeismometerItemRenderer;
import com.petrolpark.destroy.util.PollutionHelper;

import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class DestroyClientEvents {

    @Nonnull
    private static Color BROWN = new Color(0xFF4D2F19);

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            SeismometerItemRenderer.tick();
        } else {
            BlockEntityBehaviourRenderer.tick();
        };
    };

    @SubscribeEvent
    public static void renderFog(RenderFog event) {
        if (!DestroyClientEvents.smogEnabled()) return;
        if (event.getType() == FogType.NONE) {
            LevelPollution levelPollution = ClientLevelPollutionData.getLevelPollution();
            event.scaleNearPlaneDistance(1f - (0.8f * (float)levelPollution.get(PollutionType.SMOG) / (float)PollutionType.SMOG.max));
            event.scaleFarPlaneDistance(1f - (0.5f * (float)levelPollution.get(PollutionType.SMOG) / (float)PollutionType.SMOG.max));
            event.setCanceled(true);
        };
    };

    @SubscribeEvent
    public static void colorFog(ComputeFogColor event) {
        if (!DestroyClientEvents.smogEnabled()) return;
        if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            LevelPollution levelPollution = ClientLevelPollutionData.getLevelPollution();
            Color existing = new Color(event.getRed(), event.getGreen(), event.getBlue(), 1f);
            Color color = Color.mixColors(existing, BROWN, 0.8f * (float)levelPollution.get(PollutionType.SMOG) / (float)PollutionType.SMOG.max);
            event.setRed(color.getRedAsFloat());
            event.setGreen(color.getGreenAsFloat());
            event.setBlue(color.getBlueAsFloat());
        };
    };

    public static boolean smogEnabled() {
        return PollutionHelper.pollutionEnabled() && DestroyAllConfigs.COMMON.pollution.smog.get();
    };
};
