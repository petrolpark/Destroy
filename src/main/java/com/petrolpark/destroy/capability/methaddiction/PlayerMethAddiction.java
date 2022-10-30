package com.petrolpark.destroy.capability.methaddiction;

import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.nbt.CompoundTag;

public class PlayerMethAddiction {
    private int methAddiction;
    private final int MIN_METH_ADDICTION = 0;
    private final int MAX_METH_ADDICTION = DestroyAllConfigs.SERVER.substances.maxAddictionLevel.get(); //default is 590
    //private final int MAX_METH_ADDICTION = 590;

    public void copyFrom(PlayerMethAddiction source) {
        this.methAddiction = source.methAddiction;
    };

    public int getMethAddiction() {
        return this.methAddiction;
    };

    public void setMethAddiction(int methAddiction) {
        if (methAddiction < MIN_METH_ADDICTION) {
            this.methAddiction = MIN_METH_ADDICTION;
        } else if (methAddiction > MAX_METH_ADDICTION) {
            this.methAddiction = MAX_METH_ADDICTION;
        } else {
            this.methAddiction = methAddiction;
        };
    };

    public void addMethAddiction(int change) {
        //TODO use clamp instead
        if (change >= 0) {
            this.methAddiction = Math.min(MAX_METH_ADDICTION, this.methAddiction + change);
        } else {
            this.methAddiction = Math.max(MIN_METH_ADDICTION, this.methAddiction - change);
        };
    };

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("methAddiction", this.methAddiction);
    };

    public void loadNBTData(CompoundTag nbt) {
        this.methAddiction = nbt.getInt("methAddiction");
    };
}
