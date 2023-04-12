package com.petrolpark.destroy.client.particle;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.BubbleCapBlockEntity;
import com.petrolpark.destroy.client.particle.data.GasParticleData;
import com.simibubi.create.content.contraptions.fluids.particle.FluidStackParticle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;

public class GasParticle extends FluidStackParticle {

    private static final int TICKS_PER_BLOCK = BubbleCapBlockEntity.getTankCapacity() / BubbleCapBlockEntity.getTransferRate() ; // How long this Particle should take to travel up the distance of one Block
    private static final float VERTICAL_SPEED = 1f / TICKS_PER_BLOCK;
    private final boolean isDistillation;

    private final float blockHeight;

    private GasParticle(ClientLevel level, FluidStack fluid, ParticleType<GasParticleData> type, float blockHeight, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, fluid, x, y, z, vx, vy, vz);

        pickSprite(sprites);
        gravity = 0f;
        quadSize *= 6.0f;
        this.blockHeight = blockHeight;

        if (type == DestroyParticleTypes.DISTILLATION.get() && blockHeight != 0) {
            isDistillation = true;
            lifetime = (int)(this.blockHeight * TICKS_PER_BLOCK);
            yd += VERTICAL_SPEED + (double)(random.nextFloat() / 500.0F);
        } else {
            isDistillation = false;
            lifetime = (int)Mth.lerp(fluid.getAmount() / 4000, 60, 180);
        };

        hasPhysics = !isDistillation;
    };

    @Override
    public void tick() {
        super.tick();
        xd += (double)(random.nextFloat() / 5000.0F * (float)(random.nextBoolean() ? 1 : -1));
        zd += (double)(random.nextFloat() / 5000.0F * (float)(random.nextBoolean() ? 1 : -1));

        if (isDistillation) {
            this.move(0d, VERTICAL_SPEED, 0d);
        };

        if (lifetime - age < TICKS_PER_BLOCK && alpha > 0.010f) {
            alpha -= 0.015f;
        };
    };

    @Override
    protected float getU0() {
        return sprite.getU0();
    };

    @Override
    protected float getU1() {
        return sprite.getU1();
    };

    @Override
    protected float getV0() {
        return sprite.getV0();
    };

    @Override
    protected float getV1() {
        return sprite.getV1();
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    };

    @Override
    protected boolean canEvaporate() {
        return false;
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
