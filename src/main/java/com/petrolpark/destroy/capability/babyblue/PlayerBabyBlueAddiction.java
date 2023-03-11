package com.petrolpark.destroy.capability.babyblue;

import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class PlayerBabyBlueAddiction {
    private int babyBlueAddiction;
    private final int MIN_BABY_BLUE_ADDICTION = 0;
    private final int MAX_BABY_BLUE_ADDICTION = DestroyAllConfigs.COMMON.substances.maxAddictionLevel.get(); //default is 590

    public void copyFrom(PlayerBabyBlueAddiction source) {
        this.babyBlueAddiction = source.babyBlueAddiction;
    };

    public int getBabyBlueAddiction() {
        return this.babyBlueAddiction;
    };

    public void setBabyBlueAddiction(int babyBlueAddiction) {
        if (babyBlueAddiction < MIN_BABY_BLUE_ADDICTION) {
            this.babyBlueAddiction = MIN_BABY_BLUE_ADDICTION;
        } else if (babyBlueAddiction > MAX_BABY_BLUE_ADDICTION) {
            this.babyBlueAddiction = MAX_BABY_BLUE_ADDICTION;
        } else {
            this.babyBlueAddiction = babyBlueAddiction;
        };
    };

    public void addBabyBlueAddiction(int change) {
        this.babyBlueAddiction = Mth.clamp(this.babyBlueAddiction + change, MIN_BABY_BLUE_ADDICTION, MAX_BABY_BLUE_ADDICTION);
    };

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("BabyBlueAddiction", this.babyBlueAddiction);
    };

    public void loadNBTData(CompoundTag nbt) {
        this.babyBlueAddiction = nbt.getInt("BabyBlueAddiction");
    };
}
