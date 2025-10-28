package com.petrolpark.destroy.mixin.client;

import net.createmod.catnip.theme.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.fluid.TintedSplashParticle;
import com.petrolpark.destroy.core.pollution.ClientLevelPollutionData;
import com.petrolpark.destroy.core.pollution.Pollution;
import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Mixin to Level Renderer to change the color of rain depending on the {@link com.petrolpark.destroy.core.pollution.Pollution.PollutionType#ACID_RAIN acid rain} level.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    
    // Tint the rain according to the rain acidity
    @Inject(
        method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
            ordinal = 0
        )
    )
    public void inRenderSnowAndRainDrawRain(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        if (!rainColorAffected()) return;
        Color color = getRainColor();
        RenderSystem.setShaderColor(color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat(), 1f);
    };

    // Disable tinting if we're rendering snow and not rain
    @Inject(
        method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V",
            ordinal = 1
        )
    )
    public void inRenderSnowAndRainDrawSnow(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    };

    // Disable the tinting once the rain has been rendered
    @Inject(
        method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
        at = @At("RETURN")
    )
    public void inRenderSnowAndRainReturn(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    };

    // Add tinted splash particles 
    @Inject(
        method = "tickRain(Lnet/minecraft/client/Camera;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    public void inTickRain(Camera pCamera, CallbackInfo ci, float f, RandomSource randomsource, LevelReader levelreader, BlockPos blockpos, BlockPos blockpos1, int i, int j, int k, int l, BlockPos blockpos2, Biome biome, double d0, double d1, BlockState blockstate, FluidState fluidstate, VoxelShape voxelshape, double d2, double d3, double d4, ParticleOptions particleoptions) {
        if (!rainColorAffected()) return;
        if (particleoptions == ParticleTypes.SMOKE) return;
        Color color = getRainColor();
        ClientLevel level =  ((LevelRenderer)(Object)this).minecraft.level;
        if (level != null) level.addParticle(new TintedSplashParticle.Data(), (double)blockpos1.getX() + d0, (double)blockpos1.getY() + d4, (double)blockpos1.getZ() + d1, color.getRedAsFloat(), color.getGreenAsFloat(), color.getBlueAsFloat());
    };

    // Don't add the original untinted splash particles, but do add smoke
    @Redirect(
        method = "tickRain(Lnet/minecraft/client/Camera;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        )
    )
    public void ignoreAddParticle(ClientLevel level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if (particleData == ParticleTypes.SMOKE || !rainColorAffected()) level.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    };

    private Color getRainColor() {
        Pollution levelPollution = ClientLevelPollutionData.getLevelPollution();
        if (levelPollution == null) return new Color(0xFF3E5EB8);
        return new Color(Color.mixColors(0xFF3E5EB8, 0xFF00FF00, (float)levelPollution.get(PollutionType.ACID_RAIN) / (float)PollutionType.ACID_RAIN.max));
    };

    private static boolean rainColorAffected() {
        return PollutionHelper.pollutionEnabled() && DestroyAllConfigs.SERVER.pollution.rainColorChanges.get();
    };
};
