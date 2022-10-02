package com.petrolpark.destroy.methaddiction;

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

public class PlayerMethAddictionProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerMethAddiction> PLAYER_METH_ADDICTION = CapabilityManager.get(new CapabilityToken<PlayerMethAddiction>() { });

    private PlayerMethAddiction methAddiction = null;
    private final LazyOptional<PlayerMethAddiction> optional = LazyOptional.of(this::createPlayerMethAddiction);

    private PlayerMethAddiction createPlayerMethAddiction() {
        if (this.methAddiction == null) {
            this.methAddiction = new PlayerMethAddiction();
        }
        return this.methAddiction;
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_METH_ADDICTION) {
            return optional.cast();
        };

        return LazyOptional.empty();
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerMethAddiction().saveNBTData(nbt);
        return nbt;
    };

    @Override
    public void deserializeNBT(CompoundTag nbt) {
         createPlayerMethAddiction().loadNBTData(nbt);
    };
}
