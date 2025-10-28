package com.petrolpark.destroy.client;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyClient;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;
import com.petrolpark.destroy.core.pollution.PollutionHelper;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent.ComputeFogColor;
import net.minecraftforge.client.event.ViewportEvent.RenderFog;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = Destroy.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class FogHandler {

    private static final Color BROWN = new Color(0xFF4D2F19);

    protected Color targetColor = Color.BLACK;
    protected Color lastColor = Color.TRANSPARENT_BLACK;
    protected LerpedFloat colorMix = LerpedFloat.linear();

    public void tick() {
        colorMix.tickChaser();
        if (colorMix.getValue() >= 1d) lastColor = targetColor;
    };

    public void setTargetColor(Color color, float partialTicks) {
        if (color.equals(targetColor)) return;
        if (lastColor.equals(Color.TRANSPARENT_BLACK)) {
            lastColor = color;
        } else {
            lastColor = getColor(partialTicks);
        }
        targetColor = color;
        colorMix.setValue(0d);
        colorMix.chase(1d, 0.2d, LerpedFloat.Chaser.EXP);
    };

    public Color getColor(float partialTicks) {
        return Color.mixColors(lastColor, targetColor, colorMix.getValue(partialTicks));
    };

    /**
     * Tick a couple of renderers.
     * @param event
     */
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) DestroyClient.FOG_HANDLER.tick();
    };

    /**
     * {@link Camera#getFluidInCamera()} doesn't account for modded fluids so we need a more general check.
     */
    private static FogType getFluidInCamera(Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        FluidState state = mc.level.getFluidState(camera.getBlockPosition());
        if (camera.getPosition().y < (double)((float)camera.getBlockPosition().getY() + state.getHeight(mc.level, camera.getBlockPosition()))) {
            return FogType.WATER;
        }
        return camera.getFluidInCamera();
    };

    /**
     * Render fog according to the world's Smog Level.
     */
    @SubscribeEvent
    public static void renderFog(RenderFog event) {
        if (!smogEnabled()) return;

        if (getFluidInCamera(event.getCamera()) == FogType.NONE) {
            Minecraft mc = Minecraft.getInstance();
            float smog = (float)PollutionHelper.getPollution(mc.level, mc.player.blockPosition(), PollutionType.SMOG);
            event.scaleNearPlaneDistance(1f - (0.8f * smog / (float)PollutionType.SMOG.max));
            event.scaleFarPlaneDistance(1f - (0.5f * smog / (float)PollutionType.SMOG.max));
            event.setCanceled(true);
        };
    };

    /**
     * Set the color of Smog.
     */
    @SubscribeEvent
    public static void colorFog(ComputeFogColor event) {
        if (!smogEnabled()) return;

        if (getFluidInCamera(event.getCamera()) == FogType.NONE) {
            Minecraft mc = Minecraft.getInstance();
            float smog = (float)PollutionHelper.getPollution(mc.level, mc.player.blockPosition(), PollutionType.SMOG);
            Color existing = new Color(event.getRed(), event.getGreen(), event.getBlue(), 1f);
            DestroyClient.FOG_HANDLER.setTargetColor(Color.mixColors(existing, BROWN, 0.8f * smog / (float)PollutionType.SMOG.max), AnimationTickHolder.getPartialTicks());
            Color color = DestroyClient.FOG_HANDLER.getColor(AnimationTickHolder.getPartialTicks());
            event.setRed(color.getRedAsFloat());
            event.setGreen(color.getGreenAsFloat());
            event.setBlue(color.getBlueAsFloat());
        };
    };

    protected static boolean smogEnabled() {
        return PollutionHelper.pollutionEnabled() && DestroyAllConfigs.SERVER.pollution.smog.get();
    };
};