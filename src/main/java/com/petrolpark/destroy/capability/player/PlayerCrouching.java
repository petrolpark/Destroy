package com.petrolpark.destroy.capability.player;

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

public class PlayerCrouching {

    public int ticksCrouching; // How long the Player has been crouching
    public int ticksUrinating; // How long the Player has been urinating

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<PlayerCrouching> PLAYER_CROUCHING = CapabilityManager.get(new CapabilityToken<PlayerCrouching>() {});

        private PlayerCrouching playerCrouching = null;
        private final LazyOptional<PlayerCrouching> optional = LazyOptional.of(this::createPlayerCrouching);

        private PlayerCrouching createPlayerCrouching() {
            if (playerCrouching == null) {
                playerCrouching = new PlayerCrouching();
            };
            return playerCrouching;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            PlayerCrouching playerCrouching = createPlayerCrouching();
            tag.putInt("Crouching", playerCrouching.ticksCrouching);
            tag.putInt("Urinating", playerCrouching.ticksUrinating);
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            PlayerCrouching playerCrouching = createPlayerCrouching();
            playerCrouching.ticksCrouching = tag.getInt("Crouching");
            playerCrouching.ticksUrinating = tag.getInt("Urinating");
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap == PLAYER_CROUCHING) {
                return optional.cast();
            };
            return LazyOptional.empty();
        };

    };
};
