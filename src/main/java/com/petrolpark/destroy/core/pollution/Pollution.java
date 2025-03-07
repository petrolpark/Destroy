package com.petrolpark.destroy.core.pollution;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import net.createmod.catnip.lang.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.DestroyTags.Fluids;
import com.petrolpark.destroy.chemistry.legacy.LegacySpeciesTag;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.client.DestroyIcons;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class Pollution {

    public static final Capability<Pollution> CAPABILITY = CapabilityManager.get(new CapabilityToken<Pollution>() {});

    protected final boolean local;
    protected EnumMap<PollutionType, Integer> levels;

    protected Pollution(boolean local) {
        this.local = local;
        levels = new EnumMap<>(PollutionType.class);
        List.of(PollutionType.values()).forEach(p -> {
            if (p.local == local) levels.put(p, 0);
        });
    };

    /**
     * Get the value of the given type of Pollution in this Level.
     */
    public int get(PollutionType pollutionType) {
        if (pollutionType == null) return 0;
        checkLocal(pollutionType);
        return levels.get(pollutionType);
    };

    /**
     * Set the value of the given type of Pollution in this Level.
     * This does not broadcast the change to clients or reward advancements.
     * @param pollutionType
     * @param value Will be set within the {@link PollutionType bounds}
     * @return The actual value to which the Pollution level was set
     */
    public int set(PollutionType pollutionType, int value) {
        if (pollutionType == null) return 0;
        checkLocal(pollutionType);
        value = Mth.clamp(value, 0, pollutionType.max);
        levels.replace(pollutionType, value);
        return value;
    };

    /**
     * Increase the value of the given type of Pollution in this level by the given amount, within the {@link PollutionType bounds} of that type of Pollution.
     * This does not broadcast the change to clients or reward advancements.
     * @param pollutionType
     * @param change Can be positive or negative
     * @return The actual value to which the Pollution level was set
     */
    public int change(PollutionType pollutionType, int change) {
        if (pollutionType == null) return 0;
        checkLocal(pollutionType);
        return set(pollutionType, levels.get(pollutionType) + change);
    };
    
    public void saveNBTData(CompoundTag tag) {
        levels.forEach((pollutionType, value) -> {
            tag.putInt(pollutionType.name(), value);
        });
    };
  
    public void loadNBTData(CompoundTag tag) {
        levels.keySet().forEach((pollutionType) -> {
            levels.replace(pollutionType, tag.getInt(pollutionType.name()));
        });
    };

    protected void checkLocal(PollutionType type) {
        if (type.local != local) throw new IllegalArgumentException("Pollution Type "+type.name()+" has the wrong locality.");
    };

    public static class PonderCapabilityProvider extends CapabilityProvider<Pollution> {
        
        public PonderCapabilityProvider() {
            super(() -> new Pollution(true));
        };
    };

    public static class Level extends Pollution {

        private float outdoorTemperature; // In kelvins

        public Level() {
            super(false);
            outdoorTemperature = 289f;
        };

        @Override
        public int set(PollutionType pollutionType, int value) {
            int result = super.set(pollutionType, value);
            updateTemperature();
            return result;
        };

        public void updateTemperature() {
            outdoorTemperature = 289f;
            if (!PollutionHelper.pollutionEnabled() || !DestroyAllConfigs.SERVER.pollution.temperatureAffected.get()) return;
            outdoorTemperature +=
                (get(PollutionType.GREENHOUSE) / PollutionType.GREENHOUSE.max) * 20f
              + (get(PollutionType.OZONE_DEPLETION) / PollutionType.OZONE_DEPLETION.max) * 4f;
        };

        @Override
        public void saveNBTData(CompoundTag tag) {
            super.saveNBTData(tag);
            tag.putFloat("Temperature", outdoorTemperature);
        };

        @Override
        public void loadNBTData(CompoundTag tag) {
            super.loadNBTData(tag);
            outdoorTemperature = tag.getFloat("Temperature");
        };

        public float getOutdoorTemperature() {
            return outdoorTemperature;
        };

        public static class Provider extends Pollution.CapabilityProvider<Pollution.Level> {

            public Provider() {
                super(Pollution.Level::new);
            };
    
        };
    
    };

    public static class Chunk extends Pollution {

        private int smogLevelSinceLastRerender;


        public Chunk() {
            super(true);
            smogLevelSinceLastRerender = levels.get(PollutionType.SMOG);
        };

        public boolean checkRerender() {
            int smog = levels.get(PollutionType.SMOG);
            if (Math.abs(smog - smogLevelSinceLastRerender) >= PollutionType.SMOG.max / 64) {
                smogLevelSinceLastRerender = smog;
                return true;
            };
            return false;
        };

        public static class Provider extends Pollution.CapabilityProvider<Pollution.Chunk> {

            public Provider() {
                super(Chunk::new);
            };

        };
    };
    
    public static abstract class CapabilityProvider<P extends Pollution> implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        private final Supplier<P> factory;

        private P pollution;
        protected final LazyOptional<P> optional = LazyOptional.of(this::createPollutionCap);

        protected CapabilityProvider(Supplier<P> factory) {
            this.factory = factory;
        };

        private P createPollutionCap() {
            if (pollution == null) pollution = factory.get();
            return pollution;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            createPollutionCap().saveNBTData(tag);
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            createPollutionCap().loadNBTData(nbt);
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == CAPABILITY) return optional.cast();
            return LazyOptional.empty();
        };
    };

    /**
     * Get the outdoor ("room") temperature at the given position, accounting for the change in temperature due to pollution
     * and the natural heat of the Biome at that position.
     * @param level
     * @param pos
     * @return Temperature in kelvins
     * @see Pollution#getGlobalTemperature Get the temperature not accounting for the Biome
     */
    public static float getLocalTemperature(net.minecraft.world.level.Level level, BlockPos pos) {
        return level.getCapability(CAPABILITY).map(pollution -> {
            return ((Pollution.Level)pollution).getOutdoorTemperature() + (10f * level.getBiome(pos).get().getBaseTemperature());
        }).orElse(289f);
    };

    public enum PollutionType implements INamedIconOptions {

        GREENHOUSE(DestroyIcons.GREENHOUSE, false, 65536, DestroyMolecules.Tags.GREENHOUSE, Fluids.GREENHOUSE_GAS.tag),

        OZONE_DEPLETION(DestroyIcons.OZONE_DEPLETION, false, 65536, DestroyMolecules.Tags.OZONE_DEPLETER, Fluids.DEPLETES_OZONE.tag),

        SMOG(DestroyIcons.SMOG, true, 65536, DestroyMolecules.Tags.SMOG, Fluids.AMPLIFIES_SMOG.tag),

        ACID_RAIN(DestroyIcons.ACID_RAIN, false, 65536, DestroyMolecules.Tags.ACID_RAIN, Fluids.ACIDIFIES_RAIN.tag),

        RADIOACTIVITY(DestroyIcons.RADIOACTIVITY, true, 65536, null, Fluids.RADIOACTIVE.tag);

        private final AllIcons icon;
        // Min is always 0
        public final int max;
        /**
         * Whether this applies to chunks (if {@code false}, it applies to the whole level)
         */
        public final boolean local;
        public final LegacySpeciesTag moleculeTag;
        public final TagKey<Fluid> fluidTag;

        PollutionType(AllIcons icon, boolean local, int max, LegacySpeciesTag moleculeTag, TagKey<Fluid> fluidTag) {
            this.icon = icon;
            this.local = local;
            this.max = max;
            this.moleculeTag = moleculeTag;
            this.fluidTag = fluidTag;
        };

        @Override
        public AllIcons getIcon() {
            return icon;
        };

        @Override
        public String getTranslationKey() {
            return "destroy.pollution."+ Lang.asId(name());
        };

    };
};
