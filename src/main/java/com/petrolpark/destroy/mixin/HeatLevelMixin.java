package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;

@Mixin(HeatLevel.class)
public abstract class HeatLevelMixin {
    
    @Shadow
    @Final
    @Mutable
    private static HeatLevel[] $VALUES;

    @SuppressWarnings("unused")
    private static final HeatLevel FROSTING = heatLevelModifier$addValue("FROSTING");

    @Invoker("<init>")
    public static HeatLevel heatLevelModifier$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    };

    private static HeatLevel heatLevelModifier$addValue(String internalName) {
        ArrayList<HeatLevel> heatLevels = new ArrayList<HeatLevel>(Arrays.asList(HeatLevelMixin.$VALUES));
        HeatLevel heatLevel = heatLevelModifier$invokeInit(internalName, heatLevels.get(heatLevels.size() - 1).ordinal() + 1);
        heatLevels.add(heatLevel);
        HeatLevelMixin.$VALUES = heatLevels.toArray(new HeatLevel[0]);
        return heatLevel;
    };
};
