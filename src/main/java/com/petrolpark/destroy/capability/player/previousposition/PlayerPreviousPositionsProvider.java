package com.petrolpark.destroy.capability.player.previousposition;

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

public class PlayerPreviousPositionsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerPreviousPositions> PLAYER_PREVIOUS_POSITIONS = CapabilityManager.get(new CapabilityToken<PlayerPreviousPositions>() {});

    private PlayerPreviousPositions previousPositions = null;
    private final LazyOptional<PlayerPreviousPositions> optional = LazyOptional.of(this::createPlayerPreviousPositions);

    private PlayerPreviousPositions createPlayerPreviousPositions() {
        PlayerPreviousPositions.updateQueueSize();
        if (previousPositions == null) {
            previousPositions = new PlayerPreviousPositions();
        };
        return previousPositions;
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_PREVIOUS_POSITIONS) {
            return optional.cast();
        };
        return LazyOptional.empty();
    };

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createPlayerPreviousPositions().saveNBTData(tag);
        return tag;
    };

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createPlayerPreviousPositions().loadNBTData(tag);
    };
    
}
