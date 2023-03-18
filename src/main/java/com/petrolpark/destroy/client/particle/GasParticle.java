package com.petrolpark.destroy.client.particle;

import javax.annotation.Nullable;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.block.entity.BubbleCapBlockEntity;
import com.petrolpark.destroy.client.particle.data.GasParticleData;
import com.simibubi.create.content.contraptions.fluids.particle.FluidStackParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.fluids.FluidStack;

public class GasParticle extends FluidStackParticle {

    private static final int TICKS_PER_BLOCK = BubbleCapBlockEntity.getTransferRate() / BubbleCapBlockEntity.getTankCapacity(); // How long this Particle should take to travel up the distance of one Block
    private static final float VERTICAL_SPEED = 1f / TICKS_PER_BLOCK;
    private boolean isDistillation;

    private int blockHeight;

    private GasParticle(ClientLevel level, FluidStack fluid, ParticleType<GasParticleData> type, int blockHeight, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, fluid, x, y, z, vx, vy, vz);
        pickSprite(sprites); // Override the sprite (which is currently a Fluid Texture) with a random sprite from the file
        isDistillation = false;
        this.blockHeight = blockHeight;

        if (type == DestroyParticleTypes.DISTILLATION.get() && blockHeight != 0) {
            isDistillation = true;
            Destroy.LOGGER.info("Look at me I'm in a distillation tower. "+blockHeight);
            lifetime = this.blockHeight * TICKS_PER_BLOCK;
        };
    };

    @Override
    public void tick() {
        super.tick();
        if (isDistillation) {
            yd += VERTICAL_SPEED;
            if (lifetime - age < TICKS_PER_BLOCK && alpha > 0.010f) {
                alpha -= 0.015f;
            };
        };
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    };

    public static class Provider implements ParticleProvider<GasParticleData> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        };

        @Override
        @Nullable
        public Particle createParticle(GasParticleData data, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
            return new GasParticle(level, data.getFluid(), data.getType(), data.getBlockHeight(), x, y, z, vx, vy, vz, spriteSet);
        };
    };
};
