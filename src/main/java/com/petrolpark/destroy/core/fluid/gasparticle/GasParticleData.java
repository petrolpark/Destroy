package com.petrolpark.destroy.core.fluid.gasparticle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.destroy.client.DestroyParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;

import net.createmod.catnip.platform.ForgeRegisteredObjectsHelper;
import net.createmod.catnip.platform.services.RegisteredObjectsHelper;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

public class GasParticleData implements ParticleOptions, ICustomParticleDataWithSprite<GasParticleData> {

    private ParticleType<GasParticleData> type; // What type of particle this is (Distillation, etc.)
	private FluidStack fluid; // The fluid stack this of which this Particle is meant to be a gas cloud
    private float blockHeight; // How many blocks upwards this Particle should float (used if it is in a Distillation Tower)

    /**
     * Empty constructor to use in {@link com.petrolpark.destroy.client.DestroyParticleTypes registration}.
     */
    public GasParticleData() {};

    /**
     * Get the Fluid stack which this gas Particle represents.
     */
    public FluidStack getFluid() {
        return fluid;
    };

    /**
     * How high this Particle should float before disappearing.
     */
    public float getBlockHeight() {
        return blockHeight;
    };

	public GasParticleData(ParticleType<?> type, FluidStack fluid) {
		this(type, fluid, 0);
	};

    /**
     * A Particle with the apperance of a cloud of smoke and the color of a given Fluid.
     * @param type See {@link com.petrolpark.destroy.client.DestroyParticleTypes here}
     * @param fluid The Fluid of which this Particle should take the appearance
     * @param blockHeight How many blocks upward this Particle should float before disappearing (used for the {@link com.petrolpark.destroy.content.processing.distillation.BubbleCapBlockEntity#spawnParticles Distillation Tower})
     */
    @SuppressWarnings("unchecked")
    public GasParticleData(ParticleType<?> type, FluidStack fluid, float blockHeight) {
        this.type = (ParticleType<GasParticleData>) type;
        this.fluid = fluid;
        this.blockHeight = blockHeight;
    };

    public static final Codec<GasParticleData> DISTILLATION_CODEC = RecordCodecBuilder.create(i -> i
		.group(
            FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid),
            Codec.FLOAT.fieldOf("blockHeight").forGetter(p -> p.blockHeight)
        ).apply(i, (fluidStack, blockHeight) -> new GasParticleData(DestroyParticleTypes.DISTILLATION.get(), fluidStack, blockHeight))
    );

    public static final Codec<GasParticleData> EVAPORATION_CODEC = RecordCodecBuilder.create(i -> i
        .group(
            FluidStack.CODEC.fieldOf("fluid").forGetter(p -> p.fluid)
        ).apply(i, (fluidStack) -> new GasParticleData(DestroyParticleTypes.EVAPORATION.get(), fluidStack, 0))
    );

    @SuppressWarnings("deprecation") // Deserializer is deprecated
    public static final ParticleOptions.Deserializer<GasParticleData> DESERIALIZER =
		new ParticleOptions.Deserializer<GasParticleData>() {

            // Currently no command capability exists for fluid-based particles, so just make it water
            @Override
			public GasParticleData fromCommand(ParticleType<GasParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
				return new GasParticleData(particleTypeIn, new FluidStack(Fluids.WATER, 1), 5);
			};

            @Override
			public GasParticleData fromNetwork(ParticleType<GasParticleData> particleTypeIn, FriendlyByteBuf buffer) {
				return new GasParticleData(particleTypeIn, buffer.readFluidStack(), buffer.readInt());
			};
		};

    @Override
    public Codec<GasParticleData> getCodec(ParticleType<GasParticleData> type) {
        if (type == DestroyParticleTypes.DISTILLATION.get()) return DISTILLATION_CODEC;
        return EVAPORATION_CODEC;
    };

    @Override
    @SuppressWarnings("deprecation") // Deserializer is deprecated
    public Deserializer<GasParticleData> getDeserializer() {
        return DESERIALIZER;
    };


    @Override
    public ParticleType<GasParticleData> getType() {
        return type;
    };

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFluidStack(fluid);
        buffer.writeFloat(blockHeight);
    };

    @Override
    @SuppressWarnings("rawtypes")
    public String writeToString() {
        RegisteredObjectsHelper<IForgeRegistry> o = new ForgeRegisteredObjectsHelper();
        return o.getKeyOrThrow(type) + " " + o.getKeyOrThrow(fluid.getFluid());
    };

    @Override
    public SpriteParticleRegistration<GasParticleData> getMetaFactory() {
        return GasParticle.Provider::new;
    };
};
