package com.petrolpark.destroy.mixin.client;

import net.createmod.ponder.api.level.PonderLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.petrolpark.destroy.core.pollution.Pollution;
import com.petrolpark.destroy.core.pollution.SmogAffectedBlockColor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

@Mixin(targets = "net.minecraftforge.common.ForgeMod$2$1")
public class ForgeMod$2$1Mixin {
    
    @Overwrite(remap = false)
    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        if (getter instanceof PonderLevel ponder) return SmogAffectedBlockColor.getColor(0xFF3F76E4, ponder.getCapability(Pollution.CAPABILITY));
        return SmogAffectedBlockColor.getAverageWaterColor(getter, pos) | 0xFF000000;
    };
};
