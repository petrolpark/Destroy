package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;

//this is all pretty much copied from https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java

@Mixin(HeatCondition.class)
@Unique
public abstract class HeatConditionMixin {
    
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    private static final HeatCondition COOLED = heatConditionModifier$addValue("COOLED", 0xFFFFFF);

    @Invoker("<init>")
    public static HeatCondition heatConditionModifier$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    };

    private static HeatCondition heatConditionModifier$addValue(String internalName, int color) {
        ArrayList<HeatCondition> heatConditions = new ArrayList<HeatCondition>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heatCondition = heatConditionModifier$invokeInit(internalName, heatConditions.get(heatConditions.size() - 1).ordinal() + 1, color);
        heatConditions.add(heatCondition);
        HeatConditionMixin.$VALUES = heatConditions.toArray(new HeatCondition[0]);
        return heatCondition;
    };

    @Inject(method = "testBlazeBurner", at = @At("HEAD"), cancellable = true)
    public void testBlazeBurner(HeatLevel heatLevel, CallbackInfoReturnable<Boolean> ci) {
        HeatCondition thisHeatCondition = (HeatCondition) (Object) (this);
        if (heatLevel == HeatLevel.valueOf("FROSTING")) {
            ci.setReturnValue(thisHeatCondition == COOLED);
        };
    };



};
