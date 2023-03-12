package com.petrolpark.destroy.capability.level.pollution;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class LevelPollutionProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<LevelPollution> LEVEL_POLLUTION = CapabilityManager.get(new CapabilityToken<LevelPollution>() {});

    private LevelPollution pollution = null;
    private final LazyOptional<LevelPollution> optional = LazyOptional.of(this::createLevelPollution);

    private LevelPollution createLevelPollution() {
        if (this.pollution == null) {
            this.pollution = new LevelPollution();
        }
        return this.pollution;
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == LEVEL_POLLUTION) {
            return optional.cast();
        };

        return LazyOptional.empty();
    };

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createLevelPollution().saveNBTData(nbt);
        return nbt;
    };

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createLevelPollution().loadNBTData(nbt);
    };
    
};
