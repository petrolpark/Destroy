package com.petrolpark.destroy.core.fluid.gasparticle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import static net.createmod.catnip.platform.CatnipServices.REGISTRIES;

public class BoilingFluidBubbleParticleData implements ParticleOptions, ICustomParticleDataWithSprite<BoilingFluidBubbleParticleData> {

    protected ParticleType<BoilingFluidBubbleParticleData> type;
    protected FluidStack fluid; // The fluid stack this of which this Particle is meant to be a gas cloud

    /**
     * Empty constructor to use in {@link com.petrolpark.destroy.client.DestroyParticleTypes registration}.
     */
    public BoilingFluidBubbleParticleData() {};

    /**
     * Get the Fluid stack which this gas Particle represents.
     */
    public FluidStack getFluid() {
        return fluid;
    };

    /**
     * A Particle with the apperance of a cloud of smoke and the color of a given Fluid.
     * @param type See {@link com.petrolpark.destroy.client.DestroyParticleTypes here}
     * @param fluid The Fluid of which this Particle should take the appearance
     */
    @SuppressWarnings("unchecked")
    public BoilingFluidBubbleParticleData(ParticleType<?> type, FluidStack fluid) {
        this.type = (ParticleType<BoilingFluidBubbleParticleData>) type;
        this.fluid = fluid;
    };

    public BoilingFluidBubbleParticleData(FluidStack fluid) {
        this(DestroyParticleTypes.BOILING_FLUID_BUBBLE.get(), fluid);
    };

    public static final Codec<BoilingFluidBubbleParticleData> CODEC = RecordCodecBuilder.create(i -> i
            .group(FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid)).apply(i, fs -> new BoilingFluidBubbleParticleData(DestroyParticleTypes.BOILING_FLUID_BUBBLE.get(), fs))
    );

    @SuppressWarnings("deprecation") // Deserializer is deprecated
    public static final ParticleOptions.Deserializer<BoilingFluidBubbleParticleData> DESERIALIZER =
            new ParticleOptions.Deserializer<BoilingFluidBubbleParticleData>() {

                // Currently no command capability exists for fluid-based particles, so just make it water
                @Override
                public BoilingFluidBubbleParticleData fromCommand(ParticleType<BoilingFluidBubbleParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
                    return new BoilingFluidBubbleParticleData(particleTypeIn, new FluidStack(Fluids.WATER, 1));
                };

                @Override
                public BoilingFluidBubbleParticleData fromNetwork(ParticleType<BoilingFluidBubbleParticleData> particleTypeIn, FriendlyByteBuf buffer) {
                    return new BoilingFluidBubbleParticleData(particleTypeIn, buffer.readFluidStack());
                };
            };

    @Override
    public Codec<BoilingFluidBubbleParticleData> getCodec(ParticleType<BoilingFluidBubbleParticleData> type) {
        return CODEC;
    };

    @Override
    @SuppressWarnings("deprecation") // Deserializer is deprecated
    public Deserializer<BoilingFluidBubbleParticleData> getDeserializer() {
        return DESERIALIZER;
    };


    @Override
    public ParticleType<BoilingFluidBubbleParticleData> getType() {
        return type;
    };

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFluidStack(fluid);
    };

    @Override
    public @NotNull String writeToString() {
        return REGISTRIES.getKeyOrThrow(type) + " " + REGISTRIES.getKeyOrThrow(fluid.getFluid());
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public ParticleEngine.SpriteParticleRegistration<BoilingFluidBubbleParticleData> getMetaFactory() {
        return BoilingFluidBubbleParticle.Provider::new;
    };
};