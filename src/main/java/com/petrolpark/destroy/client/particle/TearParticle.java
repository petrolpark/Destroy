package com.petrolpark.destroy.client.particle;

import com.simibubi.create.content.equipment.bell.BasicParticleData;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class TearParticle extends TextureSheetParticle {

    public TearParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, ParticleOptions data) {
        super(level, x, y, z);
        
        setSize(0.01f, 0.01f);
        pickSprite(spriteSet);
        setColor(203 / 255f, 242 / 255f, 240 / 255f);
        xd = vx;
        yd = vy;
        zd = vz;
        gravity = 0.16f;
    };

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    };

    public static class Data extends BasicParticleData<TearParticle> {
        
        @Override
        public ParticleType<?> getType() {
            return DestroyParticleTypes.TEAR.get();
        };

        @Override
        public IBasicParticleFactory<TearParticle> getBasicFactory() {
            return (level, x, y, z, vx, vy, vz, spriteSet) -> new TearParticle(level, x, y, z, vx, vy, vz, spriteSet, this);
        };
    };
    
};
