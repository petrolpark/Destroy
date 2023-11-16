package com.petrolpark.destroy.mixin;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.world.entity.player.Player;

@Mixin(HeartType.class)
public class HeartTypeMixin {
    
    @Shadow
    @Final
    @Mutable
    private static HeartType[] $VALUES;

    private static final HeartType CHEMICAL_POISON = heartTypeModifier$addValue("CHEMICAL_POISON", 0, true);

    @Invoker("<init>")
    public static HeartType heartTypeModifier$invokeInit(String internalName, int internalId, int index, boolean canBlink) {
        throw new AssertionError();
    };

     /**
     * Creates a new entry in the {@link net.minecraft.client.gui.Gui.HeartType HeartType enum}.
     * The technique is copied from <a href="https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java">here</a>.
     */
    private static HeartType heartTypeModifier$addValue(String internalName, int index, boolean canBlink) {
        ArrayList<HeartType> heartTypes = new ArrayList<HeartType>(Arrays.asList(HeartTypeMixin.$VALUES));
        HeartType heartType = heartTypeModifier$invokeInit(internalName, heartTypes.get(heartTypes.size() - 1).ordinal() + 1, index, canBlink);
        heartTypes.add(heartType);
        HeartTypeMixin.$VALUES = heartTypes.toArray(new HeartType[0]);
        return heartType;
    };

    @Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
    private static void inForPlayer(Player player, CallbackInfoReturnable<HeartType> cir) {
        if (player.hasEffect(DestroyMobEffects.CHEMICAL_POISON.get())) cir.setReturnValue(CHEMICAL_POISON);
    };
};
