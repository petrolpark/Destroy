package com.petrolpark.destroy.capability.babyblue;

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

public class PlayerBabyBlueAddictionProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerBabyBlueAddiction> PLAYER_BABY_BLUE_ADDICTION = CapabilityManager.get(new CapabilityToken<PlayerBabyBlueAddiction>() { });

    private PlayerBabyBlueAddiction babyBlueAddiction = null;
    private final LazyOptional<PlayerBabyBlueAddiction> optional = LazyOptional.of(this::createPlayerBabyBlueAddiction);

    private PlayerBabyBlueAddiction createPlayerBabyBlueAddiction() {
        if (this.babyBlueAddiction == null) {
            this.babyBlueAddiction = new PlayerBabyBlueAddiction();
        }
        return this.babyBlueAddiction;
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_BABY_BLUE_ADDICTION) {
            return optional.cast();
        };

        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerBabyBlueAddiction().saveNBTData(nbt);
        return nbt;
    };

    @Override
    public void deserializeNBT(CompoundTag nbt) {
         createPlayerBabyBlueAddiction().loadNBTData(nbt);
    };
}
