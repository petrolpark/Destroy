package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;

@Mixin(HeatCondition.class)
@Unique
public abstract class HeatConditionMixin {
    
    @Shadow
    @Final
    @Mutable
    private static HeatCondition[] $VALUES;

    private static final HeatCondition COOLED = heatConditionModifier$addValue("COOLED", 0xD9FEFF);

    @Invoker("<init>")
    public static HeatCondition heatConditionModifier$invokeInit(String internalName, int internalId, int color) {
        throw new AssertionError();
    };

    /**
     * Creates a new entry in the {@link com.simibubi.create.content.contraptions.processing.HeatCondition HeatCondition enum}.
     * The technique is copied from <a href="https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java">here</a>.
     */
    private static HeatCondition heatConditionModifier$addValue(String internalName, int color) {
        ArrayList<HeatCondition> heatConditions = new ArrayList<HeatCondition>(Arrays.asList(HeatConditionMixin.$VALUES));
        HeatCondition heatCondition = heatConditionModifier$invokeInit(internalName, heatConditions.get(heatConditions.size() - 1).ordinal() + 1, color);
        heatConditions.add(heatCondition);
        HeatConditionMixin.$VALUES = heatConditions.toArray(new HeatCondition[0]);
        return heatCondition;
    };

    /**
     * Injection into {@link com.simibubi.create.content.contraptions.processing.HeatCondition#testBlazeBurner HeatCondition}.
     * This ensures that a {@link com.petrolpark.destroy.block.entity.CoolerBlockEntity Cooler} (or anything which is {@code FROSTING}) is required for Recipes with the {@code cooled} heat requirement.
     */
    @Inject(
        method = "Lcom/simibubi/create/content/processing/recipe/HeatCondition;testBlazeBurner(Lcom/simibubi/create/content/processing/burner/BlazeBurnerBlock$HeatLevel;)Z",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    public void inTestBlazeBurner(HeatLevel heatLevel, CallbackInfoReturnable<Boolean> ci) {
        HeatCondition thisHeatCondition = (HeatCondition) (Object) (this);
        if (thisHeatCondition == COOLED) {
            ci.setReturnValue(heatLevel.name().equals("FROSTING"));
        };
    };
};
