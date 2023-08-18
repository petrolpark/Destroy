package com.petrolpark.destroy.client.particle;

import com.simibubi.create.content.equipment.bell.BasicParticleData;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.ParticleType;

public class TintedSplashParticle extends WaterDropParticle {

    public TintedSplashParticle(ClientLevel level, double x, double y, double z, double r, double g, double b, SpriteSet sprites) {
        super(level, x, y, z);
        this.gravity = 0.04f;
        pickSprite(sprites);
        setColor((float)r, (float)g, (float)b);
    };

    public static class Data extends BasicParticleData<TintedSplashParticle> {

        @Override
        public ParticleType<?> getType() {
            return DestroyParticleTypes.TINTED_SPLASH.get();
        };

        @Override
        public IBasicParticleFactory<TintedSplashParticle> getBasicFactory() {
            return TintedSplashParticle::new;
        };
        
    };
    
};
